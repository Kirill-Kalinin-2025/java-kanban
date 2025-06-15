package httpTests;

import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

import java.io.IOException;

public class HttpHandlerTestBase {
    protected TaskManager taskManager;
    protected HttpTaskServer server;
    protected final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager() {
            @Override
            protected void save() {

            }

            @Override
            protected String toString(Task task) {
                return "";
            }
        };
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }
}