package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskEqualityTest {

    @Test
    void tasksShouldBeEqualIfIdsEqual() {
        Task t1 = new Task("a", "b", Status.NEW);
        Task t2 = new Task("x", "y", Status.DONE);

        t1.setId(1);
        t2.setId(1);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void epicsShouldBeEqualIfIdsEqual() {
        Epic e1 = new Epic("e1", "d1");
        Epic e2 = new Epic("e2", "d2");

        e1.setId(10);
        e2.setId(10);

        assertEquals(e1, e2);
    }

    @Test
    void subtasksShouldBeEqualIfIdsEqual() {
        Subtask s1 = new Subtask("s1", "d", Status.NEW, 5);
        Subtask s2 = new Subtask("s2", "d", Status.DONE, 5);

        s1.setId(7);
        s2.setId(7);

        assertEquals(s1, s2);
    }
}

