package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tools.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private void updateEpicInfo(Epic epic) {
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean aStatusNew = false;
        boolean aStatusInProgress = false;
        boolean aStatusDone = false;
        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            switch (subtask.getStatus()) {
                case NEW:
                    aStatusNew = true;
                    break;
                case IN_PROGRESS:
                    aStatusInProgress = true;
                    break;
                case DONE:
                    aStatusDone = true;
                    break;
            }
        }
        if (aStatusNew && !aStatusInProgress && !aStatusDone) {
            epic.setStatus(Status.NEW);
        } else if (aStatusDone && !aStatusNew && !aStatusInProgress) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Integer addTask(Task task) {
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
    public Integer addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            final int id = counterId();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.getSubtaskId().add(id);
            updateEpicInfo(epic);
            return id;
        } else {
            System.out.println("Не указан эпик");
            return -1;
        }
    }

    @Override
    public void updateTask(Task task) {
        final int id = task.getId();
        if (tasks.containsKey(id)) {
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
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
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