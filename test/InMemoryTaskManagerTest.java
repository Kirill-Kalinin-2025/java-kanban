import exception.InputException;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager() {
            @Override
            protected void save() {

            }

            @Override
            protected String toString(Task task) {
                return "";
            }
        };
    }

    @BeforeEach
    void setUp() throws InputException {
        super.setUp();
    }
}
