package org.camunda.optimize.examples.event.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.extensions.InMemoryFormat;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableBinding(Sink.class)
public class BatchMessageSinkToOptimizeIngestListener {

  private static final URI CLOUD_EVENT_SOURCE = URI.create("/kafka/myEvents");
  private static final String CLOUD_EVENTS_EXTENSION_GROUP_VALUE = "order-process";

  private final OptimizeCloudEventClient optimizeCloudEventClient;

  @Autowired
  public BatchMessageSinkToOptimizeIngestListener(final OptimizeCloudEventClient optimizeCloudEventClient) {
    this.optimizeCloudEventClient = optimizeCloudEventClient;
  }

  @StreamListener(target = Sink.INPUT)
  public void handleBatch(final List<MessageDto> messageBatch) {
    optimizeCloudEventClient.sendCloudEventsToOptimize(mapToCloudEvents(messageBatch));
  }

  private List<CloudEvent<AttributesImpl, Map<String, Object>>> mapToCloudEvents(final List<MessageDto> messageBatch) {
    return messageBatch.stream()
      .map(
        messageDto -> CloudEventBuilder.<Map<String, Object>>builder()
          .withId(messageDto.getId())
          .withSource(CLOUD_EVENT_SOURCE)
          .withType(messageDto.getMessageType())
          .withData(messageDto.getPayload())
          .withTime(ZonedDateTime.ofInstant(messageDto.getTimestamp(), ZoneId.of("UTC")))
          .withExtension(
            ExtensionFormat.of(InMemoryFormat.of("traceid", messageDto.getOrderId(), String.class))
          )
          .withExtension(
            ExtensionFormat.of(InMemoryFormat.of("group", CLOUD_EVENTS_EXTENSION_GROUP_VALUE, String.class))
          )
          .build()
      )
      .collect(Collectors.toList());
  }

}
