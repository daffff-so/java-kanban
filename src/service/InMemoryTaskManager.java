package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int nextId = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int generateId() {

        return nextId++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subs = getSubtasksOfEpic(epicId);

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

    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {

        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    //EPIC methods

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id,epic);
        return epic;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {

        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (!epics.containsKey(id)) return;

        epics.put(id, epic);

        updateEpicStatus(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) return;

        for (Integer subtaskId : epic.getIdList()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    //SUBTASKS methods

    @Override
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

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {

        return new ArrayList<>(subtasks.values());
    }

    @Override
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

    @Override
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

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearList();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) return result;

        for (Integer subtaskId : epic.getIdList()) {
            Subtask st = subtasks.get(subtaskId);
            if (st != null) result.add(st);
        }
        return result;
    }
}
