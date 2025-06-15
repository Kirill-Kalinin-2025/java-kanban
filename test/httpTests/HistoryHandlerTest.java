package httpTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tools.Status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest extends HttpHandlerTestBase {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Test
    void getHistory_shouldReturn200AndHistory() throws Exception {
        Task task = new Task("Test", "Description", Status.NEW);
        int id = taskManager.addTask(task);
        taskManager.getTaskById(id); // Добавляем в историю

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
    }
}