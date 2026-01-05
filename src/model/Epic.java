package model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public void deleteSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public List<Integer> getIdList() {
        return new ArrayList<>(subtaskIds);
    }

    public void clearList() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}

