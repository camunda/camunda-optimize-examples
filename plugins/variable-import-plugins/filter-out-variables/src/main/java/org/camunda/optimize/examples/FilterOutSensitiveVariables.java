package org.camunda.optimize.examples;

import org.camunda.optimize.plugin.importing.variable.PluginVariableDto;
import org.camunda.optimize.plugin.importing.variable.VariableImportAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterOutSensitiveVariables implements VariableImportAdapter {

  private static final Set<String> sensitiveVariables = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList("customerName", "customerId"))
  );


  public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> list) {

    return list
      .stream()
      .filter(var -> !sensitiveVariables.contains(var.getName()))
      .collect(Collectors.toList());
  }
}
