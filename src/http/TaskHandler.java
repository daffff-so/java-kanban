package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET") && path.equals("/tasks")) {
                handleGetTasks(exchange);
            } else if (method.equals("GET") && path.matches("/tasks/\\d+")) {
                handleGetTaskById(exchange);
            } else if (method.equals("POST") && path.equals("/tasks")) {
                handlePostTask(exchange);
            } else if (method.equals("DELETE") && path.matches("/tasks/\\d+")) {
                handleDeleteTask(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());
        sendText(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);

        Task task = taskManager.getTask(id);

        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        sendText(exchange, gson.toJson(task));
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            Task createdTask = taskManager.createTask(task);

            if (createdTask == null) {
                sendHasInteractions(exchange);
                return;
            }
        } else {
            taskManager.updateTask(task);
        }

        sendCreated(exchange);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);
        taskManager.deleteTaskById(id);
        sendText(exchange, "Задача удалена");
    }

    private int getIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }
}