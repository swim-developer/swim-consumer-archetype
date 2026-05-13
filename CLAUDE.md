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
      domain/model/
      application/port/{in,out}/, application/service/, application/usecase/
      infrastructure/in/{rest,amqp}/, infrastructure/out/{persistence,client,messaging,xml,subscription,mapper}/
    src/main/resources/
      application.properties   # Velocity template with ${serviceName}, ${dollar}{ENV_VAR} patterns
    Makefile, mvnw, mvnw.cmd, .mvn/
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

### Consumer-Validator Architecture
A Consumer NEVER connects to the Provider of the same module. The "provider" from a Consumer's perspective is always a **Consumer Validator** (its own Artemis + mock Subscription Manager). When editing templates that reference provider endpoints, ensure they point to `*-consumer-validator`, never to `*-provider`.

### Archetype Sync
When changes are made to a consumer project (e.g., `swim-digital-notam-consumer`), check whether the modified class also exists as a template here. Mechanical/infrastructure classes should be updated in the archetype; domain-specific stubs should not. After updating, run `mvn clean install` on this archetype.

### Code Standards (apply to generated code too)
- Integration tests: `./mvnw verify -DskipITs=false` (generated projects default `skipITs=true`)
- Testing: RestAssured for HTTP, AssertJ for assertions

### Naming
Every name must be unambiguous. Never use bare `consumer` when multiple consumer types exist — always qualify (e.g., `swim-dnotam-consumer`).

## Generated Project Tech Stack

- **Runtime**: Quarkus (JDK 21)
- **Database**: MongoDB via Panache
- **Messaging**: Kafka (inbox/outbox), AMQP (external provider via Artemis)
- **Security**: mTLS, TLS 1.3 (SPEC-170 SWIM-TIYP-0008: TLS 1.2 deprecated)
- **Observability**: OpenTelemetry, Micrometer/Prometheus
- **Build**: Maven with wrapper, Makefile for workflow automation

## Reference Implementation

Use `swim-dnotam-consumer` (sibling repo at `../swim-digital-notam-consumer/`) as the reference for how generated TODO stubs should be implemented.
