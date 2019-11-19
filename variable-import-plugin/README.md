# Optimize variable plugin

In Optimize you can [hook into the Optimize import](https://docs.camunda.org/optimize/latest/technical-guide/plugins/variable-import/) 
and adjust variable imports. In this directory you will find common use cases on why and how to use
those variable plugins.

To give you a better understanding of how variables are imported in Optimize, 
have a look at the following diagram:

![Variable Import][1]

Using the engine REST-API Optimize fetches all variables from the engine, excluding those 
that contain binary data (e.g. pdfs). This also includes complex 
variables like JSON, XML or Java object variables. Those variables are then 
passed through all variable plugins that are configured in Optimize. Optimize will 
import any primitive typed variables that exist after the plugin filtering. Any 
variables that are not primitive typed will be ignored. Finally, 
the primitive variables are persisted to Elasticsearch.

[1]: ../docs/optimize-variable-import.png
