package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET") && path.equals("/subtasks")) {
                handleGetSubtasks(exchange);
            } else if (method.equals("GET") && path.matches("/subtasks/\\d+")) {
                handleGetSubtaskById(exchange);
            } else if (method.equals("POST") && path.equals("/subtasks")) {
                handlePostSubtask(exchange);
            } else if (method.equals("DELETE") && path.matches("/subtasks/\\d+")) {
                handleDeleteSubtask(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception exception) {
            sendInternalError(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtasks());
        sendText(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);

        Subtask subtask = taskManager.getSubtask(id);

        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }

        sendText(exchange, gson.toJson(subtask));
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getId() == 0) {
            Subtask createdSubtask = taskManager.createSubtask(subtask);

            if (createdSubtask == null) {
                sendHasInteractions(exchange);
                return;
            }
        } else {
            taskManager.updateSubtask(subtask);
        }

        sendCreated(exchange);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);
        taskManager.deleteSubtaskById(id);
        sendText(exchange, "Подзадача удалена");
    }

    private int getIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }
}