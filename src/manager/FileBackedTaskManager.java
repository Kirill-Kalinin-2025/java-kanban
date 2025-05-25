package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tools.Status;
import tools.TypeOfTask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    static File file;

    public FileBackedTaskManager(File file) {
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
            while ((line = fileReader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                if (!line.equals("id, typeOfTask, name, status, description")) {
                    Task task = fromString(line);
                    if (task != null) {
                        fileBackedTaskManager.addTask(task);
                    }
                }
            }
            // Чтение истории
            if ((line = fileReader.readLine()) != null) {
                String[] ids = line.split(", ");
                for (String id : ids) {
                    int taskId = Integer.parseInt(id);
                    Task task = fileBackedTaskManager.getTaskById(taskId);
                    if (task != null) {
                        fileBackedTaskManager.historyManager.add(task);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
            e.printStackTrace();
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(", ");
        if (parts.length < 5) return null;

        int id = Integer.parseInt(parts[0]);
        TypeOfTask type = TypeOfTask.valueOf(parts[1]);
        String title = parts[2];
        String status = parts[3];
        String description = parts[4];

        switch (type) {
            case TASK:
                return new Task(id, type, title, description, Status.valueOf(status));
            case EPIC:
                return new Epic(id, type, title, description, Status.valueOf(status));
            case SUBTASK:
                if (parts.length < 6) return null;
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, type, title, description, Status.valueOf(status), epicId);
            default:
                return null;
        }
    }

    private static List<Integer> historyFromString(String value) {
        String[] ids = value.split(", ");
        List<Integer> result = new ArrayList<>();
        for (String id :  ids) {
            result.add(Integer.parseInt(id));
        }
        return result;
    }

    protected static String historyToString(HistoryManager manager) {
        StringBuilder historyString = new StringBuilder();
        for (Task task : manager.getHistory()) {
            historyString.append(task.getId()).append(", ");
        }
        if (historyString.length() > 2) {
            historyString.setLength(historyString.length() - 2);
        }
        return historyString.toString();
    }

    public void save() {
        List<Task> allTask = new ArrayList<>();
        allTask.addAll(getTasks());
        allTask.addAll(getEpics());
        allTask.addAll(getSubtasks());
        try (FileWriter fileWriter = new FileWriter(file,  StandardCharsets.UTF_8)) {
            fileWriter.write("id, typeOfTask, name, status, description\n");
            for (Task task :  allTask) {
                fileWriter.write(toString(task));
            }
            fileWriter.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
            e.printStackTrace();
        }
    }

    protected String toString(Task task) {
        StringBuilder taskString = new StringBuilder();
        taskString.append(String.format("%d, %s, %s, %s, %s",
                task.getId(),  task.getType(),  task.getTitle(),  task.getStatus(),  task.getDescription()));
        if (task.getType() == TypeOfTask.SUBTASK) {
            taskString.append(", ").append(((Subtask) task).getEpicId());
        }
        taskString.append("\n");
        return taskString.toString();
    }

    @Override
    public Integer addTask(Task task) {
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
    public Integer addSubtask(Subtask subtask) {
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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
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
