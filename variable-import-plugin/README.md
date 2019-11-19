# Optimize variable plugin

In Optimize you can [hook into the Optimize import](https://docs.camunda.org/optimize/latest/technical-guide/plugins/variable-import/) 
and adjust variable import. In this directory you will find common use cases on why and how to use
those variable plugins.

To give you a better understanding of how variables are imported in Optimize, 
have a look at the following diagram:

![Variable Import][1]

Using the engine REST-API Optimize fetches all variable, but the variables 
that contain binary data (e.g. pdfs) from the engine. This also includes complex 
variables like JSON, XML or Java object variables. Then those variables are 
passed through all variable plugins that are configured in Optimize. All variables 
that are still available after the plugins are filtered again such that Optimize
only imports primitive typed variables. Finally, the primitive variables are 
persisted to Elasticsearch.

[1]: ../docs/optimize-variable-import.png
