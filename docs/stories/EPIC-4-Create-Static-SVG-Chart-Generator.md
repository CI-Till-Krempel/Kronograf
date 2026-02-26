# User Story

- Story ID: EPIC-4
- Title: Create Static SVG Chart Generator
- Status: Ready
- Priority: P1
- Owner: Scrum Team
- Last Updated: 2026-02-26

## As an Engineering Lead, I want the system to generate time-series SVG charts from the stored metric data, so that I can embed them in my `README.md` to visualize quality trends.

## Acceptance Criteria
- Given a `metrics.ndjson` file in the data branch, When the chart generator is run, Then it creates one SVG file for each metric configured in `kronograf.yml`.
- Given a generated SVG chart, When viewed in a browser or GitHub README, Then it displays a clear time-series line graph of the metric's values.
- Given the configuration specifies a `window_days` of 90, When the chart is generated, Then it only includes data points from the last 90 days.
- Given the chart is generated, When committed to the data branch, Then it can be embedded in a Markdown file using a raw git URL and it renders correctly.
- Given the SVG file, When inspected, Then it is self-contained, under 15KB, and contains CSS for dark/light mode.

## Notes
The embeddable charts are the primary value proposition for the end-user. This feature makes the collected data visible and useful, closing the communication gap for stakeholders.
- Dependencies: EPIC-2

## Test Approach

