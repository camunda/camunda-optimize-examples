package org.camunda.optimize.examples;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.camunda.optimize.plugin.importing.variable.DecisionInputImportAdapter;
import org.camunda.optimize.plugin.importing.variable.PluginDecisionInputDto;

import java.util.List;
import java.util.stream.Collectors;


public class FilterInputs implements DecisionInputImportAdapter {
  @Override
  public List<PluginDecisionInputDto> adaptInputs(List<PluginDecisionInputDto> list) {
    return list.stream()
      .filter(i -> {
        boolean validAbilityId = false;
        try {
          validAbilityId = isValidAbilityId(i);
        } catch (UnirestException e) {
          e.printStackTrace();
        }
        return validAbilityId;
      })
      .collect(
        Collectors.toList());
  }

  private boolean isValidAbilityId(PluginDecisionInputDto output) throws UnirestException {
    String abilityId = output.getValue();
    String pokemonUrl = "https://pokeapi.co/api/v2/ability/" + abilityId;
    HttpResponse<JsonNode> response = Unirest.get(pokemonUrl)
      .header("accept", "application/json")
      .asJson();

    return response.getStatus() == 200;
  }
}
