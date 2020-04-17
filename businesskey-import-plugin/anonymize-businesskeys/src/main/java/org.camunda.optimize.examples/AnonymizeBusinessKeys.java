package org.camunda.optimize.examples;

import org.camunda.optimize.plugin.importing.businesskey.BusinessKeyImportAdapter;

public class AnonymizeBusinessKeys implements BusinessKeyImportAdapter {

  public String adaptBusinessKey(String businessKey) {
    return String.valueOf(businessKey.hashCode());
  }
}
