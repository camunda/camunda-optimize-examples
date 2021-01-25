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
demo:notdemo
```

# How to run?

## Clone the Repo

## Build the SSO-Plugin Plugin

Run `mvn clean package -pl plugins/sso-plugin -am` in the root of this repository

## Replace the the License

Put your Optimize license into `./optimize-config/OptimizeLicense.txt`

## Run docker-compose

1. Build all images with `docker-compose build`
2. Login to private Camunda Docker EE Registry with `docker login registry.camunda.cloud` Use your EE LDAP credentials to log in.
3. Start all images `docker-compose up -d`

## Open Optimize

Open `http://localhost:8095` in a web browser and log in with `demo:notdemo`

# Some more background

Keycloak is responsible for Authentication, so the users are stored in Keycloak and the Keycloak Proxy makes sure that only authenticated users can see Optimize.

In the Optimize Plugin we only read the authenticated user from the request header. If the user is in the request, we authenticate the user directly in Optimize.
For details please see [sso-plugin](../plugins/sso-plugin)