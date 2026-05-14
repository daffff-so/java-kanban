package service;

import model.*;
import org.junit.jupiter.api.Test;

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
    void deletedTaskShouldBeRemovedFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description 1", Status.NEW);
        manager.createTask(task);

        manager.getTask(task.getId());
        assertEquals(1, manager.getHistory().size());

        manager.deleteTaskById(task.getId());

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void deletedSubtaskShouldBeRemovedFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        manager.getSubtask(subtask.getId());
        assertEquals(1, manager.getHistory().size());

        manager.deleteSubtaskById(subtask.getId());

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void deletedEpicShouldRemoveEpicAndSubtasksFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.getEpic(epic.getId());
        manager.getSubtask(subtask1.getId());
        manager.getSubtask(subtask2.getId());

        assertEquals(3, manager.getHistory().size());

        manager.deleteEpicById(epic.getId());

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void deletedSubtaskShouldBeRemovedFromEpicIdList() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        manager.deleteSubtaskById(subtask.getId());

        assertTrue(epic.getIdList().isEmpty());
    }

    @Test
    void clearSubtasksShouldClearEpicIdList() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.clearSubtasks();

        assertTrue(epic.getIdList().isEmpty());
    }

    @Test
    void deletedEpicShouldDeleteItsSubtasks() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getSubtask(subtask.getId()));
    }
}
