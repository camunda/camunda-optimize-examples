# Optimize variable plugin - anonymize sensitive variables

This example demonstrates how to use an Optimize variable plugin to hook into the
Optimize import and anonymize variables that might contains sensitive data. 

After having read through the guide, you will understand

* how the plugin system in Optimize works.
* how to implement your own Optimize variable plugin.
* how to configure the plugin in Optimize.

What is the idea/use case of this demo:

* It should not be possible to track back data to a certain entity. For instance, 
you have data about customers and from legal perspective you are not allowed to
analyze single customers. However, you are rather interested in how good the 
overall process performs. In those cases, you can just anonymize the customers 
data and still analyze the process.

### How does it work?

To see how the import in Optimize works in general, have a look at the [documentation][4]. 
The details of the variable import and how the plugin hooks into that 
are explained in the [variable import plugin readme][5].

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
at [plugin setup][6] in the Optimize documentation.

### Implement the variable plugin

Now with the Optimize dependency added, the minimum requirements are set to
implement your own plugin. For that extend the 
`org.camunda.optimize.plugin.importing.variable.VariableImportAdapter` interface. In 
the given [example][2]:

```java
package org.camunda.optimize.examples;

// imports come here

public class AnonymizeSensitiveVariables implements VariableImportAdapter {

  private static final String CUSTOMER_NAME_VARIABLE = "customerName";

  private Map<String, String> anonymizationMapping = new HashMap<>();

  public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> list) {

    list
      .stream()
      .filter(var -> CUSTOMER_NAME_VARIABLE.equals(var.getName()))
      .forEach(this::anonymizeVariableValue);
    return list;
  }

  private void anonymizeVariableValue(PluginVariableDto variableDto) {
    String originalValue = variableDto.getValue();
    variableDto.setValue(
      anonymizationMapping.compute(originalValue, (k, v) -> v != null ? v : createRandomName())
    );
  }

  private String createRandomName() {
    return String.valueOf(UUID.randomUUID());
  }
}
```

This variable plugin searches for all variables with the name `customerName` and
replaces the value with a random new value. Thereby, variables with the same
customer name still get the same randomized value assigned. This ensures that
it is possible to fully analyze the whole data without knowing who actually the
customers was. Note, that this is just a simple example of how to anonymize data
to illustrate how it works. However, this might not be the right way to peform
proper anonymization.

### Build the plugin and add it to your Optimize distribution

Now run the following command to build the Jar containing the implemented plugin:

```cmd
mvn clean install
```

This creates a `anonymize-variables-1.0.0.jar` in `target` folder. Copy this
jar file into the `plugin` folder of your Optimize distribution.

Finally it is necessary to tell Optimize in which package it should search for the plugin. You 
could do that if you add the following lines to `environment-config.yaml` in the 
`environment` folder of your Optimize distribution:
```yaml
plugin:
  variableImport:
    basePackages: ['org.camunda.optimize.examples']
```

The package path must match the one which is written in the implemented java class of the plugin.

When Optimize is now started, it should automatically anonymize variables with 
the name `customerName`. All other variables are still imported to Optimize (if they are not complex or 
binary variables).

[1]: ../../docs/optimize-variable-import.png
[2]: src/main/java/org/camunda/optimize/examples/AnonymizeSensitiveVariables.java
[3]: pom.xml
[4]: https://docs.camunda.org/optimize/latest/technical-guide/import/import-overview/
[5]: ../README.md
[6]: https://docs.camunda.org/optimize/latest/technical-guide/plugins/#setup-your-environment