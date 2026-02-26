# User Story

- Story ID: US-XXXX
- Title: Task Management within Projects
- Status: In Progress
- Priority: P2
- Owner: Scrum Team
- Last Updated: 2026-02-26

## As a team member, I want to manage tasks within a project, so that I can track my work and collaborate with my team.

## Acceptance Criteria
- Given a user has created a project, when they view the project, they can add tasks with descriptions and due dates.
- Given a task has been created, when a user marks it as complete, the task's status is updated.
- Given a user is viewing a project, when they filter by task status, they can see a list of all complete or incomplete tasks.

## Notes
Effective task management is core to our value proposition. We will validate this by measuring the average number of tasks created per project.

## Test Approach
Unit tests will ensure the `TaskService` correctly handles task logic. Integration tests will verify the API endpoints for task management. Manual testing will focus on the user interaction with the task list, including creating, completing, and filtering tasks, to ensure a smooth user experience.
