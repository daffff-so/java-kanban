package service;

import model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void shouldCreateAndFindTaskById() {
        TaskManager manager = Managers.getDefault();

        Task created = manager.createTask(new Task("t", "d", Status.NEW));
        Task found = manager.getTask(created.getId());

        assertNotNull(found);
        assertEquals(created, found);
    }

    @Test
    void shouldCreateAndFindEpicAndSubtaskById() {
        TaskManager manager = Managers.getDefault();

        Epic epic = manager.createEpic(new Epic("e", "d"));
        Subtask sub = manager.createSubtask(new Subtask("s", "d", Status.NEW, epic.getId()));

        assertEquals(epic, manager.getEpic(epic.getId()));
        assertEquals(sub, manager.getSubtask(sub.getId()));
    }

    @Test
    void shouldReturnTasksSortedByStartTime() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task(
                "Task 1",
                "Description 1",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 12, 0)
        );

        Task task2 = new Task(
                "Task 2",
                "Description 2",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(task2, prioritizedTasks.get(0));
        assertEquals(task1, prioritizedTasks.get(1));
    }

    @Test
    void taskWithoutStartTimeShouldNotBeInPrioritizedTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description 1", Status.NEW);

        manager.createTask(task);

        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void subtaskShouldBeInPrioritizedTasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(
                "Subtask 1",
                "Description 1",
                Status.NEW,
                epic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        manager.createSubtask(subtask);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(1, prioritizedTasks.size());
        assertEquals(subtask, prioritizedTasks.get(0));
    }

    @Test
    void shouldNotAddTaskWithTimeOverlap() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task(
                "Task 1",
                "Description 1",
                Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        Task task2 = new Task(
                "Task 2",
                "Description 2",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 10, 30)
        );

        manager.createTask(task1);
        Task result = manager.createTask(task2);

        assertNull(result);
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @Test
    void epicTimeShouldBeCalculatedFromSubtasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask 1",
                "Description 1",
                Status.NEW,
                epic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        Subtask subtask2 = new Subtask(
                "Subtask 2",
                "Description 2",
                Status.NEW,
                epic.getId(),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 1, 1, 12, 0)
        );

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic savedEpic = manager.getEpic(epic.getId());

        assertEquals(Duration.ofMinutes(90), savedEpic.getDuration());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), savedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2024, 1, 1, 13, 0), savedEpic.getEndTime());
    }

    @Test
    void epicTimeShouldBeRecalculatedAfterSubtaskDeletion() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(
                "Subtask 1",
                "Description 1",
                Status.NEW,
                epic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        Subtask subtask2 = new Subtask(
                "Subtask 2",
                "Description 2",
                Status.NEW,
                epic.getId(),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 1, 1, 12, 0)
        );

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteSubtaskById(subtask2.getId());

        Epic savedEpic = manager.getEpic(epic.getId());

        assertEquals(Duration.ofMinutes(30), savedEpic.getDuration());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), savedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 30), savedEpic.getEndTime());
    }
}
