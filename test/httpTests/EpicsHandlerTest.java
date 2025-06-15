package httpTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest extends HttpHandlerTestBase {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Test
    void createEpic_shouldReturn201AndId() throws Exception {
        String epicJson = """
                {
                    "id": 0,
                    "type": "EPIC",
                    "title": "New Epic",
                    "description": "Description",
                    "status": "NEW",
                    "startTime": null,
                    "duration": null,
                    "subtaskId": []
                }
                """;

        // Добавьте логирование перед отправкой
        System.out.println("Sending JSON: " + epicJson);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Детальное логирование
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("id"));
    }
}