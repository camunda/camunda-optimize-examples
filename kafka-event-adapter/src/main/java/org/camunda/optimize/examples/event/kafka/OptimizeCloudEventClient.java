/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.optimize.examples.event.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.json.Json;
import io.cloudevents.v1.AttributesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;

@Component
public class OptimizeCloudEventClient {

  private static final Logger log = LoggerFactory.getLogger(OptimizeCloudEventClient.class);

  private final RestTemplate rest;

  private String optimizeIngestionEndpoint;
  private String optimizeIngestionAccessToken;

  @Autowired
  public OptimizeCloudEventClient(
    @Value("${camunda.optimize.ingestion.endpoint:http://localhost:8090/api/ingestion/event/batch}") final String optimizeIngestionEndpoint,
    @Value("${camunda.optimize.ingestion.accessToken}") final String optimizeIngestionAccessToken,
    final RestTemplate rest) {
    this.optimizeIngestionEndpoint = optimizeIngestionEndpoint;
    this.optimizeIngestionAccessToken = optimizeIngestionAccessToken;
    this.rest = rest;
  }

  public <T> void sendCloudEventsToOptimize(final List<CloudEvent<AttributesImpl, T>> cloudEvents) {
    log.debug("Try to ingest {} events into Optimize", cloudEvents.size());

    // Use Optimize Event Ingestion API
    // see https://docs.camunda.org/optimize/latest/technical-guide/event-ingestion-rest-api/
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(HttpHeaders.AUTHORIZATION, optimizeIngestionAccessToken);
    try {
      final HttpEntity<String> request = new HttpEntity<>(Json.encode(cloudEvents), headers);
      final ResponseEntity<String> response = rest.postForEntity(optimizeIngestionEndpoint, request, String.class);

      if (response.getStatusCodeValue() == 204) {
        log.debug("Ingested {} events into Optimize", cloudEvents.size());
      } else {
        final String errorMessage = MessageFormat.format(
          "Ingestion API failure when ingesting events to Optimize, Response Status Code: [{0}]",
          response.getStatusCodeValue()
        );
        log.error(errorMessage);
        throw new RuntimeException(errorMessage);
      }
    } catch (final Exception exception) {
      log.error("Could not ingest {} events into Optimize", cloudEvents.size(), exception);
      throw new RuntimeException("Failed ingesting events to Optimize", exception);
    }
  }
}
