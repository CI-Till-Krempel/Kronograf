# Product Requirements Document
## Kronograf — Technical Debt Visualizer for Git Repositories

**Version:** 1.3  
**Status:** Draft  
**Date:** February 2026  
**Changes from v1.2:** Renamed tool from Repograde to Kronograf throughout.

---

## 1. Executive Summary

Teams working on long-lived software products often struggle to communicate the state of their codebase's technical health to stakeholders in customer reviews or steering meetings. **Kronograf** is a lightweight, CI/CD-integrated system that automatically extracts key quality KPIs from build artifacts, tracks them as time-series data across commits and releases, and renders them as embeddable SVG charts — suitable for display directly in a repository's `README.md` or any web page.

Kronograf is designed to work across major Git hosting platforms (GitHub, GitLab, Bitbucket) and build ecosystems (Gradle, Maven, Bazel, CMake, etc.) with minimal setup, leveraging infrastructure teams already use. A **configurable plugin system** allows any tech stack to be supported by declaring log-parsing rules in a YAML file, without writing code.

---

## 2. Problem Statement

### 2.1 The Communication Gap
Engineering teams invest significant effort in refactoring, adding test coverage, resolving security vulnerabilities, and reducing build warnings. This work is invisible to stakeholders unless explicitly surfaced. During customer reviews or management briefings, there is typically no easy way to show the trajectory of code quality over time.

### 2.2 The Tooling Fragmentation Problem
Existing code quality tools (SonarQube, Codecov, Snyk, etc.) are powerful but expensive, require dedicated infrastructure, are often platform-specific, and introduce vendor lock-in. Smaller teams or open-source projects need a lightweight alternative that can be bootstrapped with only a git repository and a CI/CD pipeline.

### 2.3 The Log Format Fragmentation Problem
Every tech stack emits build output in a different format, and those formats change with tool version upgrades. A system that hardcodes parsing logic for each tool becomes a maintenance burden. Extraction rules must be declarative, version-aware, and independently updatable without touching core application code.

---

## 3. Goals and Non-Goals

### Goals
- Automatically collect quality KPIs from CI/CD build logs without requiring external services.
- Store KPI data points in a git-native format (a dedicated data branch or companion repository).
- Render KPIs as time-series SVG charts embeddable in `README.md` files.
- Provide a **declarative, YAML-based plugin system** for defining KPI extraction rules per tech stack.
- Support versioned extraction rules so log format changes can be handled without modifying core code.
- Ship a production-ready plugin for Android / Kotlin / Gradle.
- Support GitHub, GitLab, and other platforms through a build-tool-agnostic design.
- Provide a **Historical Backfill Tool** to generate retroactive data from the beginning of the repository.
- Provide an extension model for KPIs sourced from external systems (Jira, GitHub Issues, etc.).

### Non-Goals
- Real-time dashboards (Kronograf is a commit/build-time tool, not a live monitoring system).
- Replacing full-featured SAST/DAST tools.
- Code review integration or automated pull request comments (out of scope for v1).
- Providing fix recommendations or actionable insights beyond trend visualization.

---

## 4. Target Users

| User | Need |
|---|---|
| Engineering Lead | Show background quality work to stakeholders without manual reporting |
| Software Developer | Quickly see if quality metrics are trending in the right direction |
| Platform / DevOps Engineer | Integrate the pipeline step and maintain plugin definitions |
| Plugin Author | Add support for a new tech stack by editing YAML, not writing Python |
| Product Manager / Customer | Understand the technical health of the product at a glance |

---

## 5. Plugin System

### 5.1 Design Goals

The plugin system must satisfy the following requirements:

- **No-code configuration** — a new tech stack is supported by writing YAML, not Python. The extraction primitive is a `sed`-compatible regex with a capture group, which is universally understood and testable with standard shell tools.
- **Versioned rules** — a plugin declares which tool version range a rule applies to. When a tool upgrade changes log format, a new rule entry is added for the new version rather than replacing the old one, preserving the ability to re-process historical logs.
- **Composable** — a project may activate multiple plugins (e.g., `android-kotlin` and `owasp-dependency-check`).
- **Testable in isolation** — each extraction rule ships with sample log lines and expected values, enabling unit-test-style validation of plugins independent of a real build.
- **Override-friendly** — Kronograf ships built-in plugins; a project can override any rule or extend any plugin in its local `kronograf.yml` without forking.

