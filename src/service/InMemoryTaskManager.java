package service;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int nextId = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime)
                    .thenComparing(Task::getId)
    );

    private int generateId() {

        return nextId++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpicStatus(int epicId) {
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

    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }

        List<Subtask> subs = getSubtasksOfEpic(epicId);

        Duration duration = subs.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime startTime = subs.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = subs.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        if (task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }

        return task1.getStartTime().isBefore(task2.getEndTime())
                && task2.getStartTime().isBefore(task1.getEndTime());
    }

    private boolean hasTimeOverlap(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .anyMatch(existingTask -> isTasksOverlap(existingTask, task));
    }

    private void removeFromPrioritizedTasks(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    //TASK methods

    @Override
    public Task createTask(Task task) {
        if (hasTimeOverlap(task)) {
            return null;
        }

        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        addToPrioritizedTasks(task);
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
        Task oldTask = tasks.get(id);

        if (oldTask == null) {
            return;
        }

        removeFromPrioritizedTasks(oldTask);

        if (hasTimeOverlap(task)) {
            addToPrioritizedTasks(oldTask);
            return;
        }

        tasks.put(id, task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void deleteTaskById(int id) {
        Task removed = tasks.remove(id);

        removeFromPrioritizedTasks(removed);
        historyManager.remove(id);
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            removeFromPrioritizedTasks(task);
            historyManager.remove(task.getId());
        }

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

        if (epic == null) {
            return;
        }

        for (Integer subtaskId : epic.getIdList()) {
            Subtask subtask = subtasks.remove(subtaskId);
            removeFromPrioritizedTasks(subtask);
            historyManager.remove(subtaskId);
        }

        historyManager.remove(id);
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }

        for (Subtask subtask : subtasks.values()) {
            removeFromPrioritizedTasks(subtask);
            historyManager.remove(subtask.getId());
        }

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

        if (hasTimeOverlap(subtask)) {
            return null;
        }

        int id = generateId();
        subtask.setId(id);

        subtasks.put(id, subtask);
        epic.addSubtaskId(id);
        addToPrioritizedTasks(subtask);

        updateEpicStatus(epicId);
        updateEpicTime(epicId);
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

        if (old == null) {
            return;
        }

        if (old.getEpicId() != subtask.getEpicId()) {
            return;
        }

        int epicId = subtask.getEpicId();

        if (!epics.containsKey(epicId)) {
            return;
        }

        removeFromPrioritizedTasks(old);

        if (hasTimeOverlap(subtask)) {
            addToPrioritizedTasks(old);
            return;
        }

        subtasks.put(id, subtask);

        addToPrioritizedTasks(subtask);
        updateEpicStatus(epicId);
        updateEpicTime(epicId);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask removed = subtasks.remove(id);

        if (removed == null) {
            return;
        }

        removeFromPrioritizedTasks(removed);
        historyManager.remove(id);

        int epicId = removed.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic != null) {
            epic.deleteSubtaskId(id);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
        }
    }

    @Override
    public void clearSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            removeFromPrioritizedTasks(subtask);
            historyManager.remove(subtask.getId());
        }

        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearList();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
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

    protected void restoreTask(Task task) {
        tasks.put(task.getId(), task);
        updateNextId(task.getId());
    }

    protected void restoreEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateNextId(epic.getId());
    }

    protected void restoreSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
        }

        updateNextId(subtask.getId());
    }

    private void updateNextId(int id) {
        if (id >= nextId) {
            nextId = id + 1;
        }
    }
}
