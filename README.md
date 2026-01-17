# Inhouse Integration Platform (Java 8 Microservices MVP)

This repository provides a Java 8 + Spring Boot microservices implementation of the Inhouse enterprise integration platform described in `inhouse-system-design.md`.

## Services
- **IAM Service (8081)**: user/role management and login token issuance.
- **Permission Service (8082)**: policy management and authorization checks.
- **Registry Service (8083)**: application registry.
- **AI Service (8084)**: AIHub invocation stub.
- **Event Service (8085)**: event publish/list API.
- **Observability Service (8086)**: audit log ingestion and listing.
- **Gateway Service (8080)**: API gateway that calls Permission Service for access checks.

## Requirements
- Java 8
- Maven 3.x

## Quickstart

Build all services:

```bash
mvn clean package
```

Run each service (in separate terminals):

```bash
mvn -pl services/iam-service spring-boot:run
mvn -pl services/permission-service spring-boot:run
mvn -pl services/registry-service spring-boot:run
mvn -pl services/ai-service spring-boot:run
mvn -pl services/event-service spring-boot:run
mvn -pl services/observability-service spring-boot:run
mvn -pl services/gateway-service spring-boot:run
```

## Example Flow
1. Create a user and role in IAM (`/iam/*`).
2. Login via `/auth/login` to get a token.
3. Create policies in Permission Service (`/permissions/*`).
4. Register apps in Registry Service (`/registry/apps`).
5. Invoke AI models via AI Service (`/ai/{provider}/{model}/invoke`).
6. Publish events via Event Service (`/events`).
7. Send audit logs to Observability Service (`/observability/audits`).
8. Use Gateway (`/gateway/{app}/{feature}`) to verify access with Permission Service.

## Notes
- Each service stores data in memory for this MVP; restart clears data.
- Gateway calls Permission Service at `permission.service.url` (default `http://localhost:8082`).
