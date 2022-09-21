# Optimize Event Ingestion Kafka Adapter

In Optimize you can [ingest event mesages from external systems](https://docs.camunda.io/docs/apis-clients/optimize-api/event-ingestion/)
that can be used to create [Event Based Processes](https://docs.camunda.io/optimize/userguide/additional-features/event-based-processes/)

The following diagram should give you a better understanding of how this adapter integrates with Optimize:

![Event Ingestion Architecture][1]

In this diagram there are 5 imaginary microservices connected to Kafka that publish business relevant messages onto it.
The payload of those messages correspond to the [`org.camunda.optimize.examples.event.kafka.MessageDto`][2] class present in this example.

```java
public class MessageDto {
  // globally unique id of that message
  private String id;
  // Type of the message/event, e.g. `OrderReceived`
  private String messageType;
  // The timestamp when this message/event was created.
  private Instant timestamp;
  // The order id this message relates to.
  private String orderId;
  // Map with a generic payload, e.g. `customerId: C101`.
  private Map<String, Object> payload;
  //...
}
```

## Kafka Message Listener
These messages are received and handled by the [`org.camunda.optimize.examples.event.kafka.BatchMessageSinkToOptimizeIngestListener`][3] Kafka listener implementation.
This listener is based on [spring-cloud-stream-binder-kafka](https://cloud.spring.io/spring-cloud-stream-binder-kafka/spring-cloud-stream-binder-kafka.html#kafka-consumer-properties)
in order to connect and receive kafka messages in batches from the `flowing-retail` topic configured by `spring.cloud.stream.bindings.input.destination: flowing-retail`.

These messages are then mapped to correspond to the [CloudEvents v1.0 Specification](https://github.com/cloudevents/spec)
using the [CloudEvents Java SDK](https://github.com/cloudevents/sdk-java).

```java
@Component
@EnableBinding(Sink.class)
public class BatchMessageSinkToOptimizeIngestListener {

  private static final URI CLOUD_EVENT_SOURCE = URI.create("/kafka/myEvents");
  private static final String CLOUD_EVENTS_EXTENSION_GROUP_VALUE = "order-process";

  // ...

  private List<CloudEvent<AttributesImpl, Map<String, Object>>> mapToCloudEvents(final List<MessageDto> messageBatch) {
    return messageBatch.stream()
      .map(
        messageDto -> CloudEventBuilder.<Map<String, Object>>builder()
          .withId(messageDto.getId())
          // the source is static for this example, but it could e.g. dynamically reflect the originating service
          .withSource(CLOUD_EVENT_SOURCE)
          .withType(messageDto.getMessageType())
          .withData(messageDto.getPayload())
          .withTime(ZonedDateTime.ofInstant(messageDto.getTimestamp(), ZoneId.of("UTC")))
          // Mandatory Optimize specific CloudEvents Extension, a correlation key
          // that relates multiple events to a single business transaction or process instance in BPMN terms. 
          .withExtension(
            ExtensionFormat.of(InMemoryFormat.of("traceid", messageDto.getOrderId(), String.class))
          )
          // Optional Optimize specific CloudEvents Extension, a group identifier that may allow to easier identify 
          // a group of related events for a user at the stage of mapping events to a process model. 
          .withExtension(
            ExtensionFormat.of(InMemoryFormat.of("group", CLOUD_EVENTS_EXTENSION_GROUP_VALUE, String.class))
          )
          .build()
      )
      .collect(Collectors.toList());
  }
}
```

The CloudEvent interface classes are obtained via this Maven Dependency:
```xml
    <dependency>
      <groupId>io.cloudevents</groupId>
      <artifactId>cloudevents-api</artifactId>
      <version>1.3.0</version>
    </dependency>
```

## Optimize Ingestion REST-API client
The final key component of this example is the [Optimize Ingestion REST-API client](https://docs.camunda.io/docs/apis-clients/optimize-api/event-ingestion/) implementation.
It can be found in the class [`org.camunda.optimize.examples.event.kafka.OptimizeCloudEventClient`][4], it accepts a list of CloudEvents and forwards them to the Optimize Ingestion REST-API.
In order to do that it also provides the, on Optimize side, configured API 
[`accessToken`](https://docs.camunda.io/docs/apis-clients/optimize-api/event-ingestion/#authorization) in the `Authorization` header of the request.

```java
public class OptimizeCloudEventClient {

  // ...

  public <T> void sendCloudEventsToOptimize(final List<CloudEvent<AttributesImpl, T>> cloudEvents) {
    log.debug("Try to ingest {} events into Optimize", cloudEvents.size());

    // Use Optimize Event Ingestion API
    // see https://docs.camunda.io/docs/apis-clients/optimize-api/event-ingestion/
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // Provide the secret accessToken configured via `eventBasedProcess.eventIngestion.accessToken` in Optimize
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
```

## Sample Message to CloudEvent conversion 
In summary the following sample message received via Kafka:
```json
{
  "id": "3c57737f-4d40-4fc3-8c58-28d8830437a2",
  "messageType": "orderCreated",
  "timestamp": "2020-04-03T12:00:00.000Z",
  "orderId": "ORDER-101",
  "payload": {
    "customerId": "C101"
  }
}
```
would get converted to the following CloudEvent:
```json
{
  "specversion": "1.0", 
  "id": "3c57737f-4d40-4fc3-8c58-28d8830437a2",
  "source": "/kafka/myEvents",
  "type": "orderCreated",
  "time": "2020-04-03T12:00:00.000Z",
  "traceid": "ORDER-101",
  "group": "order-process",
  "data": {
    "customerId": "C101"
  }
}
```
and ingested to Optimize.

[1]: ./docs/optimize-event-ingestion-architecture.png
[2]: ./src/main/java/org/camunda/optimize/examples/event/kafka/MessageDto.java
[3]: ./src/main/java/org/camunda/optimize/examples/event/kafka/BatchMessageSinkToOptimizeIngestListener.java
[4]: ./src/main/java/org/camunda/optimize/examples/event/kafka/OptimizeCloudEventClient.java
