package httpTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest extends HttpHandlerTestBase {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    String startTime = LocalDateTime.now()
            .plusDays(1)
            .plusHours(2)
            .truncatedTo(ChronoUnit.MINUTES)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @Test
    void createSubtask_shouldReturn201AndId() throws Exception {
        taskManager.delAllSubtasks();
        taskManager.delAllEpics();

        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.addEpic(epic);

        String subtaskJson = String.format("""
                {
                    "title": "Test Subtask",
                    "description": "Test Description",
                    "status": "NEW",
                    "type": "SUBTASK",
                    "startTime": null,
                    "duration": null,
                    "epicId": %d,
                    "id": 0
                }
                """, epicId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Response: " + response.body());
        assertTrue(response.body().contains("id"));
    }
}