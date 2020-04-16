package org.camunda.optimize.examples;

import org.camunda.optimize.plugin.importing.businesskey.ProcessInstancePluginDto;
import org.camunda.optimize.plugin.importing.businesskey.BusinessKeyImportAdapter;
import java.util.List;

public class AnonymizeBusinessKeys implements BusinessKeyImportAdapter {

  public List<ProcessInstancePluginDto> adaptBusinessKeys(List<ProcessInstancePluginDto> processInstances) {
    processInstances.stream()
      .forEach(this::anonymizeBusinessKey);
    return list;
  }

  private void anonymizeBusinessKey(ProcessInstancePluginDto processInstance) {
    final int anonymizedBusinessKey = processInstance.getBusinessKey().hashCode();
    processInstance.setBusinessKey(String.valueOf(anonymizedBusinessKey));
  }
}
