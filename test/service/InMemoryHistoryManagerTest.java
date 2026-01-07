package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void historyShouldKeepOnlyLast10() {
        HistoryManager history = Managers.getDefaultHistory();

        for (int i = 1; i <= 11; i++) {
            Task t = new Task("t" + i, "d" + i, Status.NEW);
            t.setId(i);
            history.add(t);
        }

        List<Task> h = history.getHistory();
        assertEquals(10, h.size());
        assertEquals(2, h.get(0).getId());
        assertEquals(11, h.get(9).getId());
    }
}
