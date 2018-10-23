package org.camunda.optimize.examples;

import org.camunda.optimize.plugin.importing.variable.PluginVariableDto;
import org.camunda.optimize.plugin.importing.variable.VariableImportAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AnonymizeSensitiveVariables implements VariableImportAdapter {

  private static final String CUSTOMER_NAME_VARIABLE = "customerName";

  private Map<String, String> anonymizationMapping = new HashMap<>();

  public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> list) {

    list
      .stream()
      .filter(var -> CUSTOMER_NAME_VARIABLE.equals(var.getName()))
      .forEach(this::anonymizeVariableValue);
    return list;
  }

  private void anonymizeVariableValue(PluginVariableDto variableDto) {
    String originalValue = variableDto.getValue();
    variableDto.setValue(
      anonymizationMapping.compute(originalValue, (k, v) -> v != null ? v : createRandomName())
    );
  }

  private String createRandomName() {
    return String.valueOf(UUID.randomUUID());
  }
}
