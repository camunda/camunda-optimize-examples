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

import java.time.Instant;
import java.util.Map;

public class MessageDto {
  private String id;
  private String messageType;
  private Instant timestamp;
  private String orderId;
  private Map<String, Object> payload;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(final String messageType) {
    this.messageType = messageType;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final Instant timestamp) {
    this.timestamp = timestamp;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(final String orderId) {
    this.orderId = orderId;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(final Map<String, Object> payload) {
    this.payload = payload;
  }
}
