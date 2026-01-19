# Java Kanban (In-Memory Task Tracker)

A simple in-memory task tracker written in Java.  
The project supports **Tasks**, **Epics**, and **Subtasks**, calculates **Epic status** based on its subtasks, and keeps a **view history** of the last 10 opened items. The code is covered with basic **JUnit** tests.

## Features

### Task management
- Create / read / update / delete:
  - `Task`
  - `Epic`
  - `Subtask`
- Get lists of all tasks, epics, and subtasks
- Get all subtasks of a specific epic

### Epic status calculation
Epic status is updated automatically based on its subtasks:
- **NEW** — if all subtasks are NEW (or there are no subtasks)
- **DONE** — if all subtasks are DONE
- **IN_PROGRESS** — in all other cases (mixed statuses)

### View history (last 10)
- The manager stores the last **10** viewed items (duplicates are allowed)
- Viewing means calling:
  - `getTask(id)`
  - `getEpic(id)`
  - `getSubtask(id)`
- `getHistory()` returns `List<Task>` (polymorphism allows different task types)

## Project structure

- `model/`
  - `Task`, `Epic`, `Subtask`, `Status`, `TaskType` (if used)
- `service/`
  - `TaskManager` — interface for task managers
  - `InMemoryTaskManager` — in-memory implementation of `TaskManager`
  - `HistoryManager` — interface for history tracking
  - `InMemoryHistoryManager` — in-memory implementation of `HistoryManager`
  - `Managers` — factory/utility class that provides default implementations

## How to run

### Run the application
Open the project in IntelliJ IDEA and run `Main`.

### Run tests
Run tests from IntelliJ, or use your build/run configuration for JUnit.
All tests are located under:
- `test/java/...`

## Notes
- The project stores all data **in RAM** (no persistence).
- The output directory `out/` is build/IDE-generated and should not be committed.
