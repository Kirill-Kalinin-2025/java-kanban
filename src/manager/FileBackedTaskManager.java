package manager;

import exception.InputException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tools.Status;
import tools.TypeOfTask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        if (file.length() == 0) {
            System.out.println("Файл пуст");
            return fileBackedTaskManager;
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            int maxId = 0;
            // Читаем задачи
            while ((line = fileReader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // пропускаем заголовок
                }
                Task task = fromString(line);
                if (task != null) {
                    fileBackedTaskManager.addTaskToStorage(task);
                    maxId = Math.max(maxId, task.getId());
                }
            }
            fileBackedTaskManager.setId(maxId);

            // Читаем историю задач
            if ((line = fileReader.readLine()) != null && !line.isEmpty()) {
                String[] ids = line.split(",");
                // Заменяем цикл for на stream()
                java.util.Arrays.stream(ids)
                        .map(String::trim)
                        .mapToInt(idStr -> {
                            try {
                                return Integer.parseInt(idStr);
                            } catch (NumberFormatException e) {
                                System.out.println("Некорректный идентификатор задачи в истории: " + idStr);
                                return -1;
                            }
                        })
                        .filter(id -> id != -1)
                        .forEach(taskId -> {
                            Task task = fileBackedTaskManager.getTaskFromStorage(taskId);
                            if (task != null) {
                                fileBackedTaskManager.historyManager.add(task);
                            }
                        });
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при чтении файла.", e);
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(", ");
        if (parts.length < 7) return null;

        int id = Integer.parseInt(parts[0]);
        TypeOfTask type = TypeOfTask.valueOf(parts[1]);
        String title = parts[2];
        String status = parts[3];
        String description = parts[4];

        LocalDateTime startTime = "null".equals(parts[5]) ? null : LocalDateTime.parse(parts[5]);
        Duration duration = "null".equals(parts[6]) ? null : Duration.parse(parts[6]);

        switch (type) {
            case TASK:
                return new Task(id, type, title, description, Status.valueOf(status), startTime, duration);
            case EPIC:
                LocalDateTime endTime = null;
                if (parts.length > 7) {
                    endTime = "null".equals(parts[7]) ? null : LocalDateTime.parse(parts[7]);
                }
                Epic epic = new Epic(id, type, title, description, Status.valueOf(status), startTime, duration);
                epic.setEndTime(endTime);
                return epic;
            case SUBTASK:
                if (parts.length < 8) return null;
                int epicId = Integer.parseInt(parts[7]);
                return new Subtask(id, type, title, description, Status.valueOf(status), startTime, duration, epicId);
            default:
                return null;
        }
    }

    @Override
    protected void save() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtasks());

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id, type, name, status, description, startTime, duration, endTime, epic\n");
            allTasks.stream()
                    .map(this::toString)
                    .forEach(line -> {
                        try {
                            writer.write(line);
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка записи задачи в файл.", e);
                        }
                    });
            writer.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при записи файла.", e);
        }
    }

    @Override
    protected String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(", ")
                .append(task.getType()).append(", ")
                .append(task.getTitle()).append(", ")
                .append(task.getStatus()).append(", ")
                .append(task.getDescription()).append(", ")
                .append(task.getStartTime() != null ? task.getStartTime() : "null").append(", ")
                .append(task.getDuration() != null ? task.getDuration() : "null");

        if (task instanceof Epic) {
            LocalDateTime epicEndTime = ((Epic) task).getEndTime();
            sb.append(", ").append(epicEndTime != null ? epicEndTime : "null");
            sb.append(", ");
        } else if (task instanceof Subtask) {
            sb.append(", null");
            sb.append(", ").append(((Subtask) task).getEpicId());
        } else {
            sb.append(", null, null");
        }
        sb.append("\n");
        return sb.toString();
    }

    protected static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(task -> String.valueOf(task.getId()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Override
    public Integer addTask(Task task) throws InputException {
        Integer id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public Integer addEpic(Epic epic) {
        Integer id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addSubtask(Subtask subtask) throws InputException {
        Integer id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) throws InputException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws InputException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void delAllTasks() {
        super.delAllTasks();
        save();
    }

    @Override
    public void delAllEpics() {
        super.delAllEpics();
        save();
    }

    @Override
    public void delAllSubtasks() {
        super.delAllSubtasks();
        save();
    }

    @Override
    public void delTaskById(Integer id) {
        super.delTaskById(id);
        save();
    }

    @Override
    public void delEpicById(Integer id) {
        super.delEpicById(id);
        save();
    }

    @Override
    public void delSubtaskById(Integer id) {
        super.delSubtaskById(id);
        save();
    }
}