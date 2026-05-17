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
}
