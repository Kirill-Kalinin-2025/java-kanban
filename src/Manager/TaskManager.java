package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import Tools.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    static final private HashMap<Integer, Task> tasks = new HashMap<>();
    static final private HashMap<Integer, Epic> epics = new HashMap<>();
    static final private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;

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

    public Integer addTask(Task task) {
        final int id = counterId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public Integer addEpic(Epic epic) {
        final int id = counterId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }


    public Integer addSubtask(Subtask subtask) {
        final int id = counterId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskId().add(id);
        updateEpicInfo(epic);
        return id;
    }

    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        final Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        epic.setSubtaskId(savedEpic.getSubtaskId());
        epics.put(id, epic);
        updateEpicInfo(epic);
    }

    public void updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicInfo(epic);
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskId()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
        }
        return subtasksOfEpic;
    }

    public void delAllTasks() {
        tasks.clear();
    }

    public void delAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void delAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskId().clear();
            updateEpicInfo(epic);
        }
    }

    public void delTaskById(Integer id) {
        tasks.remove(id);
    }

    public void delEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void delSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskId().remove(id);
            updateEpicInfo(epic);
            subtasks.remove(id);
        }
    }
}