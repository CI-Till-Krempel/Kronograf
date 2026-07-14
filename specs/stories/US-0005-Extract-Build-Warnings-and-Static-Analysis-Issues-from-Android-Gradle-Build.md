# User Story

<!-- AGENT SAFEGUARD: Do NOT implement or fill out this template file directly. -->
<!-- This is a blueprint. Always create a new file (e.g., US-0001-My-Story.md) for actual content. -->

- Story ID: US-0005
- Title: Extract Build Warnings and Static Analysis Issues from Android/Gradle Build
- Status: To Do
- Priority: P1
- Owner: Scrum Team
- Last Updated: 2026-07-14

## As an Android Developer, I want to extract build warnings and static analysis issues so that I can monitor code quality.

## Acceptance Criteria
- - Given the `android-kotlin-gradle` plugin is enabled, when the extractor runs against a build log with Kotlin and Gradle warnings, then the `build_warnings` metric is correctly extracted and aggregated.
- - Given the extractor runs against a project with Android Lint XML reports, then the `lint_errors` and `lint_warnings` metrics are correctly extracted.
- - Given the extractor runs against a project that uses `detekt`, then the `detekt_issues` metric is correctly extracted from the build log.
- - Given the build uses a version of the Kotlin compiler that changes the warning format, when the extractor runs, then the correct versioned rule is applied to parse the warnings.

## Notes


## Test Approach

