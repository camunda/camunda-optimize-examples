# Acknowledgement

This plugin was originally written by Jan Rohwer and the original can be found at Github as [optimize-plugin-variable-flattener](https://github.com/janhuddel/optimize-plugin-variable-flattener).
We like to thank him and [Provinzial Versicherungen](https://www.provinzial.de/export/sites/pvn/verteilerseite/index.html) 
for publishing his work and allowing us to include it into this example repository!

# Optimize Generic Complex Variable Flattener Plugin (JSON) - flatten generic complex variables to primitive ones

This example demonstrates how to use an Optimize variable plugin to hook into the
Optimize import and flatten generic complex variables serialized as JSON to primitive variables that
can be used in Optimize Reports.

After having read through the guide, you will understand

* how the plugin system in Optimize works.
* how to implement your own Optimize variable plugin.
* how to configure the plugin in Optimize.

What is the idea/use case of this example:

* You have variables of complex data types serialized as JSON, e.g.:
  ```
  {
   "firstName": "John",
   "lastName": "Doe"
  }
  ```
  and want to analyse these variable in Optimize as `user.firstName` & `user.lastName`.

### How does it work?

To see how the import in Optimize works in general, have a look at the [documentation][2].
The details of the variable import and how the plugin hooks into that
are explained in the [variable import plugin readme][3].

### Setup your environment

First, add the Optimize plugin dependency to your project using mavens [pom.xml][4]:

```xml
<dependency>
  <groupId>org.camunda.optimize</groupId>
  <artifactId>plugin</artifactId>
  <version>${optimize.version}</version>
</dependency>
```

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
Note: For further information why it is necessary to build the plugin as an uber jar you can have a look
at [plugin setup][4] in the Optimize documentation.

### Implement the variable plugin

In this example we assume that you have a business process where you interact with users, 
e.g. handling customer support. Each user is stored as a JSON variable during the process execution. 
Optimize would be the perfect tool for analyzing how much time is needed to support a certain user. 
But since the user data is stored in a complex JSON variable, Optimize is not able to analyze it out of the box. 
However, we can use the Optimize plugin system to hook into the import and transform each complex user variable 
to a set of primitive variables.

For that extend the
`org.camunda.optimize.plugin.importing.variable.VariableImportAdapter` interface. In
the given [example][1]:

```java
package org.camunda.optimize.examples;

// imports come here

public class ComplexVariableFlattener implements VariableImportAdapter {
  
  /*
   some fields
  */

  @Override
  public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> variables) {
    List<PluginVariableDto> resultList = new ArrayList<>();
    for (PluginVariableDto pluginVariableDto : variables) {
      logger.debug("adapting variable {} (v{}) of process-instance {}...", //
                   pluginVariableDto.getName(), //
                   pluginVariableDto.getVersion(), //
                   pluginVariableDto.getProcessInstanceId());
      if (pluginVariableDto.getType().equalsIgnoreCase("object")) {
        String serializationDataFormat = String.valueOf(pluginVariableDto.getValueInfo().get("serializationDataFormat"));
        if (serializationDataFormat.equals("application/json")) {
          this.flatJsonObject(pluginVariableDto, resultList);
        } else {
          logger.warn("complex variable '{}' won't be imported (unsupported serializationDataFormat: {})",
                      pluginVariableDto.getName(), serializationDataFormat);
        }
      } else {
        resultList.add(pluginVariableDto);
      }
    }
    return resultList;
  }

  private void flatJsonObject(PluginVariableDto variable, List<PluginVariableDto> resultList) {
    if (variable.getValue() == null || variable.getValue().isEmpty()) {
      return;
    }

    try {
      new JsonFlattener(variable.getValue()) //
        .withFlattenMode(FlattenMode.KEEP_ARRAYS) //
        .flattenAsMap() //
        .entrySet() //
        .stream() //
        .map(e -> this.map(e.getKey(), e.getValue(), variable)) //
        .filter(Optional::isPresent) //
        .map(Optional::get) //
        .forEach(resultList::add);
    } catch (Throwable t) {
      logger.error("error while flattening variable '" + variable.getName() + "')", t);
    }
  }
  
  /*
   additional mapping helper methods
  */

}
```

This code would flatten any JSON object passed to it by converting each field to a dot-separated primitive variable.
So the following user object:
  ```
  {
   "firstName": "John",
   "lastName": "Doe"
  }
  ```
will get transformed to two primitive variables of the type String: `user.firstName: "John"` & `user.lastName: "Doe"`.

### Build the plugin and add it to your Optimize distribution

Now run the following command to build the Jar containing the implemented plugin:

```cmd
mvn clean install
```

This creates a `generic-complex-variable-flattener-X.X.X.jar` in `target` folder. Copy this
jar file into the `plugin` folder of your Optimize distribution.

Finally it is necessary to tell Optimize in which package it should search for the plugin. You
could do that if you add the following lines to `environment-config.yaml` in the
`config` folder of your Optimize distribution:

```yaml
plugin:
  variableImport:
    basePackages: ['org.camunda.optimize.examples;']
```

The package path must match the one that is written in the implemented java class of the plugin.

[1]: src/main/java/de/janhuddel/bpm/optimize/plugin/ComplexVariableFlattener.java
[2]: https://docs.camunda.org/optimize/latest/technical-guide/import/import-overview/
[3]: ../README.md
[4]: https://docs.camunda.org/optimize/latest/technical-guide/plugins/#setup-your-environment