package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class HistoryHandler extends BaseHttpHandler {
    static final Logger logger = Logger.getLogger(HistoryHandler.class.getName());

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            logger.info("Обработка запроса истории");
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Task> history = taskManager.getHistory();
                logger.info("Размер истории: " + history.size());
                sendSuccess(exchange, gson.toJson(history));
            } else {
                logger.warning("Недопустимый метод запроса: " + exchange.getRequestMethod());
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            logger.severe("Ошибка в HistoryHandler: " + e.getMessage());
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}