# User Story

- Story ID: EPIC-1
- Title: Implement the core Plugin Engine
- Status: In Progress
- Priority: P0
- Owner: Scrum Team
- Last Updated: 2026-02-26

## As a Plugin Author, I want a core engine that can load YAML plugin files, detect tool versions from logs, and apply the correct versioned rules, so that I can define and test KPI extraction logic for any build tool.

## Acceptance Criteria
- Given a valid `kronograf.yml` file and a set of plugin YAML files, When the engine is invoked, Then it correctly loads and parses all plugin definitions.
- Given a build log file containing a tool version string (e.g., "Gradle 8.9"), When the engine runs version detection, Then it correctly identifies the tool and its semantic version.
- Given a plugin with multiple versioned rules for a metric, When the engine processes a log file with a detected tool version, Then it selects and applies only the rule whose `version_range` matches the detected version.
- Given a rule with a `log_scrape` source, When the engine processes a log file, Then it correctly applies the regex pattern and `aggregate` mode (e.g., `count_matches`, `extract_first`) to produce the expected metric value.
- Given a rule with a structured report source (e.g., `junit_xml`), When the engine is pointed at a directory of report files, Then it correctly parses the files and aggregates the metric value as specified.

## Notes
A robust and testable plugin engine is the foundation of Kronograf. Getting this right will enable rapid, parallel development of support for new tech stacks and is the highest-value component for validating the core product concept.
- Dependencies: None. This is the foundational component.

## Test Approach
This epic will be heavily unit-tested. We will practice Test-Driven Development (TDD), creating test cases for each piece of logic before implementation. This includes tests with sample plugin files, sample log files, and sample JUnit XML reports to cover all acceptance criteria.
