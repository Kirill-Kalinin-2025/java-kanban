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

public abstract class InMemoryTaskManager implements TaskManager {
    final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();
    final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;

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
        boolean hasNewSubtasks = false;
        boolean hasDoneSubtasks = false;
        boolean hasInProgressSubtasks = false;

        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            switch (subtask.getStatus()) {
                case NEW:
                    hasNewSubtasks = true;
                    break;
                case IN_PROGRESS:
                    hasInProgressSubtasks = true;
                    break;
                case DONE:
                    hasDoneSubtasks = true;
                    break;
            }
        }

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
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = Duration.ZERO;
        for (Subtask subtask : getSubtasksOfEpic(epic)) {
            if (subtask == null || subtask.getStartTime() == null) {
                continue;
            }
            if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
            duration = duration.plus(subtask.getDuration());
        }
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

    private void validateTaskByDateTime(Task task) throws InputException {
        for (Task existTask : getPrioritizedTasks()) {
            if (existTask.getStartTime() == null || task.getStartTime() == null) {
                continue;
            }
            if ((task.getStartTime().isAfter(existTask.getStartTime()) &&
                    task.getStartTime().isBefore(existTask.getEndTime())) ||
                    (task.getEndTime().isAfter(existTask.getStartTime()) &&
                            task.getEndTime().isBefore(existTask.getEndTime())) ||
                    task.getStartTime().equals(existTask.getStartTime())) {
                throw new InputException("Создаваемая задача " + task.getTitle() +
                        " пересекается по времени с задачей " + existTask.getTitle());
            }
        }
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
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    subtasksOfEpic.add(subtask);
                }
            }
        }
        return subtasksOfEpic;
    }

    @Override
    public void delAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void delAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void delAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
            updateEpicInfo(epic);
        }
        subtasks.clear();
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
            for (Integer subtaskId : epic.getSubtaskId()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
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