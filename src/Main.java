import model.*;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        /* по ТЗ не очень понятно, что именно нужно писать в мейн и нужно ли оставлять тут тесты, поэтому просто оформила
        здесь то, как проводила тесты */

        System.out.println("СОЗДАЮ ЗАДАЧИ");
        Task t1 = manager.createTask(new Task("Задание 1", "Описание 1", Status.NEW));
        Task t2 = manager.createTask(new Task("Задание 2", "Описание 2", Status.NEW));
        System.out.println("Задачи: " + manager.getAllTasks());

        System.out.println("\nСОЗДАЮ ЭПИКИ");
        Epic e1 = manager.createEpic(new Epic("Проект 1", "Эпик с 2 подзадачами"));
        Epic e2 = manager.createEpic(new Epic("Проект 2", "Эпик с 1 подзадачей"));
        System.out.println("Эпики: " + manager.getAllEpics());

        System.out.println("\nСОЗДАЮ ПОДЗАДАЧИ");
        Subtask s11 = manager.createSubtask(new Subtask("Подзадача 1.1", "Сделать часть 1", Status.NEW, e1.getId()));
        Subtask s12 = manager.createSubtask(new Subtask("Подзадача 1.2", "Сделать часть 2", Status.NEW, e1.getId()));
        Subtask s21 = manager.createSubtask(new Subtask("Подзадача 2.1", "Сделать часть 1", Status.NEW, e2.getId()));
        System.out.println("Подзадачи: " + manager.getAllSubtasks());

        System.out.println("\nПодзадачи эпика e1: " + manager.getSubtasksOfEpic(e1.getId()));
        System.out.println("Подзадачи эпика e2: " + manager.getSubtasksOfEpic(e2.getId()));

        System.out.println("\nПРОВЕРЯЮ СТАТУСЫ ЭПИКОВ ПОСЛЕ СОЗДАНИЯ");
        System.out.println("Эпик e1: " + manager.getEpicById(e1.getId())); // NEW
        System.out.println("Эпик e2: " + manager.getEpicById(e2.getId())); // NEW

        System.out.println("\nМЕНЯЮ СТАТУСЫ ПОДЗАДАЧ И СМОТРЮ, КАК МЕНЯЕТСЯ ЭПИК");

        Subtask s11InProgress = new Subtask("Подзадача 1.1", "Сделать часть 1", Status.IN_PROGRESS, e1.getId());
        s11InProgress.setId(s11.getId());
        manager.updateSubtask(s11InProgress);
        System.out.println("После s11=IN_PROGRESS, эпик e1: " + manager.getEpicById(e1.getId()));

        Subtask s12Done = new Subtask("Подзадача 1.2", "Сделать часть 2", Status.DONE, e1.getId());
        s12Done.setId(s12.getId());
        manager.updateSubtask(s12Done);
        System.out.println("После s12=DONE, эпик e1: " + manager.getEpicById(e1.getId()));

        Subtask s11Done = new Subtask("Подзадача 1.1", "Сделать часть 1", Status.DONE, e1.getId());
        s11Done.setId(s11.getId());
        manager.updateSubtask(s11Done);
        System.out.println("После s11=DONE, эпик e1: " + manager.getEpicById(e1.getId()));

        System.out.println("\nУДАЛЯЮ ОДНУ ЗАДАЧУ И ОДИН ЭПИК");
        manager.deleteTaskById(t2.getId());
        System.out.println("Задачи после удаления t2: " + manager.getAllTasks());

        manager.deleteEpicById(e2.getId());
        System.out.println("Эпики после удаления e2: " + manager.getAllEpics());
        System.out.println("Подзадачи после удаления e2 (s21 должна исчезнуть): " + manager.getAllSubtasks());
        System.out.println("Подзадачи эпика e2 (должно быть пусто): " + manager.getSubtasksOfEpic(e2.getId()));

        System.out.println("\nФИНАЛЬНОЕ СОСТОЯНИЕ");
        System.out.println("Задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());
    }
}
