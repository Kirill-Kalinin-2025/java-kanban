package httpTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tools.Status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest extends HttpHandlerTestBase {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    String startTime = LocalDateTime.now()
            .plusDays(1)
            .truncatedTo(ChronoUnit.MINUTES)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @Test
    void getTasks_shouldReturn200AndTasksList() throws Exception {
        // Добавляем тестовые задачи
        Task task = new Task("Test", "Description", Status.NEW);
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertFalse(response.body().isEmpty());
    }

    @Test
    void getTaskById_shouldReturn200ForExistingTask() throws Exception {
        Task task = new Task("Test", "Description", Status.NEW);
        int id = taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }

    @Test
    void getTaskById_shouldReturn404ForNonExistingTask() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void createTask_shouldReturn201AndId() throws Exception {
        taskManager.delAllTasks();

        String taskJson = """
                {
                    "title": "Test Task",
                    "description": "Test Description",
                    "status": "NEW",
                    "type": "TASK",
                    "startTime": null,
                    "duration": null,
                    "id": 0
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Response: " + response.body());
        assertTrue(response.body().contains("id"));
    }
}