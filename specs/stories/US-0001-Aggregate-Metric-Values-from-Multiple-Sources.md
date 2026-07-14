# User Story

<!-- AGENT SAFEGUARD: Do NOT implement or fill out this template file directly. -->
<!-- This is a blueprint. Always create a new file (e.g., US-0001-My-Story.md) for actual content. -->

- Story ID: US-0001
- Title: Aggregate Metric Values from Multiple Sources
- Status: To Do
- Priority: P1
- Owner: Scrum Team
- Last Updated: 2026-07-14

## As a Plugin Author, I want the plugin engine to aggregate values from multiple sources for a single metric, so that I can define comprehensive metrics.

## Acceptance Criteria
- - Given a metric is configured with multiple `sources` (e.g., multiple JUnit XML file patterns), when the extractor runs, then the values from all sources are aggregated into a single metric value using the specified aggregation mode (e.g., `sum_tests`).
- - Given a log-scraped metric is configured with multiple `rules`, when the extractor runs, then the values extracted by each rule are aggregated using the `multi_rule_aggregate` mode (e.g., `sum`).
- - Given the configuration for a metric is invalid (e.g., an unknown `aggregate` mode is used), when the extractor runs, then it reports a clear error message.

## Notes


## Test Approach

