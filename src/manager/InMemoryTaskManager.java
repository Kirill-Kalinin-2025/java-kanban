package manager;

import exception.InputException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tools.Status;
import tools.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class InMemoryTaskManager implements TaskManager {
    final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();
    final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;
    final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );

    public HistoryManager historyManager = Managers.getDefaultHistory();

    private int counterId() {
        id++;
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    private void updateEpicInfo(Epic epic) {
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean hasNewSubtasks = epic.getSubtaskId().stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask != null && subtask.getStatus() == Status.NEW);

        boolean hasDoneSubtasks = epic.getSubtaskId().stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask != null && subtask.getStatus() == Status.DONE);

        boolean hasInProgressSubtasks = epic.getSubtaskId().stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask != null && subtask.getStatus() == Status.IN_PROGRESS);

        if (hasNewSubtasks && hasDoneSubtasks) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (hasInProgressSubtasks) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (hasNewSubtasks && !hasDoneSubtasks) {
            epic.setStatus(Status.NEW);
        } else if (hasDoneSubtasks && !hasNewSubtasks) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        updateEpicDateTime(epic);
    }

    protected void updateEpicDateTime(Epic epic) {
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }
        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic);
        LocalDateTime startTime = subtasksOfEpic.stream()
                .filter(s -> s != null && s.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime endTime = subtasksOfEpic.stream()
                .filter(s -> s != null && s.getEndTime() != null)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        Duration duration = subtasksOfEpic.stream()
                .filter(s -> s != null && s.getDuration() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    protected void addTaskToStorage(Task task) {
        task.setType(task.getType() == null ? TypeOfTask.TASK : task.getType());
        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
                Epic epic = epics.get(((Subtask) task).getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(task.getId());
                    updateEpicInfo(epic);
                }
                break;
        }
    }

    protected Task getTaskFromStorage(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            task = epics.get(id);
        }
        if (task == null) {
            task = subtasks.get(id);
        }
        return task;
    }

    protected void validateTaskByDateTime(Task newTask) throws InputException {
        // Если время не указано - пропускаем проверку
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            System.out.println("[DEBUG] Пропуск валидации: задача без времени");
            return;
        }

        System.out.println("[DEBUG] Начало валидации для: " + newTask);

        // Получаем ТОЛЬКО задачи с указанным временем
        List<Task> tasksToCheck = getPrioritizedTasks().stream()
                .filter(t -> t.getStartTime() != null && t.getDuration() != null)
                .filter(t -> !t.equals(newTask)) // Исключаем саму задачу
                .toList();

        if (tasksToCheck.isEmpty()) {
            System.out.println("[DEBUG] Нет задач для проверки пересечений");
            return;
        }

        for (Task existing : tasksToCheck) {
            System.out.println("[DEBUG] Проверка против: " + existing);

            boolean overlaps = newTask.getStartTime().isBefore(existing.getEndTime()) &&
                    newTask.getEndTime().isAfter(existing.getStartTime());

            if (overlaps) {
                String errorMsg = String.format(
                        "Временной конфликт: %s (%s - %s) пересекается с %s (%s - %s)",
                        newTask.getTitle(), newTask.getStartTime(), newTask.getEndTime(),
                        existing.getTitle(), existing.getStartTime(), existing.getEndTime());
                System.out.println("[ERROR] " + errorMsg);
                throw new InputException(errorMsg);
            }
        }
        System.out.println("[DEBUG] Валидация успешно завершена");
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> prioritizedTasks = new TreeSet<>(
                Comparator.comparing(Task::getStartTime,
                        Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId)
        );
        prioritizedTasks.addAll(getTasks());
        prioritizedTasks.addAll(getSubtasks());
        return prioritizedTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected abstract void save();

    protected abstract String toString(Task task);

    @Override
    public Integer addTask(Task task) throws InputException {
        validateTaskByDateTime(task);
        final int newId = counterId();
        task.setId(newId);
        task.setType(TypeOfTask.TASK);
        tasks.put(newId, task);
        prioritizedTasks.add(task);
        return newId;
    }

    @Override
    public Integer addEpic(Epic epic) {
        final int newId = counterId();
        epic.setId(newId);
        epic.setType(TypeOfTask.EPIC);
        epics.put(newId, epic);
        return newId;
    }

    @Override
    public Integer addSubtask(Subtask subtask) throws InputException {
        validateTaskByDateTime(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            final int newId = counterId();
            subtask.setId(newId);
            subtask.setType(TypeOfTask.SUBTASK);
            subtasks.put(newId, subtask);
            epic.addSubtaskId(newId);
            updateEpicInfo(epic);
            return newId;
        } else {
            System.out.println("Не указан эпик");
            return -1;
        }
    }

    @Override
    public void updateTask(Task task) throws InputException {
        final int id = task.getId();
        if (tasks.containsKey(id)) {
            validateTaskByDateTime(task);
            tasks.put(id, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic savedEpic = epics.get(id);
            savedEpic.setTitle(epic.getTitle());
            savedEpic.setDescription(epic.getDescription());
            // status and subtasks handled internally
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws InputException {
        final int id = subtask.getId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask != null) {
            if (subtask.getEpicId() == savedSubtask.getEpicId()) {
                validateTaskByDateTime(subtask);
                subtasks.put(id, subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    updateEpicInfo(epic);
                }
            }
        }
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NullPointerException("Task with id " + id + " not found");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NullPointerException("Epic with id " + id + " not found");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NullPointerException("Subtask with id " + id + " not found");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getSubtaskId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void delAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TypeOfTask.TASK);
    }

    @Override
    public void delAllEpics() {
        delAllSubtasks();
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void delAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        epics.values().forEach(epic -> {
            epic.clearSubtaskId();
            updateEpicInfo(epic);
        });
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TypeOfTask.SUBTASK);
    }

    @Override
    public void delTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void delEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskId().forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void delSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicInfo(epic);
            }
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }
}