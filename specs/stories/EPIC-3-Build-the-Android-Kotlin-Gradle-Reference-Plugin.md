# User Story

- Story ID: EPIC-3
- Title: Build the Android/Kotlin/Gradle Reference Plugin
- Status: Ready
- Priority: P0
- Owner: Scrum Team
- Last Updated: 2026-02-26

## As a Plugin Author, I want a complete, production-ready plugin for a popular tech stack (Android/Kotlin/Gradle), so that I have a clear example of how to define various metric source types and aggregation rules.

## Acceptance Criteria
- Given an Android project built with Gradle and Kotlin, When the `android-kotlin-gradle` plugin is active, Then it successfully extracts `test_count` from `junit_xml` reports.
- Given the same project, When the plugin is active, Then it successfully extracts `test_coverage_pct` from `jacoco_xml` reports.
- Given the same project, When the plugin is active, Then it successfully extracts `build_warnings` by scraping the build log, respecting Kotlin compiler versions.
- Given the same project, When the plugin is active, Then it successfully extracts `lint_errors` and `lint_warnings` from `android_lint_xml` reports.
- Given the same project, When the plugin is active, Then it successfully extracts CVE counts (critical, high, medium) from `owasp_xml` reports.
- Given the same project, When the plugin is active, Then it successfully extracts `detekt_issues` by scraping the build log.

## Notes
Delivering a high-quality, comprehensive reference plugin demonstrates the power and flexibility of the plugin engine and provides immediate value to a large user base (Android developers).
- Dependencies: EPIC-1

## Test Approach

