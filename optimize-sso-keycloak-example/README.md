# Camunda Optimize Keycloak SSO Example

This example demonstrates how you can set up SSO with Optimize and Keycloak.

The most important part from the Optimize perspective of this example is the custom Optimize SSO Plugin
[sso-plugin](../plugins/sso-plugin)
To find out more about the Optimize SSO Plugin mechanism, find the docs [here](https://docs.camunda.org/optimize/latest/technical-guide/plugins/single-sign-on/)

It includes a docker-compose with:
1. Camunda Optimize
2. ElasticSearch
3. Camunda Platform
2. Keycloak Authentication Server
3. Keycloak Proxy Server

The Keycloak Server has one user:

```
demo:password
```

# How to run?

## Clone the Repo

## Build the SSO-Plugin Plugin

Run `mvn clean package -pl plugins/sso-plugin -am` in the root of this repository

## Replace the the License

Put your Optimize license into `./optimize-config/OptimizeLicense.txt`

## Run docker-compose

1. Login to private Camunda Docker EE Registry with `docker login registry.camunda.cloud`, use your EE LDAP credentials to log in.
2. Start all images along with providing the Optimize version you want to run `OPTIMIZE_VERSION=3.6.0 docker-compose up -d`

## Open Optimize

Open `http://sso-proxy.localtest.me/` in a web browser and log in with `demo:password`

# Some more background

Keycloak is responsible for Authentication, so the users are stored in Keycloak and the Keycloak Proxy makes sure that only authenticated users can see Optimize.

In the Optimize Plugin we only read the authenticated user from the request header. If the user is in the request, we authenticate the user directly in Optimize.
For details please see [sso-plugin](../plugins/sso-plugin)