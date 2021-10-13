Camunda Optimize examples
====================

A collection of usage examples for Camunda Optimize intended to get you started quickly (e.g. with the plugin system).


| Optimize Version | Link                                                                         | Checkout command      |
| -----------------|------------------------------------------------------------------------------|-----------------------|
| Latest           | [Master branch](https://github.com/camunda/camunda-optimize-examples)        | `git checkout master` |
| 3.6.0              | [3.6.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/3.6.0)   | `git checkout 3.6.0`  |
| 3.5.0            | [3.5.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/3.5.0) | `git checkout 3.5.0`  |
| 3.4.0            | [3.4.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/3.4.0) | `git checkout 3.4.0`  |
| 3.3.0            | [3.3.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/3.3.0) | `git checkout 3.3.0`  |
| 3.2.0            | [3.2.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/3.2.0) | `git checkout 3.2.0`  |
| 3.1.0            | [3.1.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/3.1.0) | `git checkout 3.1.0`  |
| 3.0.0            | [3.0.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/3.0.0) | `git checkout 3.0.0`  |
| 2.7.0            | [2.7.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/2.7.0) | `git checkout 2.7.0`  |
| 2.6.0            | [2.6.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/2.6.0) | `git checkout 2.6.0`  |
| 2.5.0            | [2.5.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/2.5.0) | `git checkout 2.5.0`  |
| 2.4.0            | [2.4.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/2.4.0) | `git checkout 2.4.0`  |
| 2.3.0            | [2.3.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/2.3.0) | `git checkout 2.3.0`  |
| 2.2.0            | [2.2.0 tag](https://github.com/camunda/camunda-optimize-examples/tree/2.2.0) | `git checkout 2.2.0`  |

If you clone this repository, use the checkout commands to access the sources for the desired version.

## Overview

* [Getting Started with Variable Import Plugins](#getting-started-with-variable-import-plugins)
* [Getting Started with Decision Import Plugins](#getting-started-with-decision-import-plugins)
* [Getting Started with Business Key Import Plugins](#getting-started-with-business-key-import-plugins)
* [Getting Started with SSO Plugins](#getting-started-with-sso-plugins)
* [Getting Started with Elasticsearch Header Plugins](#getting-started-with-elasticsearch-header-plugins)

### Getting Started with Variable Import Plugins

In Optimize you can [hook into the Optimize import](https://docs.camunda.org/optimize/latest/technical-guide/plugins/variable-import/)
and adjust variable import. In the following you will find common use cases on why and how to use
those variable plugins:

| Name                                                                                                   | Support from Version | Keywords                |
| -------------------------------------------------------------------------------------------------------|----------------------|-------------------------|
| [Anonymize your Variables](plugins/variable-import-plugins/anonymize-variables)                                 | 2.1.0+               | variable, plugin, import|
| [Filter out Variables](plugins/variable-import-plugins/filter-out-variables)                                    | 2.1.0+               | variable, plugin, import|
| [Resolve reference variables](plugins/variable-import-plugins/resolve-reference-variables)                      | 2.1.0+               | variable, plugin, import|
| [Transform complex variables](plugins/variable-import-plugins/transform-complex-variables)                      | 2.2.0+               | variable, plugin, import|

### Getting Started with Decision Import Plugins

Analogously to the variable import plugins, you can hook into the decision instance import to enrich, filter or modify the input and output decision variables. [More information here.](https://docs.camunda.org/optimize/latest/technical-guide/plugins/decision-import/)

| Name                                                                                                   | Support from Version | Keywords                |
| -------------------------------------------------------------------------------------------------------|----------------------|-------------------------|
| [Filter out Input Instances](plugins/decision-import-plugins/filter-decision-inputs)                                 | 2.4.0+               | decision, plugin, import|
| [Resolve Reference Outputs](plugins/decision-import-plugins/resolve-reference-outputs)                                    | 2.4.0+               | decision, plugin, import|

### Getting Started with Business Key Import Plugins

You can [hook into the Optimize import](https://docs.camunda.org/optimize/latest/technical-guide/plugins/businesskey-import/)
and adjust business keys during the process instance import. In the following you can find a common use case on why and how to use
this business key plugin:

| Name                                                                                                   | Support from Version | Keywords                |
| -------------------------------------------------------------------------------------------------------|----------------------|-------------------------|
| [Anonymize Business Keys](plugins/businesskey-import-plugin)                             | 3.1.0+               | businesskey, plugin, import|


### Getting Started with SSO Plugins

In Optimize you can [hook into the Optimize authentication](https://docs.camunda.org/optimize/latest/technical-guide/plugins/single-sign-on/). In the following you will find common use cases on why and how to use
those SSO plugins:

| Name                                                                                                   | Support from Version | Keywords                |
| -------------------------------------------------------------------------------------------------------|----------------------|-------------------------|
| [Keycloak Example](optimize-sso-keycloak-example)                                 | 2.3.0+               | sso, plugin, keycloak|

### Getting Started with Elasticsearch Header Plugins

In Optimize you can [hook into Optimize Elasticsearch Requests](https://docs.camunda.org/optimize/latest/technical-guide/plugins/elasticsearch-header/). In the following you will find common use cases on why and how to use
this Elasticsearch Header plugin plugins:

| Name                                                                                                   | Support from Version | Keywords                |
| -------------------------------------------------------------------------------------------------------|----------------------|-------------------------|
| [Authorization Header Example](plugins/elasticsearch-header-plugin)                                 | 3.5.0+               | elasticsearch, plugin, token, header|


### Contribute!

  * Website: http://www.camunda.org/
  * Getting Started: https://docs.camunda.org/optimize/latest/technical-guide/setup/installation/
  * Issue Tracker: https://app.camunda.com/jira
  * Contribution Guidelines: http://www.camunda.org/community/contribute.html
  * License: Apache License, Version 2.0  http://www.apache.org/licenses/LICENSE-2.0
