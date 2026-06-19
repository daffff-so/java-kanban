package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.TaskManager;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET") && path.equals("/epics")) {
                handleGetEpics(exchange);
            } else if (method.equals("GET") && path.matches("/epics/\\d+")) {
                handleGetEpicById(exchange);
            } else if (method.equals("GET") && path.matches("/epics/\\d+/subtasks")) {
                handleGetEpicSubtasks(exchange);
            } else if (method.equals("POST") && path.equals("/epics")) {
                handlePostEpic(exchange);
            } else if (method.equals("DELETE") && path.matches("/epics/\\d+")) {
                handleDeleteEpic(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception exception) {
            sendInternalError(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpics());
        sendText(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);

        Epic epic = taskManager.getEpic(id);

        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        sendText(exchange, gson.toJson(epic));
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);

        Epic epic = taskManager.getEpic(id);

        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(taskManager.getSubtasksOfEpic(id));
        sendText(exchange, response);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == 0) {
            taskManager.createEpic(epic);
        } else {
            taskManager.updateEpic(epic);
        }

        sendCreated(exchange);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int id = getIdFromPath(exchange);
        taskManager.deleteEpicById(id);
        sendText(exchange, "Эпик удалён");
    }

    private int getIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        return Integer.parseInt(parts[2]);
    }
}