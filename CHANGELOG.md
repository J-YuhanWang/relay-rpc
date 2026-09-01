# Changelog

All notable changes to the RelayRPC project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.0] - 2026-09-01

### Added

* JSON serialization support using Jackson 3:

  * `JsonSerializer` implementation of the existing `Serializer` abstraction.
  * Restoration of declared RPC request argument types after JSON deserialization.
  * Restoration of declared RPC response data types after JSON deserialization.
  * Validation for missing parameter metadata and mismatched argument counts.
* Configurable serializer selection through `relay.rpc.serializer`, with `jdk` used by default.
* Centralized serializer identifiers through `SerializerKeys`.
* Pure JDK SPI extension mechanism:

  * Classpath-based implementation discovery through `SpiLoader`.
  * Separate `META-INF/relayrpc/system` and `META-INF/relayrpc/custom` registration paths.
  * Custom registrations overriding built-in implementations with the same key.
  * Validation that registered implementations implement their declared SPI interface.
  * Thread-safe caches for implementation classes and reusable instances.
  * Lazy implementation instantiation through reflection.
* Built-in SPI registrations for `JdkSerializer` and `JsonSerializer`.
* Unit tests covering JSON serialization, RPC type restoration, SPI loading, serializer selection, instance reuse, invalid metadata, and unsupported serializer keys.

### Changed

* Updated `SerializerFactory` to load serializer implementations through SPI and resolve them by configuration key.
* Updated `ServiceProxy` and `HttpServerHandler` to obtain serializers dynamically instead of directly constructing `JdkSerializer`.
* Extended the global RPC configuration to carry the selected serializer key.

---
## [0.2.0] - 2026-08-28

### Added

* Global RPC configuration system:

  * Immutable `RpcConfig` record containing application name, version, server host, and server port.
  * Centralized configuration property names through `RpcConfigKeys`.
  * Pure JDK classpath-based `RpcConfigLoader`.
  * Default configuration values with full and partial user overrides.
  * UTF-8 loading of `application.properties`.
  * Integer parsing and valid server-port range validation.
* Thread-safe global configuration management through `RpcApplication`:

  * Lazy classpath-based initialization.
  * Explicit initialization with a custom `RpcConfig`.
  * Safe publication of the initialized configuration.
* Consumer and provider `application.properties` configuration files.
* Unit tests covering:

  * Default, partial, and complete configuration resolution.
  * Classpath-based configuration loading.
  * Invalid and out-of-range server ports.
  * Default and custom `RpcApplication` initialization.
  * Null custom-configuration rejection.

### Changed

* Replaced hard-coded provider host and port values with values resolved through `RpcApplication`.
* Updated the consumer request pipeline and provider server startup to use the global RPC configuration.

## [0.1.0] - 2026-08-22

### Added

* Multi-module Maven architecture:

  * `relay-rpc-core`
  * `relay-example-common`
  * `relay-example-provider`
  * `relay-example-consumer`
* Core RPC data models (`RpcRequest`, `RpcResponse`) using the Builder pattern.
* Serialization abstraction with the `Serializer` interface and `JdkSerializer` implementation.
* Non-blocking asynchronous HTTP server using Eclipse Vert.x (`VertxHttpServer`).
* Service registration and dispatch pipeline:

  * In-memory, thread-safe service registry (`LocalRegistry`).
  * Unit tests for service registration, lookup, and removal.
  * Reflection-based request routing and execution through `HttpServerHandler`.
* Unified logging using SLF4J and Logback.
* Client-side dynamic proxy engine:

  * Universal `ServiceProxy` implementing JDK `InvocationHandler`.
  * Shared JDK native `HttpClient` for connection pooling.
  * Generic `ServiceProxyFactory` for creating client service stubs.
  * `ConsumerExample` bootstrapped through `ServiceProxyFactory`.
* Proof-of-concept static proxy (`UserServiceProxy`), retained as a reference implementation predating the dynamic proxy.
