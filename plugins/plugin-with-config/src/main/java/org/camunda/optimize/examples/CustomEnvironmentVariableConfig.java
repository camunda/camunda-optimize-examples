package org.camunda.optimize.examples;

import com.jayway.jsonpath.ReadContext;
import org.camunda.optimize.plugin.engine.rest.EngineRestFilter;
import org.camunda.optimize.service.exceptions.OptimizeConfigurationException;
import org.camunda.optimize.service.util.configuration.ConfigurationParser;

import javax.ws.rs.client.ClientRequestContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.camunda.optimize.service.util.configuration.ConfigurationUtil.getLocationsAsInputStream;

public class CustomEnvironmentVariableConfig implements EngineRestFilter {

  private final String customToken;

  public CustomEnvironmentVariableConfig() {

    List<InputStream> configStreams = getLocationsAsInputStream(new String[] { "custom-config.yaml" });
    ReadContext configJsonContext = ConfigurationParser.parseConfigFromLocations(configStreams)
      .orElseThrow(() -> new OptimizeConfigurationException("No single configuration source could be read"));
    customToken = configJsonContext.read("customToken");
  }

  @Override
  public void filter(ClientRequestContext requestContext, String engineAlias, String engineName) throws IOException {
    requestContext.getHeaders().add("Custom-Token", customToken);
    System.out.println("The value of the custom token is: " + customToken);
  }
}