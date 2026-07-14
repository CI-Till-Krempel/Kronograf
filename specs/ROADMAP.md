# Product Roadmap

Use this living roadmap to plan releases and track user stories across states. It doubles as a lightweight task board and a release planning tool.

## How to use
- Story IDs should match files in `docs/stories/` (e.g., `ST-001` corresponds to `docs/stories/ST-001-some-title.md`).
- Move story references between states as work progresses.
- Keep titles short; full details live in the story file.
- For each planned version, list goals and the set of stories targeted for that release.
- When a release is cut, freeze the section by adding the actual tag (e.g., `v0.1.0`) and dates.

Legend
- `[ST-###] Title` → a user story reference and its short title
- Checkbox states: `- [ ]` To Do, `- [~]` In Progress (use `- [~]` to signal WIP), `- [R]` In Review, `- [x]` Done

Tip: If you prefer standard checkboxes only, use the Kanban tables below and keep raw lists unchecked.

---

## Release plan (versions → stories)
### v1.0
Goals
- Deliver a GitHub-native implementation of Kronograf with a fully-featured Android/Kotlin/Gradle plugin.
- Provide a seamless CI/CD integration with GitHub Actions.
- Enable users to generate historical data for their repositories.

Stories
- [ ] [EP-0001-Implement-the-core-Plugin-Engine] EP-0001-Implement-the-core-Plugin-Engine
- [ ] [EP-0002-Implement-Git-based-Data-Store] EP-0002-Implement-Git-based-Data-Store
- [ ] [EP-0003-Build-the-Android-Kotlin-Gradle-Reference-Plugin] EP-0003-Build-the-Android-Kotlin-Gradle-Reference-Plugin
- [ ] [EP-0004-Create-Static-SVG-Chart-Generator] EP-0004-Create-Static-SVG-Chart-Generator
- [ ] [EP-0005-Create-Plugin-Author-CLI] EP-0005-Create-Plugin-Author-CLI
- [ ] [EP-0006-Create-GitHub-Actions-Integration] EP-0006-Create-GitHub-Actions-Integration
- [ ] [EP-0007-Build-Historical-Backfill-Tool] EP-0007-Build-Historical-Backfill-Tool
- [ ] [US-0001-Aggregate-Metric-Values-from-Multiple-Sources] US-0001-Aggregate-Metric-Values-from-Multiple-Sources
- [ ] [US-0002-Append-KPI-Data-to-NDJSON-File-in-Git-Branch] US-0002-Append-KPI-Data-to-NDJSON-File-in-Git-Branch
- [ ] [US-0003-Configure-Git-based-Data-Store] US-0003-Configure-Git-based-Data-Store
- [ ] [US-0004-Detect-Tool-Versions-from-Build-Logs] US-0004-Detect-Tool-Versions-from-Build-Logs
- [ ] [US-0005-Extract-Build-Warnings-and-Static-Analysis-Issues-from-Android-Gradle-Build] US-0005-Extract-Build-Warnings-and-Static-Analysis-Issues-from-Android-Gradle-Build
- [ ] [US-0006-Extract-CVE-Counts-from-OWASP-Dependency-Check-Report] US-0006-Extract-CVE-Counts-from-OWASP-Dependency-Check-Report
- [ ] [US-0007-Extract-Lines-of-Code-from-cloc-Tool-Output] US-0007-Extract-Lines-of-Code-from-cloc-Tool-Output
- [ ] [US-0008-Extract-Test-Count-and-Coverage-from-Android-Gradle-Build] US-0008-Extract-Test-Count-and-Coverage-from-Android-Gradle-Build
- [ ] [US-0009-Implement-Log-Scraper-with-Versioned-Rules] US-0009-Implement-Log-Scraper-with-Versioned-Rules
- [ ] [US-0010-Load-and-Parse-Plugin-Definitions] US-0010-Load-and-Parse-Plugin-Definitions
- [ ] [US-0011-Parse-Structured-Reports] US-0011-Parse-Structured-Reports
- [ ] [US-0012-Run-Multiple-Plugins-Concurrently] US-0012-Run-Multiple-Plugins-Concurrently

