# User Story

<!-- AGENT SAFEGUARD: Do NOT implement or fill out this template file directly. -->
<!-- This is a blueprint. Always create a new file (e.g., US-0001-My-Story.md) for actual content. -->

- Story ID: US-0007
- Title: Extract Lines of Code from cloc Tool Output
- Status: To Do
- Priority: P2
- Owner: Scrum Team
- Last Updated: 2026-07-14

## As a Developer, I want the plugin to extract lines of Kotlin code from cloc output, so I can track codebase growth.

## Acceptance Criteria
- - Given the `android-kotlin-gradle` plugin is enabled, when the extractor runs against a build log that includes output from the `cloc` tool, then the `kotlin_loc` metric is correctly extracted.
- - Given the `cloc` output shows zero lines of Kotlin code, then the metric is correctly reported as 0.
- - Given the `cloc` output is not present in the build log, when the extractor runs, then it logs a warning but does not fail, and reports the `kotlin_loc` metric as null.

## Notes


## Test Approach

