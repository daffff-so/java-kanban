package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        ArrayList<Subtask> subs = getSubtasksOfEpic(epicId);

        if (subs.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask st : subs) {
            if (st.getStatus() != Status.NEW) allNew = false;
            if (st.getStatus() != Status.DONE) allDone = false;
        }

        if (allNew) epic.setStatus(Status.NEW);
        else if (allDone) epic.setStatus(Status.DONE);
        else epic.setStatus(Status.IN_PROGRESS);
    }

    //TASK methods

    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    //EPIC methods

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id,epic);
        return epic;
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (!epics.containsKey(id)) return;

        epics.put(id, epic);

        updateEpicStatus(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) return;

        for (Integer subtaskId : epic.getIdList()) {
            subtasks.remove(subtaskId);
        }
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    //SUBTASKS methods

    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }

        int id = generateId();
        subtask.setId(id);

        subtasks.put(id, subtask);
        epic.addSubtaskId(id);

        updateEpicStatus(epicId);
        return subtask;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        Subtask old = subtasks.get(id);
        if (old == null) return;

        if (old.getEpicId() != subtask.getEpicId()) return;

        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) return;

        subtasks.put(id, subtask);
        updateEpicStatus(epicId);
    }

    public void deleteSubtaskById(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed == null) return;

        int epicId = removed.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.deleteSubtaskId(id);
            updateEpicStatus(epicId);
        }
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearList();
            updateEpicStatus(epic.getId());
        }
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) return result;

        for (Integer subtaskId : epic.getIdList()) {
            Subtask st = subtasks.get(subtaskId);
            if (st != null) result.add(st);
        }
        return result;
    }
}
