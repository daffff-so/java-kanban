import model.*;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task t1 = manager.createTask(new Task("Task 1", "Desc", Status.NEW));
        Epic e1 = manager.createEpic(new Epic("Epic 1", "Epic desc"));
        Subtask s1 = manager.createSubtask(new Subtask("Sub 1", "Sub desc", Status.NEW, e1.getId()));

        manager.getTask(t1.getId());
        manager.getEpic(e1.getId());
        manager.getSubtask(s1.getId());

        System.out.println("History:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

