package manager;

import tasks.Task;

import java.io.File;

public class Managers {

    static File file = new File("file.csv");

    public static TaskManager getDefault() {
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

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getTaskManagerFile(File file) {
        return new FileBackedTaskManager(Managers.file);
    }
}
