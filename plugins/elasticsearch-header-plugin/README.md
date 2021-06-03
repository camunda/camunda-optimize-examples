# Optimize Elasticsearch Header plugin

In Optimize you can hook into the Optimize Elasticsearch client, allowing you to add custom headers to all requests made 
to Elasticsearch. The plugin is invoked before every request to Elasticsearch is made, allowing different
headers and values to be added per request. In this directory you will find a use case example for this plugin.

The example plugin stores a token, which will be added to each and every Elasticsearch request made by Optimize as an
`Authorization` header. In addition, before adding the header, the plugin checks the validity of the current token it stores, 
and replaces it with a new token if it has expired. In this example, the validity of the token is simulated using a randomly generated 
boolean and the token is a randomly generated `String`.