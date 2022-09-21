# Optimize decision import plugin - resolve decision outputs that contain a reference to data somewhere else

This example demonstrates how to use an Optimize decision import plugin to hook into the
Optimize import and resolve variables that contain a reference to data that is stored
somewhere else. 

After having read through the guide, you will understand

* how the plugin system in Optimize works.
* how to implement your own Optimize decision import plugin.
* how to configure the plugin in Optimize.
* how to add custom/third-party dependencies to your plugin.

What is the idea/use case of this demo:

* In the Camunda Engine users often to not store the data in variables, but
have a variable store (e.g. a database) that contain all the necessary information.
In order to get proper data analisys in Optimize, you would need to resolve those references to the real values.

### How does it work?

To give you a better understanding of how decision instances are imported in Optimize, 
how you can resolve a references variable and store the information into Optimize,
have a look at the following diagram:

![Decision instance Import][1]

Using the engine REST-API, Optimize fetches all decision instances from the engine.
Then the decision input and output variables are passed through all the decision import plugins
that are configured in Optimize.
Every variable reference can now be resolved by fetching the data to that variable reference from
the external storage and add that to the variable stream. Then all variables are 
filtered in Optimize such that only imports primitive typed variables are imported.
Finally, the primitive variables including the variables you have added to the stream
are persisted to Elasticsearch.

### Setup your environment

First, add the Optimize plugin dependency to your project using mavens [pom.xml][3]:

```xml
<dependency>
  <groupId>org.camunda.optimize</groupId>
  <artifactId>plugin</artifactId>
  <version>${optimize.version}</version>
</dependency>
```
Note: It is important to use the same plugin version as the Optimize version you plan to use.
Optimize rejects plugins that are built with different Optimize versions to avoid compatibility problems.
This also means that to upgrade to newer Optimize versions it is necessary to build the plugin again with the new version.


To tell maven where to find the plugin environment, add the following repository to your project:

```xml
<repositories>
  <repository>
    <id>camunda-bpm-nexus</id>
    <name>camunda-bpm-nexus</name>
    <url>
      https://artifacts.camunda.com/artifactory/camunda-optimize/
    </url>
  </repository>
</repositories>
```

To package a third party library, add the library to the dependency:
```xml
<dependencies>
  <dependency>
    <groupId>com.mashape.unirest</groupId>
    <artifactId>unirest-java</artifactId>
    <version>1.4.9</version>
  </dependency>
</dependencies>
```

Then you also need to create an uber jar, which can be done with the following:
```xml
  <build>
    <defaultGoal>install</defaultGoal>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.1.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>single</goal>
            </goals>
            <configuration>
              <finalName>${project.artifactId}</finalName>
              <descriptorRefs>
                <descriptorRef>jar-with-dependencies</descriptorRef>
              </descriptorRefs>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <artifactId>maven-jar-plugin</artifactId>
        <version>3.1.2</version>
        <executions>
          <execution>
            <id>default-jar</id>
            <phase>none</phase>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
Note: For further information why it is necessary to build the plugin as a uber jar you can have a look
at [plugin setup][5] in the Optimize documentation.

### Implement the decision import plugin

Let's say you have a situation where you need to use some of the Pokémon abilities.
Depending on the ability, a decision definition decides which Pokémon needs to be summoned.
So, as a decision output you get an Pokémon ID, which is not really useful for the data analysis, as you want to see 
the actual names of the summoned Pokémons. In this example we will resolve the Pokémon ID to it's name using the pokeapi.co.

Now the only thing to do is to implement your own Optimize plugin and fetch the 
Pokémon data for each of the decision outputs. For that implement the 
`org.camunda.optimize.plugin.importing.variable.DecisionOutputImportAdapter` interface. In 
the given [example][2]:

```java
package org.camunda.optimize.examples;

// imports here

public class ResolveReferenceOutputs implements DecisionOutputImportAdapter {
  @Override
  public List<PluginDecisionOutputDto> adaptOutputs(List<PluginDecisionOutputDto> list) {
    for (PluginDecisionOutputDto output: list) {
      if (output.getVariableName().equals("pokemonId")) {
        try {
          String name = resolvePokemonName(output);
          output.setValue(name);
        } catch (UnirestException e) {
          e.printStackTrace();
        }
      }
    }
    return list;
  }

  private String resolvePokemonName(PluginDecisionOutputDto output) throws UnirestException {
    String pokemonId = output.getValue();
    String pokemonUrl = "https://pokeapi.co/api/v2/pokemon/" + pokemonId;
    HttpResponse<JsonNode> response = Unirest.get(pokemonUrl)
      .header("accept", "application/json")
      .asJson();

    return response
      .getBody()
      .getObject()
      .getString("name");
  }
}
```

### Build the plugin and add it to your Optimize distribution

Now run the following command to build the Jar containing the implemented plugin:

```cmd
mvn clean install
```

This creates a `resolve-reference-outputs-jar-with-dependencies.jar` 
in `target` folder. Copy this jar file into the `plugin` folder of your 
Optimize distribution.

Finally it is necessary to tell Optimize in which package it should search for the plugin. You 
could do that if you add the following lines to `environment-config.yaml` in the 
`config` folder of your Optimize distribution:
```yaml
plugin:
  decisionOutputImport:
    basePackages: ['org.camunda.optimize.examples']
```

The package path must match the one which is written in the implemented java class of the plugin.

When Optimize is now started, it should automatically resolve all your Pokémon ids and 
set the variables' values which contain the Pokémon names. All other output instances of simple types are still imported to 
Optimize.

[1]: ./docs/resolve-input-references.png
[2]: src/main/java/org/camunda/optimize/examples/ResolveReferenceOutputs.java
[3]: pom.xml
[4]: https://xkcd.com/
[5]: https://docs.camunda.io/docs/self-managed/optimize-deployment/plugins/plugin-system/#setup-your-environment
