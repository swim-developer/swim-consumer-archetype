# SWIM Consumer Archetype

Maven archetype that generates a complete SWIM **consumer** service project with all mechanical classes pre-configured. The generated project follows hexagonal architecture and integrates with the `swim-framework-consumer` module.

## Prerequisites

- JDK 21
- Maven 3.9+
- The `swim-framework` modules installed in the local Maven repository

## Install the Archetype

```bash
cd applications/swim-consumer-archetype
mvn clean install
```

## Generate a New Consumer

```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.github.swim-developer \
  -DarchetypeArtifactId=swim-consumer-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.github.swim_developer.ffice.consumer \
  -DartifactId=swim-ffice-consumer \
  -Dversion=1.0.0-SNAPSHOT \
  -DserviceName=ffice \
  -DserviceDisplayName="FF-ICE" \
  -DservicePrefix=Ffice \
  -DdataModel=FIXM \
  -DcollectionPrefix=ffice \
  -DmodelArtifactId=swim-fixm-ffice-model \
  -DinteractiveMode=false
```

> **`modelArtifactId` is required and has no default.** Provide the artifact ID of the data model project this consumer will parse. Examples: `swim-aixm-model`, `swim-fixm-model-ed254`, `swim-fixm-ffice-model`.

## Parameters

| Parameter | Description | Example |
|---|---|---|
| `serviceName` | Lowercase identifier used in config keys, Kafka topics, and collection names | `ffice` |
| `serviceDisplayName` | Human-readable name for OpenAPI docs and logging | `FF-ICE` |
| `servicePrefix` | PascalCase prefix (currently reserved for future use) | `Ffice` |
| `dataModel` | Data exchange model used by the service | `FIXM` |
| `collectionPrefix` | MongoDB collection name prefix | `ffice` |
| `modelArtifactId` | **REQUIRED. No default.** Artifact ID of the data model dependency (used in `SYNC_DEPS` and `deps` target) | `swim-aixm-model`, `swim-fixm-model-ed254`, `swim-fixm-ffice-model` |

Standard Maven parameters (`groupId`, `artifactId`, `version`, `package`) are also supported.

## Post-Generation

After generating the project, make the Maven wrapper executable:

```bash
cd swim-ffice-consumer
chmod +x mvnw
```

The archetype includes the full Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`). The executable bit is not preserved by the Maven Archetype Plugin, so `chmod +x` is required on Unix systems.

## Sync and Install

Run once after generation to pull dependencies and install them into the local Maven repository:

```bash
make sync
```

Or run each step individually:

```bash
make pull          # git pull --ff-only on this project
make pull-deps     # clone or pull all SYNC_DEPS from GitHub
make install-deps  # install all SYNC_DEPS into local Maven repo
```

`SYNC_DEPS` is pre-configured in the generated `Makefile` using the value you provided for `modelArtifactId` at generation time.

## Verify

Compile the generated project to confirm everything is wired correctly:

```bash
./mvnw compile
```

The project compiles with zero errors. Classes marked with `// TODO` will throw `UnsupportedOperationException` at runtime until you implement them.

## What Gets Generated

The archetype produces **38 Java classes** + `application.properties`, organized in hexagonal layers:

### Domain Layer (3 classes)

| Class | Purpose |
|---|---|
| `Event` | Domain event entity persisted to MongoDB |
| `Subscription` | Subscription domain model implementing `SwimConsumerSubscription` |
| `SubscriptionCommand` | Command record for subscription creation requests |
| `FilterDimension` | Enum defining filterable dimensions for this service |

### Application Layer (10 classes)

| Class | Purpose |
|---|---|
| `ManageSubscriptionPort` | Input port for subscription lifecycle operations |
| `EventStore` | Output port for event persistence |
| `SubscriptionStore` | Output port for subscription persistence |
| `RemoteSubscriptionManagerPort` | Output port for calling the external Subscription Manager API |
| `EventProcessingUseCase` | Orchestrates event processing pipeline |
| `SubscriptionUseCase` | Implements subscription CRUD via ManageSubscriptionPort |
| `EventPersistenceService` | Handles event storage and deduplication |
| `EventDataValidator` | Validates incoming event data |
| `EventFilterService` | Applies subscription filters to events |
| `ProcessorCallbacks` | Lifecycle callbacks for event processing |
| `ProcessingMetrics` | Exposes processing metrics via Micrometer |

### Infrastructure Layer (25 classes)

**Inbound (REST + AMQP)**

