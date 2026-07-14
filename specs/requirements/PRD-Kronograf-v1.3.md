# Product Requirements Document: Kronograf
- **Version:** 1.3
- **Status:** Draft
- **Date:** February 2026
- **Change from v1.2:** Renamed tool from Repograde to Kronograf throughout.

## 1. Executive Summary
Teams working on long-lived software products often struggle to communicate the state of their codebase's technical health to stakeholders in customer reviews or steering meetings. Kronograf is a lightweight, CI/CD-integrated system that automatically extracts key quality KPIs from build artifacts, tracks them as time-series data across commits and releases, and renders them as embeddable SVG charts — suitable for display directly in a repository's README.md or any web page.

Kronograf is designed to work across major Git hosting platforms (GitHub, GitLab, Bitbucket) and build ecosystems (Gradle, Maven, Bazel, CMake, etc.) with minimal setup, leveraging infrastructure teams already use. A configurable plugin system allows any tech stack to be supported by declaring log-parsing rules in a YAML file, without writing code.

## 2. Problem Statement
### 2.1 The Communication Gap
Engineering teams invest significant effort in refactoring, adding test coverage, resolving security vulnerabilities, and reducing build warnings. This work is invisible to stakeholders unless explicitly surfaced. During customer reviews or management briefings, there is typically no easy way to show the trajectory of code quality over time.

### 2.2 The Tooling Fragmentation Problem
Existing code quality tools (SonarQube, Codecov, Snyk, etc.) are powerful but expensive, require dedicated infrastructure, are often platform-specific, and introduce vendor lock-in. Smaller teams or open-source projects need a lightweight alternative that can be bootstrapped with only a git repository and a CI/CD pipeline.

### 2.3 The Log Format Fragmentation Problem
Every tech stack emits build output in a different format, and those formats change with tool version upgrades. A system that hardcodes parsing logic for each tool becomes a maintenance burden. Extraction rules must be declarative, version-aware, and independently updatable without touching core application code.

## 3. Goals and Non-Goals

### 3.1 Goals
1.  Automatically collect quality KPIs from CI/CD build logs without requiring external services.
2.  Store KPI data points in a git-native format (a dedicated data branch or companion repository).
3.  Render KPIs as time-series SVG charts embeddable in `README.md` files.
4.  Provide a declarative, YAML-based plugin system for defining KPI extraction rules per tech stack.
5.  Support versioned extraction rules so log format changes can be handled without modifying core code.
6.  Ship a production-ready plugin for Android / Kotlin / Gradle.
7.  Support GitHub, GitLab, and other platforms through a build-tool-agnostic design.

### 3.2 Non-Goals
1.  **No Historical Backfill:** Kronograf will not provide a tool to generate retroactive data from the beginning of a repository's history.
2.  **No External Data Sources:** The tool will not provide an extension model for KPIs sourced from external systems like Jira or GitHub Issues.
3.  **No Real-Time Dashboards:** Kronograf is a commit/build-time analysis tool, not a live monitoring system.
4.  **Not a Replacement for SAST/DAST:** Kronograf is intended to visualize trends, not to replace full-featured security or static/dynamic analysis tools.
5.  **No Code Review Integration (v1):** The initial version will not integrate with pull requests or add automated comments to code reviews.
6.  **No Actionable Insights:** The tool will focus on trend visualization and will not provide fix recommendations or deeper analysis.

## 4. Target Users

| User | Need |
| :--- | :--- |
| **Engineering Lead** | Show background quality work to stakeholders without manual reporting. |
| **Software Developer** | Quickly see if quality metrics are trending in the right direction. |
| **Platform / DevOps Engineer**| Integrate the pipeline step and maintain plugin definitions. |
| **Plugin Author** | Add support for a new tech stack by editing YAML, not writing Python. |
| **Product Manager / Customer** | Understand the technical health of the product at a glance. |

## 5. Plugin System

### 5.1 Design Goals
The plugin system must satisfy the following requirements:
1.  **No-Code Configuration:** A new tech stack is supported by writing YAML, not Python. The extraction primitive is a `sed`-compatible regex with a capture group, which is universally understood and testable with standard shell tools.
2.  **Versioned Rules:** A plugin declares which tool version range a rule applies to. When a tool upgrade changes the log format, a new rule entry is added for the new version rather than replacing the old one, preserving the ability to re-process historical logs.
3.  **Composable:** A project may activate multiple plugins simultaneously (e.g., `android-kotlin` and `owasp-dependency-check`).
4.  **Testable in Isolation:** Each extraction rule ships with sample log lines and expected values, enabling unit-test-style validation of plugins independent of a real build.
5.  **Override-Friendly:** Kronograf ships with built-in plugins; a project can override any rule or extend any plugin in its local `kronograf.yml` without forking.

