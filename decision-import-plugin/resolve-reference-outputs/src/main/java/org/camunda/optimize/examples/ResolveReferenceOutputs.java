package org.camunda.optimize.examples;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.camunda.optimize.plugin.importing.variable.DecisionOutputImportAdapter;
import org.camunda.optimize.plugin.importing.variable.PluginDecisionOutputDto;

import java.util.List;

public class ResolveReferenceOutputs implements DecisionOutputImportAdapter {
  @Override
  public List<PluginDecisionOutputDto> adaptOutputs(List<PluginDecisionOutputDto> list) {
    for (PluginDecisionOutputDto output: list) {
      if (output.getVariableName().equals("pokemonid")) {
        try {
          String name = resolvePokemonName(output);
          output.setValue(name);
        } catch (UnirestException e) {
          e.printStackTrace();
        }
      }
    }
    return list;
  }

  private String resolvePokemonName(PluginDecisionOutputDto output) throws UnirestException {
    String pokemonId = output.getValue();
    String pokemonUrl = "https://pokeapi.co/api/v2/pokemon/" + pokemonId;
    HttpResponse<JsonNode> response = Unirest.get(pokemonUrl)
      .header("accept", "application/json")
      .asJson();

    return response
      .getBody()
      .getObject()
      .getString("name");
  }
}
