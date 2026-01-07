package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Task getTask(int id);

    List<Task> getAllTasks();

    List<Task> getHistory();

    void updateTask(Task task);

    void deleteTaskById(int id);

    void clearTasks();

    Epic createEpic(Epic epic);

    Epic getEpic(int id);

    List<Epic> getAllEpics();

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    void clearEpics();

    Subtask createSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    List<Subtask> getAllSubtasks();

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    void clearSubtasks();

    List<Subtask> getSubtasksOfEpic(int epicId);
}
