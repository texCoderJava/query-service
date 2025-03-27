This is a multi-module project that houses various services for querying different databases and performing diverse data-handling tasks. The project also leverages common libraries and shared utilities to ensure consistency and reusability across modules.

# Table of Contents
## Overview

## Modules

## Architecture Diagram

## Usage

## Testing

## Roadmap

# Overview

The Multi-Module Query Service includes several discrete yet interrelated modules, each responsible for specific functionalities ranging from querying various data stores (such as Kafka, Redis, MongoDB) to providing a unified API in the main service module. The shared “commons” module contains common logic, DTOs, and utilities used across all services.

## Key objectives:

- A single codebase for multiple data-centric services.

- Consistent patterns and practices enforced by the commons module.

- Streamlined development, testing, and deployment through a parent POM and shared dependencies.

## Modules

### commons

- Purpose: Holds shared utilities, DTOs, common exception handling, and logic shared across all other modules.

### Key Contents:

- Common POJOs, data transfer objects, constants, exception classes.

- Helper methods for logging, transformations, or validations.

### kafka-service

- Purpose: Interacts with Apache Kafka to either consume or produce data streams.

### Key Responsibilities:

- Establishing Kafka consumer/producer configurations.

- Reading from or writing to specified Kafka topics.

### redis-service

- Purpose: Connects to Redis for caching and real-time data lookups.

### Key Responsibilities:

- Basic CRUD operations on Redis.

- Potential usage of pub/sub for real-time messaging.

### mongo-service

- Purpose: Manages reactive operations to a MongoDB instance.

### Key Responsibilities:

- Creating, reading, updating, and deleting documents in MongoDB in a reactive, non-blocking manner.

- Implementing advanced queries and aggregations if needed.

### main-service (sometimes also named query-service or simply the aggregator)

Purpose: Serves as the primary entry point for external clients, combining data from kafka-service, redis-service, and mongo-service.

Key Responsibilities:

Exposing HTTP (or RSocket) endpoints.

Orchestrating queries across multiple data stores.

Integrating with commons module for shared functionalities.

## Architecture Diagram

![diagram2](https://github.com/user-attachments/assets/5f78d440-e08c-4b7b-ba7e-cb252a77ea3c)

## Usage

### Main Service Endpoints

- /api/v1/data – An example endpoint that retrieves combined data from Kafka, Redis, or Mongo depending on request params.

### Kafka Service Endpoints (if exposed)

- /api/v1/kafka/publish – Publishes messages to a Kafka topic.

- /api/v1/kafka/consume – Consumes messages from a Kafka topic.

### Redis Service Endpoints (if exposed)

- /api/v1/redis/get – Retrieves data from Redis cache.

- /api/v1/redis/put – Stores data into Redis cache.

### Mongo Service Endpoints (if exposed)

- /api/v1/mongo/document – Basic CRUD operations for MongoDB in a reactive manner.

## Roadmap
- Enhance Error Handling in all services with more robust, consistent exception responses (shared in commons).

- Add Caching improvements using Redis for frequently accessed queries.

- Expand Kafka Integrations for streaming analytics or change data capture.

- Introduce GraphQL or advanced query capabilities in the main service for more flexible client requests.

- Docker / Kubernetes deployment scripts for container orchestration.