| Class | Purpose |
|---|---|
| `ConsumerEventResource` | REST API for querying consumed events |
| `ConsumerSubscriptionResource` | REST API for subscription management |
| `OperationalResource` | REST API for operational metrics and DLQ |
| `FeatureResource` | WFS GetFeature endpoint |
| `InboxMessageHandler` | Kafka inbox reader (Extension Point EP2) |
| `EventDTO` | Event data transfer object |
| `SubscriptionRequest` | Subscription creation request DTO |
| `SubscriptionResponse` | Subscription response DTO |
| `TopicDetailsResponse` | Topic details response DTO |

**Outbound (Persistence)**

| Class | Purpose |
|---|---|
| `MongoEventStore` | MongoDB implementation of EventStore |
| `MongoSubscriptionStore` | MongoDB implementation of SubscriptionStore |
| `MongoIndexInitializer` | Creates MongoDB indexes at startup |
| `EventHashProjection` | Projection for idempotency hash queries |
| `SubscriptionDocument` | MongoDB document for subscriptions |
| `SubscriptionDocumentRepository` | Panache repository for subscription documents |

**Outbound (External Services)**

| Class | Purpose |
|---|---|
| `SubscriptionManagerRestClient` | MicroProfile REST Client for Subscription Manager API |
| `SubscriptionManagerAdapter` | Adapter bridging REST client to the output port |
| `SubscriptionRenewalStrategy` | Handles subscription renewal logic |

**Outbound (Messaging + Mapping)**

| Class | Purpose |
|---|---|
| `OutboxMessageHandler` | Processes outbox events for downstream delivery |
| `SubscriptionMapper` | Maps between domain and persistence models |

**Outbound (XML)**

| Class | Purpose |
|---|---|
| `EventExtractor` | Extracts domain events from XML messages (TODO) |
| `JaxbUnmarshallerPool` | JAXB unmarshalling and XSD validation (TODO) |
| `XmlEnvelopeParser` | Parses XML envelope metadata (TODO) |

## Classes Requiring Domain-Specific Implementation

Classes marked with `// TODO` require the developer to implement domain logic:

1. **`EventExtractor`** - Parse your data model (AIXM, FIXM, etc.) and extract domain events
2. **`JaxbUnmarshallerPool`** - Initialize JAXB context for your XSD schemas
3. **`XmlEnvelopeParser`** - Extract metadata from XML envelopes
4. **`Event`** - Add domain-specific fields to the event entity
5. **`Subscription.projectFilterDimensions()`** - Define filter dimensions for your service
6. **`FilterDimension`** - Define the enum values for your filterable dimensions
7. **`SubscriptionCommand`** - Add domain-specific filter fields
8. **`SubscriptionRequest`** - Add domain-specific filter fields to the REST DTO
9. **`EventDataValidator`** - Implement domain-specific validation rules
10. **`EventFilterService`** - Implement domain-specific event filtering logic

All other classes are fully functional out of the box and only require configuration via `application.properties`.

## Configuration

The generated `application.properties` is pre-configured with parametrized values. Key properties to review:

```properties
quarkus.application.name=swim-{serviceName}-consumer
quarkus.mongodb.database=swim-{serviceName}
swim.service.name={serviceName}
swim.subscriptions.command-type=...{package}...SubscriptionCommand
```

Environment variables with sensible defaults are used for deployment flexibility (e.g., `${MONGODB_DATABASE:swim-ffice}`).

## Next Steps

After generating the project:

1. Run `make sync` to pull dependencies and install them (the `Makefile` already has the correct `modelArtifactId` set from generation time)
2. Add your outbox router extension to `pom.xml` (e.g., `swim-outbox-kafka-dnotam`)
3. Implement the 10 domain-specific classes listed above
4. Create a `compose.yml` for local development infrastructure (MongoDB, Kafka, Artemis, Consumer Validator), or use [Quarkus Dev Services](https://quarkus.io/guides/dev-services) to provision them automatically
5. Review and adjust `application.properties` for your environment

Use the existing **swim-dnotam-consumer** as a reference implementation. Its domain-specific classes demonstrate the patterns expected by each TODO class.

## Technology Stack

- **Runtime**: Quarkus
- **Database**: MongoDB (via Panache)
- **Messaging**: Kafka (inbox/outbox), AMQP (external provider)
- **Security**: mTLS, OIDC
- **Observability**: OpenTelemetry, Micrometer/Prometheus
