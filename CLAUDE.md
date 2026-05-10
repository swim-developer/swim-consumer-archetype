# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Project Is

A **Maven archetype** that generates complete SWIM consumer service projects. It produces ~38 Java classes organized in hexagonal architecture, integrated with `swim-framework-consumer`. Generated projects use Quarkus, MongoDB, Kafka, and AMQP.

This is NOT a runnable application — it is a template engine. The source files under `src/main/resources/archetype-resources/` are **Apache Velocity templates** that use `${serviceName}`, `${package}`, `${servicePrefix}`, `${modelArtifactId}`, etc. as interpolation variables.

## Build Commands

```bash
# Install the archetype into local Maven repo
mvn clean install

# Generate a new consumer project from this archetype
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

There are no tests in this project. Validation is done by generating a consumer and compiling it.

## Architecture

### Template Structure

```
src/main/resources/
  archetype-resources/
    src/main/java/
      domain/model/            # Event, Subscription, FilterDimension, SubscriptionCommand
      application/
        port/in/               # ManageSubscriptionPort
        port/out/              # EventStore, SubscriptionStore, RemoteSubscriptionManagerPort
        service/               # EventPersistenceService, EventDataValidator, EventFilterService, ProcessorCallbacks, ProcessingMetrics
        usecase/               # EventProcessingUseCase, SubscriptionUseCase
      infrastructure/
        in/rest/               # REST resources + DTOs
        in/amqp/               # InboxMessageHandler (Kafka inbox reader)
        out/persistence/       # Mongo stores, indexes, projections, repository
        out/client/            # SubscriptionManagerRestClient + adapter
        out/messaging/         # OutboxMessageHandler
        out/xml/               # EventExtractor, JaxbUnmarshallerPool, XmlEnvelopeParser (TODO stubs)
        out/subscription/      # __servicePrefix__SubscriptionRenewalStrategy
        out/mapper/            # SubscriptionMapper
    src/main/resources/
      application.properties   # Velocity template with ${serviceName}, ${dollar}{ENV_VAR} patterns
    Makefile                   # Generated Makefile for sync/build/test/sonar/container targets
    mvnw, mvnw.cmd, .mvn/     # Maven wrapper (included in generated project)
  META-INF/maven/
    archetype-metadata.xml     # Archetype descriptor — defines required properties and file sets
```

### Key Archetype Parameters

Defined in `archetype-metadata.xml`. `modelArtifactId` is **required with no default**.

| Parameter | Example | Usage |
|---|---|---|
| `serviceName` | `ffice` | Config keys, Kafka topics, collection names |
| `serviceDisplayName` | `FF-ICE` | OpenAPI docs, logging |
| `servicePrefix` | `Ffice` | PascalCase, used in filename `__servicePrefix__SubscriptionRenewalStrategy.java` |
| `dataModel` | `FIXM` | Data exchange model identifier |
| `collectionPrefix` | `ffice` | MongoDB collection name prefix |
| `modelArtifactId` | `swim-fixm-ffice-model` | Data model dependency artifact |

### Velocity Template Conventions

- `${serviceName}`, `${package}`, `${servicePrefix}` — archetype variables, interpolated at generation time
- `${dollar}{ENV_VAR:default}` — Quarkus/MicroProfile config placeholders, must survive archetype generation (note: `$dollar` is set via `#set( $dollar = '$' )`)
- `${symbol_dollar}` — another Velocity escape for `$` in Makefile and pom.xml templates
- `__servicePrefix__` in filenames — Maven Archetype Plugin replaces this with the parameter value

## Non-Negotiable Rules

### AI Authorship Prohibition
NEVER add `Co-Authored-By` or any AI/tool reference to commit messages. A global git hook at `~/.config/git/hooks/commit-msg` strips these automatically, but the rule applies regardless.

### Consumer-Validator Architecture
A Consumer NEVER connects to the Provider of the same module. The "provider" from a Consumer's perspective is always a **Consumer Validator** (its own Artemis + mock Subscription Manager). When editing templates that reference provider endpoints, ensure they point to `*-consumer-validator`, never to `*-provider`.

### Archetype Sync
When changes are made to a consumer project (e.g., `swim-digital-notam-consumer`), check whether the modified class also exists as a template here. Mechanical/infrastructure classes should be updated in the archetype; domain-specific stubs should not. After updating, run `mvn clean install` on this archetype.

### Code Standards (apply to generated code too)
- Max 400 lines per file
- No inner/nested classes — every class in its own file
- Use `@Slf4j` (Lombok) for logging, never `LoggerFactory.getLogger()`
- No comments in code
- No Java Reflection anywhere
- Build with `mvn clean package -DskipTests` (not `-Dmaven.test.skip=true`)
- Integration tests: `./mvnw verify -DskipITs=false` (generated projects default `skipITs=true`)
- Container runtime: **Podman only** (not Docker)
- JSON processing in shell: **jq only** (not Python/Node)
- Testing: RestAssured for HTTP, AssertJ for assertions

### Naming
Every name must be unambiguous. Never use bare `consumer` when multiple consumer types exist — always qualify (e.g., `swim-dnotam-consumer`).

## Generated Project Tech Stack

- **Runtime**: Quarkus (JDK 21)
- **Database**: MongoDB via Panache
- **Messaging**: Kafka (inbox/outbox), AMQP (external provider via Artemis)
- **Security**: mTLS, TLS 1.2 exclusively
- **Observability**: OpenTelemetry, Micrometer/Prometheus
- **Build**: Maven with wrapper, Makefile for workflow automation

## Reference Implementation

Use `swim-dnotam-consumer` (sibling repo at `../swim-digital-notam-consumer/`) as the reference for how generated TODO stubs should be implemented.