### 5.2 Plugin File Format

Plugins are YAML files. Built-in plugins live in the `plugins/` directory of the Kronograf repository. Projects can place additional plugin files in `.kronograf/plugins/` in their own repository.

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
    sources:
      - type: owasp_xml
        path_patterns:
          - "**/build/reports/dependency-check-report.xml"
        aggregate: count_by_cvss
        cvss_min: 4.0
        cvss_max: 6.9

  - id: kotlin_loc
    name: "Lines of Code (Kotlin)"
    unit: count
    description: "Total non-blank, non-comment lines of Kotlin source code."
    sources:
      - type: log_scrape
        rules:
          - id: cloc-kotlin
            description: "Output line from the 'cloc' tool for Kotlin files"
            tool: cloc
            version_range: "*"
            pattern: "^Kotlin\\s+[0-9]+\\s+[0-9]+\\s+[0-9]+\\s+([0-9]+)"
            aggregate: extract_first
            sample_lines:
              - line: "Kotlin          87            312            843          14201"
                expected_value: 14201
              - line: "Java             5             20             60            980"
                matches: false

  - id: detekt_issues
    name: "Detekt Issues"
    unit: count
    description: "Total static analysis issues found by Detekt."
    sources:
      - type: log_scrape
        rules:
          - id: detekt-summary-2x
            description: "Detekt 2.x summary line"
            tool: detekt
            version_range: ">=2.0.0"
            pattern: "^detekt finished with ([0-9]+) issues"
            aggregate: extract_first
            sample_lines:
              - line: "detekt finished with 23 issues"
                expected_value: 23
          - id: detekt-summary-1x
            description: "Detekt 1.x summary line"
            tool: detekt
            version_range: ">=1.0.0 <2.0.0"
            pattern: "^([0-9]+) issues found"
            aggregate: extract_first
            sample_lines:
              - line: "23 issues found"
                expected_value: 23
```

### 5.3 Source Types

The plugin system supports two categories of source types:

**Structured report parsers** — built-in parsers for well-known file formats. These are preferred over log scraping when a report file is available, as they are more robust to incidental log output changes.

| Source Type | Format | Description |
|---|---|---|
| `junit_xml` | JUnit XML | Test results from any JUnit-compatible runner |
| `jacoco_xml` | JaCoCo XML | Line/branch coverage from JaCoCo |
| `android_lint_xml` | Android Lint XML | Issues from Android Lint |
| `owasp_xml` | OWASP Dependency-Check XML | CVE findings |
| `lcov` | lcov `.info` | Coverage for C/C++, Swift, Go |
| `coverage_py_xml` | coverage.py XML | Python coverage |
| `checkstyle_xml` | Checkstyle XML | Style violations (Java, Kotlin via detekt-checkstyle) |

**Log scrapers** — applied to captured CI stdout/stderr. These use the versioned rule system described in §5.2.

| Aggregate Mode | Behavior |
|---|---|
| `count_matches` | Count the number of lines matching the pattern |
| `extract_first` | Extract capture group 1 from the first matching line as a number |
| `extract_last` | Extract capture group 1 from the last matching line |
| `extract_sum` | Sum capture group 1 across all matching lines |

### 5.4 Version Detection

To select the correct versioned rule, Kronograf attempts to detect the tool version from the build log before applying rules:

```yaml
# In plugins/tool-versions.yml (built-in, shared across plugins)
tool_version_detectors:
  kotlin-compiler:
    pattern: "^kotlinCompilerClasspath.*-(\\d+\\.\\d+\\.\\d+)\\.jar"
    source: log
  gradle:
    pattern: "^Gradle (\\d+\\.\\d+)"
    source: log
  detekt:
    pattern: "^\\s*detekt version (\\d+\\.\\d+\\.\\d+)"
    source: log
  cloc:
    pattern: "^v(\\d+\\.\\d+)"
    source: log
