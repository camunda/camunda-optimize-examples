# Optimize variable plugin - resolve variables that contain a reference to data somewhere else

This example demonstrates how to use an Optimize variable plugin to hook into the
Optimize import and resolve variables that contain a reference to data that is stored
somewhere else. 

After having read through the guide, you will understand

* how the plugin system in Optimize works.
* how to implement your own Optimize variable plugin.
* how to configure the plugin in Optimize.
* how to add custom/third-party dependencies to your plugin.

What is the idea/use case of this demo:

* In the Camunda Engine users often to not store the data in variables, but
have a variable store (e.g. a database) that contain all the necessary information.
During process execution the variables just contain a reference to those variables
in the variable store.

### How does it work?

To give you a better understanding of how variables are imported in Optimize, 
how you can resolve a references variable and store the information into Optimize
have a look at the following diagram:

![Variable Import][1]

Using the engine REST-API Optimize fetches all variable, but the variables 
that contain binary data (e.g. pdfs) from the engine. This also includes complex 
variables like JSON, XML oder Java object variables. Then those variables are 
passed through all variable plugins that are configured in Optimize. Every variable
reference can now be resolved by fetching the data to that variable reference from
the variable store and add that to the variable stream. Then all variables are 
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
      https://app.camunda.com/nexus/content/repositories/camunda-optimize
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

### Implement the variable plugin

In this example we assume that you have business process where you want to track
the happiness of the companies developers. For that developers fill out a survey
and state which [XKCD comic][4] is their favorite one. Now you want to use Optimize
to find out a correlation between XKCD comics and developer happiness. The problem
is that during the process execution only the id to the comic is stored and therefore
a correlation analysis would not be very meaningful. To solve this, you can fetch
the title of comic using the XKCD Rest-API. In this case the XKCD database would
be the variable store that you tap to retrieve the missing information.

Now the only thing to do is to implement your own Optimize plugin and fetch the
comic title for each variable that contains a comic id. To do this, you need to extend the 
`org.camunda.optimize.plugin.importing.variable.VariableImportAdapter` interface. In 
the given [example][2]:

```java
package org.camunda.optimize.examples;

// imports come here

public class ResolveReferenceVariables implements VariableImportAdapter {

  private static final String XKCD_ID = "xkcdID";

  public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> fetchedEngineVariables) {

    List<PluginVariableDto> xkcdIDVariables = fetchedEngineVariables
      .stream()
      .filter(var -> XKCD_ID.equals(var.getName()))
      .collect(Collectors.toList());

    for (PluginVariableDto xkcdIDVariable : xkcdIDVariables) {
      try {
        String xkcdTitle = getXKCDTitle(xkcdIDVariable);
        PluginVariableDto xkcdTitleVariable = createXKCDTitleVariable(xkcdIDVariable, xkcdTitle);
        fetchedEngineVariables.add(xkcdTitleVariable);
      } catch (UnirestException e) {
        e.printStackTrace();
      }
    }
    return fetchedEngineVariables;
  }

  private String getXKCDTitle(PluginVariableDto xkcdIDVariable) throws UnirestException {
    String xkcdId = xkcdIDVariable.getValue();
    String xkcdURL = String.format("https://xkcd.com/%s/info.0.json", xkcdId);
    HttpResponse<JsonNode> xkcdResponse = Unirest.get(xkcdURL)
      .header("accept", "application/json")
      .asJson();

    return xkcdResponse
      .getBody()
      .getObject()
      .getString("title");
  }

  private PluginVariableDto createXKCDTitleVariable(PluginVariableDto originalVariable, String title) {
    PluginVariableDto customerNameVar = new PluginVariableDto();
    customerNameVar.setEngineAlias(originalVariable.getEngineAlias());
    customerNameVar.setId(UUID.randomUUID().toString());
    customerNameVar.setProcessDefinitionId(originalVariable.getProcessDefinitionId());
    customerNameVar.setProcessDefinitionKey(originalVariable.getProcessDefinitionKey());
    customerNameVar.setProcessInstanceId(originalVariable.getProcessInstanceId());
    customerNameVar.setVersion(1L);
    customerNameVar.setName("xkcdTitle");
    customerNameVar.setType("String");
    customerNameVar.setValue(title);
    return customerNameVar;
  }
}
```

### Build the plugin and add it to your Optimize distribution

Now run the following command to build the Jar containing the implemented plugin:

```cmd
mvn clean install
```

This creates a `resolve-reference-variables-jar-with-dependencies.jar` 
in `target` folder. Copy this jar file into the `plugin` folder of your 
Optimize distribution.

Finally it is necessary to tell Optimize in which package it should search for the plugin. You 
could do that if you add the following lines to `environment-config.yaml` in the 
`config` folder of your Optimize distribution:
```yaml
plugin:
  variableImport:
    basePackages: ['org.camunda.optimize.examples']
```

The package path must match the one which is written in the implemented java class of the plugin.

When Optimize is now started, it should automatically resolve all your xkcd comic ids and 
add new variables which contain the xkcd title. All other variables are still imported to 
Optimize (if they are not complex or binary variables).

[1]: docs/resolve-variable-references.png
[2]: src/main/java/org/camunda/optimize/examples/ResolveReferenceVariables.java
[3]: pom.xml
[4]: https://xkcd.com/
[5]: https://docs.camunda.org/optimize/latest/technical-guide/plugins/#setup-your-environment
