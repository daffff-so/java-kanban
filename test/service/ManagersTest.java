package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultShouldReturnWorkingTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
        assertNotNull(manager.getAllTasks());
        assertNotNull(manager.getHistory());
    }

    @Test
    void getDefaultHistoryShouldReturnWorkingHistoryManager() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history);
        assertNotNull(history.getHistory());
    }
}
