package org.camunda.optimize.examples;

import org.camunda.optimize.plugin.importing.businesskey.BusinessKeyImportAdapter;

public class AnonymizeBusinessKeysByHashingAdapter implements BusinessKeyImportAdapter {

  public String adaptBusinessKey(String businessKey) {
    // This is a simple example of converting a businesskey to a hash,
    // it is recommended to use more secure hashing algorithms in practice
    return businessKey != null ? String.valueOf(businessKey.hashCode()) : null;
  }
}
