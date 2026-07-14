# User Story

<!-- AGENT SAFEGUARD: Do NOT implement or fill out this template file directly. -->
<!-- This is a blueprint. Always create a new file (e.g., US-0001-My-Story.md) for actual content. -->

- Story ID: US-0003
- Title: Configure Git-based Data Store
- Status: To Do
- Priority: P1
- Owner: Scrum Team
- Last Updated: 2026-07-14

## As a Platform Engineer, I want to configure the data store settings in `kronograf.yml`, so that I can specify which branch to use for storing KPI data.

## Acceptance Criteria
- - Given a `kronograf.yml` file, when the `data_store` section is present, then the extractor uses the specified branch name (e.g., `kronograf-metrics`) instead of the default (`kronograf-data`).
- - Given the `data_store` section specifies a `type` other than `git_branch`, when the extractor runs, then it produces a clear error message stating that only `git_branch` is supported in v1.0.
- - Given the `data_store` section is absent from `kronograf.yml`, when the extractor runs, then it defaults to using the `git_branch` type and the `kronograf-data` branch name.

## Notes


## Test Approach

