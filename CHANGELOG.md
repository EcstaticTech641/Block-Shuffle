# Changelog

All notable changes to the **Block-Shuffle** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.1.1] - 2026-07-24

### Changed
- **Compatibility Verification**: Re-verified and established full runtime compatibility with `RonlabGameAssistant 1.11.0+`.
- **Metadata Alignment**: Updated project version to `2.1.1` in `pom.xml` and synced documentation references.

## [2.1.0] - 2026-07-23

### Added
- **RGA Event API Integration**: Integrated `com.ronlab:rga-api:1.11.0` for event-driven session lifecycle management (`MinigameStartEvent` and `MinigameConcludeEvent`).
- **Companion Integration Documentation**: Produced `docs/companion-integration.md` detailing listener registration, event payloads, score passing, and standalone fallbacks (closing RGA #27).
- **Conditional Listener Wire-up**: Automatically registers `RGAEventListener` when `RonlabGameAssistant` plugin is present on the server.
- **Paper Plugin Manifest**: Created `paper-plugin.yml` targeting Paper API `26.1` with soft-dependency declaration on `RonlabGameAssistant`.
- **Unit Testing**: Added JUnit 5 and Mockito test suite for `RGAEventListener` with 100% test pass rate on Java 25.

### Changed
- **Dependency Version**: Updated `com.ronlab:rga-api` dependency version to `1.11.0`.
- **Target Baseline**: Updated project compiler baseline to **Java 25** and **Paper API 26.1.2**.
- **Namespace Standardization**: Standardized all package structures from `tech.reisu1337.blockshuffle` to `com.ronlab.blockshuffle.*`.
- **Main Class**: Updated main plugin entry point to `com.ronlab.blockshuffle.BlockShufflePlugin`.
- **Scoreboard API**: Updated sidebar line handling to use Paper `Score#numberFormat(NumberFormat.blank())` and Adventure `Component` methods.

### Removed
- Removed legacy `plugin.yml` manifest in favor of `paper-plugin.yml`.
