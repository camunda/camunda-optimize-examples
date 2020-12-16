# Optimize businesskey plugin

In Optimize you can [hook into the Optimize import](https://docs.camunda.org/optimize/latest/technical-guide/plugins/businesskey-import/) 
and adjust business keys during process instance imports. In this directory you will find a use case example for this plugin.

To give you a better understanding of how process instances are imported in Optimize, 
have a look at the following diagram:

![Businesskey Import][1]

Using the engine REST-API Optimize fetches all process instances from the engine. These process instances are then 
passed through all business key adapter plugins that are configured in Optimize.
They are mapped to Optimize data structures and an import job is added to the queue. Once the job has been polled and executed,
the new entities are persisted to Elasticsearch. 

[1]: ./docs/process-instance-import.png