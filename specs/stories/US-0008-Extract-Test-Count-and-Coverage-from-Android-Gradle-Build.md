# User Story

<!-- AGENT SAFEGUARD: Do NOT implement or fill out this template file directly. -->
<!-- This is a blueprint. Always create a new file (e.g., US-0001-My-Story.md) for actual content. -->

- Story ID: US-0008
- Title: Extract Test Count and Coverage from Android/Gradle Build
- Status: To Do
- Priority: P1
- Owner: Scrum Team
- Last Updated: 2026-07-14

## As a Developer using Android, I want the reference plugin to extract test count and coverage, so I can track my project's testing state.

## Acceptance Criteria
- - Given a `kronograf.yml` file with the `android-kotlin-gradle` plugin enabled, when the extractor runs on a project with JUnit XML reports, then the `test_count` metric is correctly extracted.
- - Given the extractor runs on a project with JaCoCo XML reports, then the `test_coverage_pct` metric is correctly extracted.
- - Given the plugin is run against test reports from both unit tests and Android instrumented tests, then the metrics are correctly aggregated.

## Notes


## Test Approach