```

If version detection fails, rules with `version_range: "*"` are used as fallback.

### 5.5 Plugin Validation and Testing

Every plugin can be validated independently using the Kronograf CLI:

```bash
# Validate plugin syntax and run built-in sample assertions
kronograf plugin validate plugins/android-kotlin-gradle.yml

# Run all plugin tests against a real build log file
kronograf plugin test plugins/android-kotlin-gradle.yml \
  --log build.log \
  --reports-dir build/reports/

# List all active plugins for the current project
kronograf plugin list

# Show which rules matched and what values were extracted for a given log
kronograf plugin dry-run --log build.log --plugin android-kotlin-gradle
```

The `validate` command checks that:
- All `sample_lines` assertions pass (correct match/no-match and expected values).
- All referenced `aggregate` modes exist.
- All `path_patterns` are valid globs.
- `version_range` strings are valid semver ranges.

### 5.6 Project-Level Plugin Configuration

Projects activate plugins and can override individual rules in `kronograf.yml`:

```yaml
plugins:
  - id: android-kotlin-gradle

  - url: https://raw.githubusercontent.com/org/kronograf-plugins/main/react-native.yml
    sha256: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4"

  - id: android-kotlin-gradle
    overrides:
      metrics:
        - id: build_warnings
          sources:
            - type: log_scrape
              rules:
                - id: custom-deprecation
                  description: "Our custom framework deprecation warning"
                  tool: internal
                  version_range: "*"
                  pattern: "^\\[DEPRECATED\\].*"
                  aggregate: count_matches
                  sample_lines:
                    - line: "[DEPRECATED] Use FooV2 instead of Foo"
                      matches: true
```

### 5.7 Plugin Authoring Guide (Summary)

A plugin author follows this workflow to add support for a new tech stack:

1. **Copy** `plugins/_template.yml` to `plugins/my-stack.yml`.
2. **Identify** which KPIs can be sourced from structured report files vs. log scraping.
3. **For each log-scraped metric**, capture one or more real log lines and craft a sed ERE pattern. Verify manually: `echo "log line" | sed -En 's/PATTERN/\1/p'`.
4. **Add sample lines** with expected values to the rule definition.
5. **Run** `kronograf plugin validate plugins/my-stack.yml` to confirm all assertions pass.
6. **Submit** a pull request to the community plugin repository, or place the file in `.kronograf/plugins/` for project-local use.

The total effort to add a new tech stack with three to five KPIs is typically under two hours.

---

## 6. Key KPIs

### 6.1 Primary KPIs — Derivable from CI/CD Build Logs

| KPI | Primary Source | Fallback (Log Scrape) |
|---|---|---|
| Number of Tests | JUnit XML (`sum_tests`) | Runner summary line |
| Test Coverage (%) | JaCoCo / lcov / coverage.py XML | Coverage tool summary line |
| Build Warnings | Log scrape (versioned rules per compiler) | — |
| CVEs (by severity) | OWASP Dependency-Check XML | Audit tool summary line |
| Static Analysis Violations | Lint XML, Checkstyle XML, Detekt | Tool summary line |
| Lines of Code | `cloc` log output | — |

### 6.2 Secondary KPIs — Requiring External Sources (Extension Model)

| KPI | Source | Extension Type |
|---|---|---|
| Open Bug Count | Jira, GitHub Issues, GitLab Issues | REST API Adapter |
| Open Technical Debt Items | Jira (issue type filter), Azure DevOps | REST API Adapter |
| Mean Time to Resolve Issues | Issue tracker + timestamps | REST API Adapter |
| Deployment Frequency | CI/CD run history | Platform API Adapter |
| Failed Build Rate | CI/CD run history | Platform API Adapter |

---

## 7. Architecture

### 7.1 Design Philosophy

```
[Build Pipeline]
      │
      ▼
[1. KPI Extractor]  ──►  [2. Data Store (Git)]  ──►  [3. Chart Renderer]
  (CI/CD step)             (kronograf-data branch)      (SVG served via URL)
      │
      ▼
[Plugin Engine]
  Loads active plugins → resolves versioned rules → applies parsers/scrapers
