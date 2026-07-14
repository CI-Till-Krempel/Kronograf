# User Story

<!-- AGENT SAFEGUARD: Do NOT implement or fill out this template file directly. -->
<!-- This is a blueprint. Always create a new file (e.g., US-0001-My-Story.md) for actual content. -->

- Story ID: US-0004
- Title: Detect Tool Versions from Build Logs
- Status: To Do
- Priority: P0
- Owner: Scrum Team
- Last Updated: 2026-07-14

## As a Plugin Author, I want the plugin engine to automatically detect tool versions from the build log, so that the correct versioned rules are applied.

## Acceptance Criteria
- - Given a build log containing a recognizable version string for a tool (e.g., 'Gradle 8.9'), when the extractor runs, then it correctly identifies and stores the version number.
- - Given a plugin rule with a `version_range` specified (e.g., '>=2.0.0'), when the engine has detected a matching tool version, then the rule is correctly selected for execution.
- - Given a plugin rule with a `version_range` specified, when the engine has detected a tool version that does not match the range, then the rule is skipped.
- - Given the engine cannot detect a tool version from the log, when a plugin has a fallback rule with `version_range: '*'`, then that rule is selected.

## Notes


## Test Approach

