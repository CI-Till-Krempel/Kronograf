# User Story

- Story ID: EPIC-2
- Title: Implement Git-based Data Store
- Status: In Progress
- Priority: P0
- Owner: Scrum Team
- Last Updated: 2026-02-26

## As a DevOps Engineer, I want Kronograf to store its time-series data in an append-only NDJSON file within a dedicated git branch, so that the quality metrics are versioned, auditable, and don't require external database infrastructure.

## Acceptance Criteria
- Given the KPI extractor has produced a JSON data point for a commit, When the data store stage is executed, Then the system checks out the configured data branch (e.g., `kronograf-data`), creating it if it doesn't exist.
- Given a new JSON data point, When the data store stage runs, Then the JSON data point is appended as a new line to the `metrics.ndjson` file in the data branch.
- Given the `metrics.ndjson` file has been updated, When the data store stage completes, Then the changes are committed and pushed back to the remote repository using a dedicated CI token.
- Given the extractor runs for a commit that already has a data point in `metrics.ndjson`, When the data store stage is executed, Then it does not write a duplicate entry.

## Notes
A git-native data store is a key differentiator, making the tool lightweight and easy to adopt. This feature is critical for the "no external services" value proposition.
- Dependencies: EPIC-1 (Plugin Engine to generate data)

## Test Approach
The logic will be tested at two levels. Unit tests will mock the JGit library to verify the correctness of our data handling and decision logic (e.g., duplicate checking). Integration tests will use temporary file-system-based Git repositories to test the full, end-to-end process of creating branches, committing files, and pushing between local clones without any network dependency.
