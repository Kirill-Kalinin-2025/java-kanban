package manager;

import exception.InputException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tools.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
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
        LocalDateTime startTime;
        LocalDateTime endTime;
        Duration duration;
        if (epic.getSubtaskId().isEmpty()) {
            return;
        }
        startTime = getSubtasksOfEpic(epic).get(0).getStartTime();
        endTime = getSubtasksOfEpic(epic).get(0).getEndTime();
        duration = Duration.ofMinutes(0);
        for (Subtask subtask : getSubtasksOfEpic(epic)) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
            duration = duration.plus(subtask.getDuration());
        }
        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    protected void addTaskToStorage(Task task) {
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

    @Override
    public Integer addTask(Task task) throws InputException {
        validateTaskByDateTime(task);
        final int id = counterId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addEpic(Epic epic) {
        final int id = counterId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addSubtask(Subtask subtask) throws InputException {
        validateTaskByDateTime(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            final int id = counterId();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.addSubtaskId(id);
            updateEpicInfo(epic);  // Убедитесь, что эта строка присутствует
            return id;
        } else {
            System.out.println("Не указан эпик");
            return -1;
        }
    }

    @Override
    public void updateTask(Task task) throws InputException {
        final int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
        validateTaskByDateTime(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic savedEpic = epics.get(id);
            savedEpic.setTitle(epic.getTitle());
            savedEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws InputException {
        final int id = subtask.getId();
        final Subtask savedSubtask = subtasks.get(id);
        if (subtasks.containsKey(id)) {
            if (subtask.getEpicId() == savedSubtask.getEpicId()) {
                subtasks.put(id, subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    updateEpicInfo(epic);
                }
            }
        }
        validateTaskByDateTime(subtask);
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                subtasksOfEpic.add(subtasks.get(subtaskId));
            }
        }
        return subtasksOfEpic;
    }

    @Override
    public void delAllTasks() {
        for (Integer id : epics.keySet()) {
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
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void delEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                historyManager.remove(subtaskId);
                subtasks.remove(subtaskId);
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void delSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            updateEpicInfo(epic);
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }
}