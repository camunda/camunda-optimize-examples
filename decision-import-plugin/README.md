# Optimize decision import plugin

In Optimize you can [hook into the Optimize import](https://docs.camunda.org/optimize/latest/technical-guide/plugins/decision-import/) 
and adjust the decision instance import. In this directory you will find example implementations of decision import plugins.

To give you a better understanding of how variables are imported in Optimize, 
have a look at the following diagram:

![Variable Import][1]

Using the engine REST-API Optimize fetches all the decision instances with the belonging input and output variables.
This also includes complex variables like JSON, XML or Java object variables. Then those variables are 
passed through all the decision import plugins that are configured in Optimize. All variables 
that are still available after the plugins are filtered again such that Optimize
only imports primitive typed variables. Finally, the primitive variables are 
persisted to Elasticsearch.

[1]: ../docs/optimize-variable-import.png
