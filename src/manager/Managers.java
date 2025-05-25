package manager;

import java.io.File;

public class Managers {

    static File file = new File("file.csv");

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getTaskManagerFile(File file) {
        return new FileBackedTaskManager(Managers.file);
    }
}
