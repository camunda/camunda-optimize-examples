package org.camunda.optimize.examples;

import org.apache.commons.lang3.RandomUtils;
import org.camunda.optimize.plugin.elasticsearch.CustomHeader;
import org.camunda.optimize.plugin.elasticsearch.ElasticsearchCustomHeaderSupplier;

import java.util.UUID;

public class AddAuthorizationHeaderPlugin implements ElasticsearchCustomHeaderSupplier {

  private String currentToken;

  public CustomHeader getElasticsearchCustomHeader() {
    if (currentToken == null || isTokenExpired()) {
      currentToken = fetchNewToken();
    }
    return new CustomHeader("Authorization", currentToken);
  }

  private String fetchNewToken() {
    return UUID.randomUUID().toString();
  }

  private boolean isTokenExpired() {
    return RandomUtils.nextBoolean();
  }

}
