# User Story

- Story ID: EPIC-5
- Title: Create Plugin Author CLI
- Status: Ready
- Priority: P1
- Owner: Scrum Team
- Last Updated: 2026-02-26

## As a Plugin Author, I want a set of CLI commands to validate and test my plugin definitions, so that I can confidently develop new plugins for any tech stack without requiring a full CI pipeline.

## Acceptance Criteria
- Given a plugin YAML file with correct syntax and passing sample assertions, When I run `kronograf plugin validate <file>`, Then the command exits with a success code and reports the plugin is valid.
- Given a plugin YAML file with a syntax error or a failing `sample_lines` assertion, When I run `kronograf plugin validate <file>`, Then the command exits with an error code and prints a descriptive error message.
- Given a plugin file and a real build log, When I run `kronograf plugin test <plugin_file> --log <log_file>`, Then the command runs all configured rules against the log and reports the extracted values.
- Given a project with a `kronograf.yml` file, When I run `kronograf plugin list`, Then the command lists all plugins activated for the project.
- Given a build log and a plugin, When I run `kronograf plugin dry-run --log <log_file> --plugin <plugin_id>`, Then the output shows exactly which rules from the plugin matched lines in the log and what values were extracted.

## Notes
A strong local development and testing story for plugins is crucial for building a community and enabling the "no-code" extensibility promised in the vision. This CLI is key to that experience.
- Dependencies: EPIC-1

## Test Approach

