package org.camunda.optimize.examples;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.camunda.optimize.plugin.importing.variable.PluginVariableDto;
import org.camunda.optimize.plugin.importing.variable.VariableImportAdapter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResolveReferenceVariables implements VariableImportAdapter {

  private static final String XKCD_ID = "xkcdID";

  public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> fetchedEngineVariables) {

    List<PluginVariableDto> xkcdIDVariables = fetchedEngineVariables
      .stream()
      .filter(var -> XKCD_ID.equals(var.getName()))
      .collect(Collectors.toList());

    for (PluginVariableDto xkcdIDVariable : xkcdIDVariables) {
      try {
        String xkcdTitle = getXKCDTitle(xkcdIDVariable);
        PluginVariableDto xkcdTitleVariable = createXKCDTitleVariable(xkcdIDVariable, xkcdTitle);
        fetchedEngineVariables.add(xkcdTitleVariable);
      } catch (UnirestException e) {
        e.printStackTrace();
      }
    }
    return fetchedEngineVariables;
  }

  private String getXKCDTitle(PluginVariableDto xkcdIDVariable) throws UnirestException {
    String xkcdId = xkcdIDVariable.getValue();
    String xkcdURL = String.format("https://xkcd.com/%s/info.0.json", xkcdId);
    HttpResponse<JsonNode> xkcdResponse = Unirest.get(xkcdURL)
      .header("accept", "application/json")
      .asJson();

    return xkcdResponse
      .getBody()
      .getObject()
      .getString("title");
  }

  private PluginVariableDto createXKCDTitleVariable(PluginVariableDto originalVariable, String title) {
    PluginVariableDto customerNameVar = new PluginVariableDto();
    customerNameVar.setEngineAlias(originalVariable.getEngineAlias());
    customerNameVar.setId(UUID.randomUUID().toString());
    customerNameVar.setProcessDefinitionId(originalVariable.getProcessDefinitionId());
    customerNameVar.setProcessDefinitionKey(originalVariable.getProcessDefinitionKey());
    customerNameVar.setProcessInstanceId(originalVariable.getProcessInstanceId());
    customerNameVar.setVersion(1L);
    customerNameVar.setName("xkcdTitle");
    customerNameVar.setType("String");
    customerNameVar.setValue(title);
    return customerNameVar;
  }
}
