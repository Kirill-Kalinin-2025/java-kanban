package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

public class PrioritizedHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(PrioritizedHandler.class.getName());

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            logger.info("Обработка запроса на выполнение приоритетных задач");
            if (!"GET".equals(exchange.getRequestMethod())) {
                logger.warning("Недопустимый метод запроса: " + exchange.getRequestMethod());
                sendNotFound(exchange);
                return;
            }

            Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            logger.info("Нашел " + prioritizedTasks.size() + " приоритетные задачи");

            if (prioritizedTasks.isEmpty()) {
                logger.info("Приоритетных задач не найдено");
                sendSuccess(exchange, "[]");
                return;
            }

            String responseBody = gson.toJson(prioritizedTasks);
            logger.fine("Ответ на запрос: " + responseBody);
            sendSuccess(exchange, responseBody);

        } catch (Exception e) {
            logger.severe("Ошибка в PrioritizedHandler: " + e.getMessage());
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}