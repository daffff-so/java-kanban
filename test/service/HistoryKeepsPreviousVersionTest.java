package service;

import model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryKeepsPreviousVersionTest {

    @Test
    void historyShouldKeepPreviousVersionOfTaskAfterUpdate() {
        TaskManager manager = Managers.getDefault();

        Task original = manager.createTask(new Task("Old name", "Old desc", Status.NEW));
        int id = original.getId();

        manager.getTask(id);

        Task updated = new Task("New name", "New desc", Status.DONE);
        updated.setId(id);
        manager.updateTask(updated);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());

        Task fromHistory = history.get(0);
        assertEquals(id, fromHistory.getId());
        assertEquals("Old name", fromHistory.getName());
        assertEquals("Old desc", fromHistory.getDescription());
        assertEquals(Status.NEW, fromHistory.getStatus());
    }

    @Test
    void historyShouldKeepPreviousVersionOfSubtaskAfterUpdate() {
        TaskManager manager = Managers.getDefault();

        Epic epic = manager.createEpic(new Epic("Epic", "E"));
        Subtask original = manager.createSubtask(new Subtask("Sub old", "S old", Status.NEW, epic.getId()));
        int id = original.getId();

        manager.getSubtask(id);

        Subtask updated = new Subtask("Sub new", "S new", Status.DONE, epic.getId());
        updated.setId(id);
        manager.updateSubtask(updated);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());

        Task fromHistory = history.get(0);
        assertEquals(id, fromHistory.getId());
        assertEquals("Sub old", fromHistory.getName());
        assertEquals("S old", fromHistory.getDescription());
        assertEquals(Status.NEW, fromHistory.getStatus());
    }

    @Test
    void historyShouldKeepPreviousVersionOfEpicAfterUpdate() {
        TaskManager manager = Managers.getDefault();

        Epic original = manager.createEpic(new Epic("Epic old", "Desc old"));
        int id = original.getId();

        manager.getEpic(id);

        Epic updated = new Epic("Epic new", "Desc new");
        updated.setId(id);
        manager.updateEpic(updated);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());

        Task fromHistory = history.get(0);
        assertEquals(id, fromHistory.getId());
        assertEquals("Epic old", fromHistory.getName());
        assertEquals("Desc old", fromHistory.getDescription());
        assertEquals(Status.NEW, fromHistory.getStatus());
    }
}
