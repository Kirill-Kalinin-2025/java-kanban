package httpTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tools.TypeOfTask;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static tools.Status.NEW;

class PrioritizedHandlerTest extends HttpHandlerTestBase {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Test
    void getPrioritized_shouldReturn200AndTasks() throws Exception {
        Task task = new Task(0, TypeOfTask.TASK, "t1", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(1));
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("t1"));
    }
}