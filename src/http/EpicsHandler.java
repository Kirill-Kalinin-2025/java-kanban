package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        List<Epic> epics = taskManager.getEpics();
                        sendSuccess(exchange, gson.toJson(epics));
                    } else if (pathParts.length == 3) {
                        try {
                            int id = Integer.parseInt(pathParts[2]);
                            Epic epic = taskManager.getEpicById(id);
                            sendSuccess(exchange, gson.toJson(epic));
                        } catch (NumberFormatException | NullPointerException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "POST":
                    if (pathParts.length == 2) {
                        try {
                            String body = readRequestBody(exchange);
                            Epic epic = gson.fromJson(body, Epic.class);
                            if (epic.getId() == 0) {
                                int id = taskManager.addEpic(epic);
                                sendCreated(exchange, "{\"id\":" + id + "}");
                            } else {
                                taskManager.updateEpic(epic);
                                sendCreated(exchange, "{\"id\":" + epic.getId() + "}");
                            }
                        } catch (JsonSyntaxException e) {
                            sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        try {
                            int id = Integer.parseInt(pathParts[2]);
                            taskManager.delEpicById(id);
                            sendSuccess(exchange, "");
                        } catch (NumberFormatException | NullPointerException e) {
                            sendNotFound(exchange);
                        }
                    } else if (pathParts.length == 2) {
                        taskManager.delAllEpics();
                        sendSuccess(exchange, "");
                    } else {
                        sendNotFound(exchange);
                    }
                    break;
                default:
                    sendNotFound(exchange);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }
}