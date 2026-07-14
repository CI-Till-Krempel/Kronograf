# User Story

<!-- AGENT SAFEGUARD: Do NOT implement or fill out this template file directly. -->
<!-- This is a blueprint. Always create a new file (e.g., US-0001-My-Story.md) for actual content. -->

- Story ID: US-0012
- Title: Run Multiple Plugins Concurrently
- Status: To Do
- Priority: P2
- Owner: Scrum Team
- Last Updated: 2026-07-14

## As a Platform Engineer, I want to activate and run multiple plugins concurrently, so that I can collect KPIs from different parts of my tech stack.

## Acceptance Criteria
- - Given `kronograf.yml` activates two or more different plugins, when the extractor runs, then it processes metrics from all active plugins.
- - Given two active plugins define a metric with the same `id`, when the extractor runs, then it produces a clear warning or error about the conflict and does not silently overwrite data.
- - Given a project uses both the built-in `android-kotlin-gradle` plugin and a custom plugin from a URL, when the extractor runs, then metrics from both plugins are collected and stored in the same data point.

## Notes


## Test Approach

