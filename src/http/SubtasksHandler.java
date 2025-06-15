package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Subtask;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class SubtasksHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(SubtasksHandler.class.getName());

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logger.info("=== НАЧАЛО ОБРАБОТКИ ЗАПРОСА ===");
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            logger.info("Метод: " + method);
            logger.info("Путь: " + path);
            logger.info("Текущие подзадачи: " + taskManager.getSubtasks());

            switch (method) {
                case "GET":
                    handleGet(exchange, pathParts);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, pathParts);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            logger.severe("Ошибка обработки запроса: " + e.getMessage());
            e.printStackTrace();
            sendInternalError(exchange);
        } finally {
            logger.info("=== ЗАВЕРШЕНИЕ ОБРАБОТКИ ЗАПРОСА ===");
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            List<Subtask> subtasks = taskManager.getSubtasks();
            logger.info("Получение списка подзадач: " + subtasks.size() + " подзадач");
            sendSuccess(exchange, gson.toJson(subtasks));
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                logger.info("Получение подзадачи по ID: " + id);
                Subtask subtask = taskManager.getSubtaskById(id);
                sendSuccess(exchange, gson.toJson(subtask));
            } catch (NumberFormatException | NullPointerException e) {
                logger.warning("Неверный ID подзадачи: " + pathParts[2]);
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        logger.info("Тело запроса: " + body);

        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            // Убедимся, что ID = 0 для новой подзадачи
            if (subtask.getId() == 0) {
                subtask.setId(0);
            }
            logger.info("Десериализованная подзадача: " + subtask);

            if (subtask.getId() == 0) {
                logger.info("Создание новой подзадачи");
                int id = taskManager.addSubtask(subtask);
                logger.info("Создана подзадача с ID: " + id);
                sendCreated(exchange, "{\"id\":" + id + "}");
            } else {
                logger.info("Обновление существующей подзадачи ID: " + subtask.getId());
                taskManager.updateSubtask(subtask);
                sendCreated(exchange, "{\"id\":" + subtask.getId() + "}");
            }
        } catch (Exception e) {
            logger.severe("Ошибка: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                logger.info("Удаление подзадачи ID: " + id);
                taskManager.delSubtaskById(id);
                sendSuccess(exchange, "");
            } catch (NumberFormatException | NullPointerException e) {
                sendNotFound(exchange);
            }
        } else if (pathParts.length == 2) {
            logger.info("Удаление всех подзадач");
            taskManager.delAllSubtasks();
            sendSuccess(exchange, "");
        } else {
            sendNotFound(exchange);
        }
    }
}