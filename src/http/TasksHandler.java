package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class TasksHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(TasksHandler.class.getName());

    public TasksHandler(TaskManager taskManager, Gson gson) {
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
            logger.info("Текущие задачи: " + taskManager.getPrioritizedTasks());

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
            List<Task> tasks = taskManager.getTasks();
            logger.info("Получение списка задач: " + tasks.size() + " задач");
            sendSuccess(exchange, gson.toJson(tasks));
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                logger.info("Получение задачи по ID: " + id);
                Task task = taskManager.getTaskById(id);
                sendSuccess(exchange, gson.toJson(task));
            } catch (NumberFormatException | NullPointerException e) {
                logger.warning("Неверный ID задачи: " + pathParts[2]);
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
            Task task = gson.fromJson(body, Task.class);
            // Убедимся, что ID = 0 для новой задачи
            if (task.getId() == 0) {
                task.setId(0);
            }
            logger.info("Десериализованная задача: " + task);

            if (task.getId() == 0) {
                logger.info("Создание новой задачи");
                int id = taskManager.addTask(task);
                logger.info("Создана задача с ID: " + id);
                sendCreated(exchange, "{\"id\":" + id + "}");
            } else {
                logger.info("Обновление существующей задачи ID: " + task.getId());
                taskManager.updateTask(task);
                sendCreated(exchange, "{\"id\":" + task.getId() + "}");
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
                logger.info("Удаление задачи ID: " + id);
                taskManager.delTaskById(id);
                sendSuccess(exchange, "");
            } catch (NumberFormatException | NullPointerException e) {
                sendNotFound(exchange);
            }
        } else if (pathParts.length == 2) {
            logger.info("Удаление всех задач");
            taskManager.delAllTasks();
            sendSuccess(exchange, "");
        } else {
            sendNotFound(exchange);
        }
    }
}