### 5.2 Plugin File Format
Plugins are YAML files. Built-in plugins live in the `plugins/` directory of the Kronograf repository. Projects can place additional plugin files in `.kronograf/plugins/` in their own repository.

The following is an example of a complete plugin file:

```yaml
# plugins/android-kotlin-gradle.yml
plugin:
  id: android-kotlin-gradle
  name: "Android – Kotlin & Gradle"
  description: >
    Extracts quality KPIs from Android projects built with Kotlin and Gradle.
    Supports Gradle build logs, JaCoCo coverage XML, JUnit XML test results,
    Android Lint XML reports, and OWASP Dependency-Check XML reports.
  version: "1.0.0"
  maintainer: "community"
  tags: [android, kotlin, gradle, mobile]
metrics:
  - id: test_count
    name: "Number of Tests"
    unit: count
    description: "Total number of test cases executed (passed + failed + skipped)."
    sources:
      - type: junit_xml
        path_patterns:
          - "**/build/test-results/**/*.xml"
          - "**/build/outputs/androidTest-results/**/*.xml"
        aggregate: sum_tests
  - id: test_coverage_pct
    name: "Test Coverage (%)"
    unit: percent
    description: "Line coverage percentage from JaCoCo."
    sources:
      - type: jacoco_xml
        path_patterns:
          - "**/build/reports/jacoco/**/*.xml"
        aggregate: weighted_average_line_coverage
  - id: build_warnings
    name: "Build Warnings"
    unit: count
    description: "Number of compiler and Gradle warnings emitted during the build."
    sources:
      - type: log_scrape
        path_patterns: ["**/build.log"]
        rules:
          - id: kotlin-warnings-2x
            description: "Kotlin compiler 2.x warning format"
            tool: kotlin-compiler
            version_range: ">=2.0.0"
            pattern: "^w: .*"
            aggregate: count_matches
            sample_lines:
              - line: "w: file.kt:42:8: This annotation is deprecated."
                matches: true
              - line: "e: file.kt:10:1: Unresolved reference: foo"
                matches: false
          - id: kotlin-warnings-1x
            description: "Kotlin compiler 1.x warning format"
            tool: kotlin-compiler
            version_range: ">=1.0.0 <2.0.0"
            pattern: "^w: .*"
            aggregate: count_matches
            sample_lines:
              - line: "w: file.kt:42:8: 'Foo' is deprecated."
                matches: true
          - id: gradle-warnings
            description: "Gradle deprecation and configuration warnings"
            tool: gradle
            version_range: "*"
            pattern: "^(Warning|WARN|\\[ant:.*\\].*warning):"
            aggregate: count_matches
            sample_lines:
              - line: "Warning: Configuration 'compile' is obsolete."
                matches: true
              - line: "Task :app:compileDebugKotlin FAILED"
                matches: false
        multi_rule_aggregate: sum
  - id: lint_errors
    name: "Android Lint Errors"
    unit: count
    description: "Number of errors reported by Android Lint."
    sources:
      - type: android_lint_xml
        path_patterns:
          - "**/build/reports/lint-results*.xml"
        aggregate: count_by_severity
        severity: error
  - id: lint_warnings
    name: "Android Lint Warnings"
    unit: count
    description: "Number of warnings reported by Android Lint."
    sources:
      - type: android_lint_xml
        path_patterns:
          - "**/build/reports/lint-results*.xml"
        aggregate: count_by_severity
        severity: warning
  - id: cve_critical
    name: "Critical CVEs"
    unit: count
    description: "Number of Critical severity CVEs in dependencies (OWASP Dependency-Check)."
    sources:
      - type: owasp_xml
        path_patterns:
          - "**/build/reports/dependency-check-report.xml"
        aggregate: count_by_cvss
        cvss_min: 9.0
  - id: cve_high
    name: "High CVEs"
    unit: count
    description: "Number of High severity CVEs in dependencies (OWASP Dependency-Check)."
    sources:
      - type: owasp_xml
        path_patterns:
          - "**/build/reports/dependency-check-report.xml"
        aggregate: count_by_cvss
        cvss_min: 7.0
        cvss_max: 8.9
  - id: cve_medium
    name: "Medium CVEs"
    unit: count
    description: "Number of Medium severity CVEs in dependencies (OWASP Dependency-Check)."
    sources:
      - type: owasp_xml
        path_patterns:
          - "**/build/reports/dependency-check-report.xml"
        aggregate: count_by_cvss
        cvss_min: 4.0
        cvss_max: 6.9
```
