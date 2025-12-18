package model;

import java.util.Objects;

public class Task {
     private int id;
     private String name;
     private String description;
     private Status status;

     public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
     }

     public String getName() {
         return name;
     }

    public void setId(int id) {
        if (this.id == 0 && id > 0) {
            this.id = id;
        }
    }

     public int getId() {
         return id;
     }

     public String getDescription() {
         return description;
     }

     public Status getStatus() {
         return status;
     }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "status=" + status +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
