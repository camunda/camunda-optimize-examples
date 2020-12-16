package org.camunda.optimize.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.optimize.plugin.importing.variable.PluginVariableDto;
import org.camunda.optimize.plugin.importing.variable.VariableImportAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransformComplexVariables implements VariableImportAdapter {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> list) {
    List<PluginVariableDto> resultList = new ArrayList<>();
    for (PluginVariableDto pluginVariableDto : list) {
      if (isCustomerVariable(pluginVariableDto)) {
        extractCustomerFieldsAndAddThemAsVariables(resultList, pluginVariableDto);
      } else {
        resultList.add(pluginVariableDto);
      }
    }
    return resultList;
  }

  private void extractCustomerFieldsAndAddThemAsVariables(List<PluginVariableDto> resultList,
                                                          PluginVariableDto pluginVariableDto) {
    try {
      Customer customer =
        objectMapper.readValue(pluginVariableDto.getValue(), Customer.class);
      resultList.add(createCustomerNameVariable(pluginVariableDto, customer));
      resultList.add(createCustomerIdVariable(pluginVariableDto, customer));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean isCustomerVariable(PluginVariableDto pluginVariableDto) {
    return pluginVariableDto.getName().equals("customer") &&
      pluginVariableDto.getValueInfo().get("objectTypeName").equals("org.camunda.optimize.examples.Customer") &&
      pluginVariableDto.getValueInfo().get("serializationDataFormat").equals("application/json");
  }

  private PluginVariableDto createCustomerNameVariable(PluginVariableDto originalVariable, Customer customer) {
    PluginVariableDto customerNameVar = new PluginVariableDto();
    customerNameVar.setEngineAlias(originalVariable.getEngineAlias());
    customerNameVar.setId(UUID.randomUUID().toString());
    customerNameVar.setProcessDefinitionId(originalVariable.getProcessDefinitionId());
    customerNameVar.setProcessDefinitionKey(originalVariable.getProcessDefinitionKey());
    customerNameVar.setProcessInstanceId(originalVariable.getProcessInstanceId());
    customerNameVar.setVersion(1L);
    customerNameVar.setName("customerName");
    customerNameVar.setType("String");
    customerNameVar.setValue(customer.getName());
    return customerNameVar;
  }

  private PluginVariableDto createCustomerIdVariable(PluginVariableDto originalVariable, Customer customer) {
    PluginVariableDto customerNameVar = new PluginVariableDto();
    customerNameVar.setEngineAlias(originalVariable.getEngineAlias());
    customerNameVar.setId(UUID.randomUUID().toString());
    customerNameVar.setProcessDefinitionId(originalVariable.getProcessDefinitionId());
    customerNameVar.setProcessDefinitionKey(originalVariable.getProcessDefinitionKey());
    customerNameVar.setProcessInstanceId(originalVariable.getProcessInstanceId());
    customerNameVar.setVersion(1L);
    customerNameVar.setName("customerId");
    customerNameVar.setType("String");
    customerNameVar.setValue(customer.getId());
    return customerNameVar;
  }

}
