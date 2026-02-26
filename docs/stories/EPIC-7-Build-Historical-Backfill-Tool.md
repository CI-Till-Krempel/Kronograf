# User Story

- Story ID: EPIC-7
- Title: Build Historical Backfill Tool
- Status: Ready
- Priority: P2
- Owner: Scrum Team
- Last Updated: 2026-02-26

## As an Engineering Lead adopting Kronograf in a mature repository, I want a CLI tool to generate historical KPI data from past commits, so that my charts show meaningful long-term trends from day one.

## Acceptance Criteria
- Given I run `kronograf backfill` in my repository, When the tool executes, Then it iterates through the git history (e.g., merge commits on main), checks out each commit into a temporary worktree, and runs the configured build command.
- Given a historical build completes successfully, When the extractor runs, Then a data point is appended to the data store stamped with the original commit timestamp, not the current time.
- Given a backfill operation is interrupted, When I re-run the `kronograf backfill` command, Then the tool automatically skips commits that already have data points and resumes from where it left off.
- Given a historical commit fails to build, When the tool processes it, Then it handles the failure according to the `on_build_failure` policy in `kronograf.yml` (e.g., records nulls) and continues.
- Given I run the tool with `--dry-run`, When it executes, Then it prints the list of commits it would process without actually running any builds.

## Notes
The backfill tool solves the "empty chart" problem for existing projects, turning Kronograf from a "nice tool for the future" into an "insightful tool today." This is a killer feature for driving adoption in established teams.
- Dependencies: EPIC-1, EPIC-2

## Test Approach

