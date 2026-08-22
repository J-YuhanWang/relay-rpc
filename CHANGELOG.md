# Changelog
All notable changes to the RelayRPC project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Planned
- Dynamic proxy implementation using JDK `InvocationHandler`
- Factory pattern abstraction for client-side proxy generation

---

## [0.1.0] - 2026-08-22
### Added
- Multi-module Maven architecture: `relay-rpc-core`, `relay-example-common`, `relay-example-provider`, and `relay-example-consumer`.
- Core RPC data models (`RpcRequest`, `RpcResponse`) with Builder pattern.
- Serialization abstraction with `Serializer` interface and `JdkSerializer` implementation.
- Non-blocking asynchronous HTTP server using Eclipse Vert.x (`VertxHttpServer`).
- Service registration and dispatch pipeline:
  - In-memory thread-safe service registry (`LocalRegistry`).
  - Unit tests for `LocalRegistry` covering register/get/remove behavior.
  - Reflection-based request routing and execution handler (`HttpServerHandler`).
- Unified logging facade with SLF4J and Logback implementation.
- Proof-of-concept static proxy (`UserServiceProxy`) and consumer bootstrap example, using a shared singleton JDK 11 native `HttpClient` for connection pooling.