```

No persistent server or database is strictly required. Kronograf runs entirely within CI/CD infrastructure and persists data in git.

### 7.2 Stage 1: KPI Extractor (CI Step)

A lightweight Python script runs as a CI/CD step after the build and test phases complete. It:

1. Loads and resolves active plugins from `kronograf.yml`.
2. For each metric in each active plugin, locates report files via configured glob patterns or the captured build log.
3. Detects tool versions from the log to select the correct versioned scrape rules.
4. Applies parsers/scrapers and aggregates values.
5. Assembles a single JSON data point and appends it to the data store.

```json
{
  "timestamp": "2026-02-24T10:30:00Z",
  "commit": "a3f9c12",
  "branch": "main",
  "plugin": "android-kotlin-gradle",
  "tool_versions": {
    "kotlin-compiler": "2.0.21",
    "gradle": "8.9",
    "detekt": "1.23.7"
  },
  "metrics": {
    "test_count": 412,
    "test_coverage_pct": 74.2,
    "build_warnings": 18,
    "lint_errors": 3,
    "lint_warnings": 47,
    "cve_critical": 0,
    "cve_high": 2,
    "cve_medium": 7,
    "detekt_issues": 23,
    "kotlin_loc": 14201
  }
}
```

### 7.3 Stage 2: Data Store

#### Option A — Git Data Branch (Recommended for v1)
A dedicated branch (`kronograf-data`) in the same repository holds a single append-only NDJSON file (`metrics.ndjson`). Each CI run appends one line using a CI bot token.

**Pros:** No external infrastructure. Data is versioned, auditable, and portable.  
**Cons:** Git is not optimized for append-heavy workloads. Acceptable for typical project cadences.

#### Option B — Companion Repository
A separate repository holds data for one or more projects. Useful for centralized metric storage across many repositories.

#### Option C — Lightweight Database (Extension)
An adapter for a time-series database (InfluxDB, TimescaleDB) or SQLite file in cloud storage for very high-frequency pipelines.

### 7.4 Stage 3: Chart Renderer

#### Model A — Static SVG Generation (CI-triggered, Recommended for v1)
After the extractor appends a data point, a second CI step regenerates SVG chart files and commits them to the `kronograf-data` branch. The `README.md` references these SVGs via a raw file URL:

```markdown
![Test Coverage](https://raw.githubusercontent.com/org/repo/kronograf-data/charts/test_coverage.svg)
```

#### Model B — On-Demand Renderer Service (v2)
A minimal web service reads the NDJSON data and renders SVG on request with optional query parameters. Deployable on GitHub Pages, GitLab Pages, or Cloudflare Workers.

---

## 8. Platform Compatibility

### 8.1 CI/CD Integration

| Platform | Integration Method |
|---|---|
| GitHub Actions | `uses: kronograf/kronograf-action@v1` |
| GitLab CI | `include:` remote template or Docker runner step |
| Bitbucket Pipelines | Pipe or script step |
| Jenkins | Pipeline step (sh/docker) |
| CircleCI, Travis, etc. | Orb or script step |

### 8.2 Built-in Plugin Coverage (Roadmap)

| Plugin ID | Stack | Status |
|---|---|---|
| `android-kotlin-gradle` | Android / Kotlin / Gradle | **v1.0 — reference plugin** |
| `android-java-gradle` | Android / Java / Gradle | v1.1 |
| `jvm-maven` | Java / Maven | v1.1 |
| `ios-swift-xcode` | iOS / Swift / Xcode | v1.2 |
| `react-jest` | React / Jest / npm | v1.2 |
| `python-pytest` | Python / pytest | v1.2 |
| `go-native` | Go / native toolchain | v1.3 |
| `cpp-cmake` | C++ / CMake / GCC | v1.3 |

Community-authored plugins are hosted in the `kronograf-plugins` repository and referenced by URL.

---

## 9. README Integration

Design requirements for Kronograf SVG charts:

- **SVG format** — renders natively on GitHub, GitLab, and most Git web UIs without JavaScript.
- **Self-contained** — no external font or script dependencies.
- **Dark/light mode aware** — uses CSS `prefers-color-scheme` within the SVG where supported.
- **Minimal size** — target under 15 KB per chart.
- **Trend indicators** — a small arrow or color delta indicating direction since the previous measurement period.

Example README section for an Android project:

```markdown
## Code Quality (last 90 days)

| Test Coverage | Build Warnings | Detekt Issues |
|:---:|:---:|:---:|
| ![Coverage](./charts/test_coverage_pct.svg) | ![Warnings](./charts/build_warnings.svg) | ![Detekt](./charts/detekt_issues.svg) |

| Lint Errors | Critical CVEs | High CVEs |
|:---:|:---:|:---:|
| ![Lint](./charts/lint_errors.svg) | ![CVE Critical](./charts/cve_critical.svg) | ![CVE High](./charts/cve_high.svg) |

*Auto-updated on every push to `main` by [Kronograf](https://github.com/kronograf/kronograf).*
```

---

## 10. Configuration

```yaml
# kronograf.yml

data_store:
  type: git_branch
  branch: kronograf-data

plugins:
  - id: android-kotlin-gradle

charts:
  output_dir: charts/
  window_days: 90
  theme: auto
  metrics:
    - test_count
    - test_coverage_pct
    - build_warnings
    - lint_errors
    - lint_warnings
    - cve_critical
    - cve_high
    - detekt_issues

log:
  path: build.log
```

---

## 11. GitHub-Only Variant: Effort Impact Analysis

| Concern | Cross-Platform | GitHub-Only | Effort Delta |
|---|---|---|---|
| CI integration | Generic script + per-platform examples | GitHub Actions native action only | ~30% less |
| Data store push | Generic git push with token | `GITHUB_TOKEN` built-in | ~15% less |
| Chart hosting | Raw file URL (generic) | raw.githubusercontent.com (pre-validated) | ~10% less |
| On-demand renderer | Requires neutral hosting | GitHub Pages + GitHub API | ~25% less (Model B) |
| Testing matrix | Multiple CI platforms | Single platform | ~40% less QA |
| Documentation | Per-platform setup guides | Single setup guide | ~20% less |

**Overall estimate:** A GitHub-only v1 would require roughly 40–50% of the effort of a fully cross-platform v1.

**Recommended strategy:** Build v1 as a GitHub-native implementation, but strictly isolate platform-specific code behind interfaces from day one. The plugin engine, data schema, and chart renderer are entirely platform-agnostic even in v1.

---

## 12. Extension Model for External KPI Sources

```python
class MetricAdapter:
    """Base class for all external KPI adapters."""

    def collect(self, config: dict) -> dict[str, float | int]:
        """Return a flat dict of metric_name -> numeric_value."""
        raise NotImplementedError
```

```yaml
# kronograf.yml
extensions:
  github_issues:
    enabled: true
    token_env: GH_ISSUES_TOKEN
    labels: ["bug", "tech-debt"]
    metrics:
      - id: open_bugs
        filter: {label: "bug", state: open}
      - id: open_tech_debt_items
        filter: {label: "tech-debt", state: open}
```

---

## 13. Historical Backfill Tool

### 13.1 Problem

When Kronograf is adopted in an existing repository, the data store starts collecting KPI data from that point forward. This leaves a gap: there is no historical context to show trends or to answer the question "was this better or worse six months ago?" Stakeholders seeing a chart for the first time with only a few weeks of data cannot understand the trajectory of the project.

The Kronograf Backfill Tool solves this by replaying the build and test pipeline for every merge commit on the main branch, from the very first commit to the present, and feeding the results into the data store as if Kronograf had been present all along.

### 13.2 Design Goals

- **Local execution** — runs on a developer's machine, not on the CI/CD server, to avoid consuming paid or shared CI compute minutes for what may be hundreds of historical builds.
- **Resumable** — if interrupted, picks up from the last successfully processed commit rather than starting over.
- **Non-destructive** — does not alter the repository's working tree permanently; uses git worktrees to build each commit in isolation.
- **Build-tool-agnostic** — uses the same plugin definitions as the main extractor, invoking whatever build command is configured in `kronograf.yml`.
- **Selective** — the operator can limit backfill to a specific date range, to every Nth commit, or to tagged releases only.
- **Output-compatible** — data points written by the backfill tool are identical in format to those written by the CI extractor.

### 13.3 Backfill Strategy: Merge Commits on Main

The default strategy targets **merge commits on the main branch**. This is a well-chosen subset because:

- Merge commits represent completed, reviewed units of work — the history points stakeholders actually care about in retrospect.
- Their number is typically an order of magnitude smaller than the total commit count.
- They align with the natural rhythm of the project (sprints, features, releases), making the resulting time-series charts meaningful rather than noisy.

Additional strategies selectable via CLI flag:

| Strategy | Flag | Use Case |
|---|---|---|
| Merge commits on main (default) | `--strategy merge-commits` | Most projects — best signal-to-noise ratio |
| All commits on main | `--strategy all-commits` | Small or slow-moving repos |
| Tagged commits only | `--strategy tags` | Release-oriented projects; smallest data set |
| Every Nth commit | `--strategy every-n --n 10` | Large repos where merge-commit density is still too high |
| Date range | `--since 2024-01-01 --until 2024-12-31` | Partial backfill after gap in data |

### 13.4 Tool Operation

#### 13.4.1 Prerequisites

The developer's machine must have the same build toolchain installed as the project requires (JDK, Android SDK, Gradle, etc.). Kronograf does not provision build environments — it assumes the same environment developers use for local builds. This is intentional: it keeps the tool simple and works naturally in the most common case where the developer already has the toolchain set up.

For projects where the toolchain changes significantly over the repository's history (e.g., a major JDK upgrade), the operator may need to run the backfill in two passes with different toolchain configurations, using `--since` / `--until` to target each era.

#### 13.4.2 Workflow

```
1. Read kronograf.yml to load plugin and build configuration.
2. Query git log to enumerate target commits (per selected strategy).
3. For each target commit (oldest to newest):
   a. Skip if a data point for this commit already exists in the data store.
   b. Create a temporary git worktree at the commit SHA.
   c. Execute the configured build command inside the worktree,
      capturing stdout and stderr to a log file.
   d. Run the Kronograf KPI extractor against the build output and report files.
   e. Write the data point to the data store, stamping it with the
      commit's original author timestamp (not the current wall-clock time).
   f. Destroy the temporary worktree.
4. After all commits are processed, trigger chart regeneration.
```

The use of `git worktree` avoids modifying the main working tree, so the developer can continue working in the repository while the backfill runs in the background or overnight.

#### 13.4.3 Timestamp Handling

Data points are written with the **original commit timestamp** (`git log --format=%aI`), not the time the backfill was executed. This means the resulting time-series charts show the correct historical timeline, and backfill data points are visually indistinguishable from data points collected in real time by the CI pipeline. A `backfill: true` flag is included in the JSON record for auditability but is not used by the chart renderer.

#### 13.4.4 CLI Interface

```bash
# Backfill the entire history using the default strategy (merge commits)
kronograf backfill

# Backfill only a specific date range
kronograf backfill --since 2023-06-01 --until 2024-01-01

# Backfill tagged commits only, with verbose output
kronograf backfill --strategy tags --verbose

# Dry run: show which commits would be processed without building
kronograf backfill --dry-run

# Resume an interrupted backfill (skips already-recorded commits automatically)
kronograf backfill --resume

# Limit parallel worktrees (default: 1; increase on powerful machines)
kronograf backfill --jobs 2

# Use a specific build command override (useful for legacy builds)
kronograf backfill --build-command "./gradlew clean test lintDebug dependencyCheckAnalyze"

# Show progress estimate based on median build time from last 5 completed commits
kronograf backfill --progress
```

#### 13.4.5 Configuration in kronograf.yml

```yaml
backfill:
  strategy: merge-commits
  every_n: 10
  branch: main
  build_command: >
    ./gradlew clean
    test
    lintDebug
    jacocoTestReport
    dependencyCheckAnalyze
    --no-daemon
    --quiet
  build_env:
    ANDROID_SDK_ROOT: "/Users/dev/Library/Android/sdk"
    CI: "true"
    ORG_GRADLE_PROJECT_disableSigning: "true"
  timeout_minutes: 20
  on_build_failure: record_null   # record_null | skip | abort
```

### 13.5 Handling Build Failures in Historical Commits

Older commits may not build successfully due to missing dependencies, expired certificates, build tool version incompatibilities, or broken intermediate states. Kronograf handles this via the `on_build_failure` policy:

- `record_null` (default): writes a data point with `null` for all metric values. In the chart, this renders as a visible gap — honest about the missing data rather than implying the metric was zero.
- `skip`: omits the data point entirely. The chart interpolates across the gap.
- `abort`: stops the backfill and reports the failing commit. Useful during initial setup to diagnose systematic build environment issues.

Kronograf always logs the full build output for each failed commit to `.kronograf/backfill-logs/<sha>.log` so failures can be investigated without re-running the build.

### 13.6 Progress and Runtime Estimation

A full backfill of a mature project (two to three years, hundreds of merge commits) may take several hours on a developer machine. Kronograf provides transparency about this:

- After each completed commit, it prints elapsed time and a rolling median build time.
- It estimates remaining time based on the median and the number of unprocessed commits.
- A progress file (`.kronograf/backfill-progress.json`) is written after each commit, checkable from another terminal via `kronograf backfill --progress`.
- The `--jobs 2` flag allows limited parallelism on machines with sufficient resources (typically 2 parallel worktrees for an Android project requires 16+ GB RAM).

### 13.7 Incremental Backfill After a Data Gap

The same tool fills gaps caused by CI pipeline outages, a period where Kronograf was disabled, or onboarding a new branch. Because the extractor's skip logic checks for an existing data point by commit SHA before building, running `kronograf backfill` at any time is safe and idempotent.

---

## 14. Milestones

### v1.0 — Core + Android Reference Plugin (GitHub-first)
- Plugin engine (YAML loading, version detection, rule resolution)
- Source types: `junit_xml`, `jacoco_xml`, `android_lint_xml`, `owasp_xml`, `log_scrape`
- Aggregate modes: all modes defined in §5.3
- `android-kotlin-gradle` reference plugin
- Plugin CLI (`validate`, `test`, `dry-run`, `list`)
- Git data branch storage (NDJSON append, branch: `kronograf-data`)
- Static SVG chart generator
- GitHub Actions composite action (`kronograf/kronograf-action@v1`)
- Backfill tool (all strategies, git worktree, resumable)
- `kronograf.yml` configuration
- README integration guide

### v1.1 — Expanded Plugin Coverage
- `android-java-gradle` and `jvm-maven` plugins
- GitLab CI template
- Community `kronograf-plugins` repository scaffold

### v2.0 — Cross-Platform & Extensions
- `ios-swift-xcode`, `react-jest`, `python-pytest` plugins
- Generic CI script (Jenkins, Bitbucket, CircleCI)
- Companion repository data store
- On-demand renderer service (Cloudflare Workers)
- GitHub Issues and GitLab Issues adapters
- Multi-project dashboard view

---

## 15. Success Metrics

- Time to first chart in README from zero setup: **< 30 minutes**
- Time to author a new three-KPI plugin: **< 2 hours**
- Extractor runtime overhead in CI: **< 60 seconds**
- Plugin sample assertion coverage: **100% of rules must have ≥ 1 passing sample**
- Chart file size: **< 15 KB per SVG**
- Data store size growth: **< 1 KB per build run**

---

## 16. Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Build tool upgrade changes log format, breaking scrape rules | High | Medium | Versioned rules in plugin YAML; old rules preserved, new rules added for new versions |
| Regex pattern too broad, overcounting warnings | Medium | Medium | Mandatory sample assertions with negative-match examples in every rule |
| CI log not captured to file | Medium | High | Document log capture step for each CI platform; provide `tee`-based fallback in CI template |
| Git data branch grows too large over time | Low | Medium | Provide a pruning script; document retention policy options |
| Raw SVG rendering disabled on future Git platform versions | Low | High | Model B (on-demand renderer) as fallback; static PNG fallback option |
| Token security for `kronograf-data` branch push | Medium | High | Document least-privilege token scoping; support GitHub App auth in v2 |
| Backfill fails on old commits due to toolchain mismatch | High | Low | `record_null` policy; date-range splitting documented in backfill guide |

---

*End of Document*
