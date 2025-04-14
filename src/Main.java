import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import Manager.Managers;
import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        while (true) {
            System.out.println();
            printMenu();
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1: {
                    addTask();
                    break;
                }
                case 2: {
                    addEpic();
                    break;
                }
                case 3: {
                    addSubtask();
                    break;
                }
                case 4: {
                    getTasks();
                    break;
                }
                case 5: {
                    getEpics();
                    break;
                }
                case 6: {
                    getSubtasks();
                    break;
                }
                case 7: {
                    deleteAllTasks();
                    break;
                }
                case 8: {
                    deleteAllEpics();
                    break;
                }
                case 9: {
                    deleteAllSubtasks();
                    break;
                }
                case 10: {
                    getTaskById();
                    break;
                }
                case 11: {
                    getEpicById();
                    break;
                }
                case 12: {
                    getSubtaskById();
                    break;
                }
                case 13: {
                    updateTask();
                    break;
                }
                case 14: {
                    updateEpic();
                    break;
                }
                case 15: {
                    updateSubtask();
                    break;
                }
                case 16: {
                    deleteTaskById();
                    break;
                }
                case 17: {
                    deleteEpicById();
                    break;
                }
                case 18: {
                    deleteSubtaskById();
                    break;
                }
                case 19: {
                    getSubtasksByEpicId();
                    break;
                }
                case 20:
                    exit();
                    return;

                default:
                    System.out.println("Неизвестная команда!");
            }
        }
    }

        public static void printMenu () {
            System.out.println("Выберите опцию: ");
            System.out.println("1. Добавить задачу");
            System.out.println("2. Добавить эпик");
            System.out.println("3. Добавить подзадачу");
            System.out.println("4. Получить список всех задач");
            System.out.println("5. Получить список всех эпиков");
            System.out.println("6. Получить список всех подзадач");
            System.out.println("7. Удалить все задачи");
            System.out.println("8. Удалить все эпики");
            System.out.println("9. Удалить все подзадачи");
            System.out.println("10. Получение задачи по id");
            System.out.println("11. Получение эпика по id");
            System.out.println("12. Получение подзадачи по id");
            System.out.println("13. Изменение задачи");
            System.out.println("14. Изменение эпика");
            System.out.println("15. Изменение подзадачи");
            System.out.println("16. Удаление задачи по id");
            System.out.println("17. Удаление эпика по id");
            System.out.println("18. Удаление подзадачи по id");
            System.out.println("19. Получение списка всех подзадач для эпика по id");
            System.out.println("20. Выход");
        }

    public static void addTask() {
        System.out.println("Введите заголовок задачи: ");
        String title = scanner.nextLine();
        System.out.println("Введите описание задачи: ");
        String description = scanner.nextLine();
        Task task = new Task(title, description);
        taskManager.addTask(task);
        System.out.println("Задача добавлена");
    }

    public static void addEpic() {
        System.out.println("Введите заголовок эпика: ");
        String title = scanner.nextLine();
        System.out.println("Введите описание эпика: ");
        String description = scanner.nextLine();
        ArrayList<Integer> subtaskId = new ArrayList<>();
        Epic epic = new Epic(title, description);
        taskManager.addEpic(epic);
        System.out.println("Эпик добавлен");
    }

    public static void addSubtask() {
        System.out.println("Введите заголовок подзадачи: ");
        String title = scanner.nextLine();
        System.out.println("Введите описание подзадачи: ");
        String description = scanner.nextLine();
        System.out.println("Введите id эпика, к которому относится подзадача: ");
        int epicId = scanner.nextInt();
        scanner.nextLine();
        Subtask subtask = new Subtask(title, description, epicId);
        taskManager.addSubtask(subtask);
        System.out.println("Подзадача добавлена");
    }

    public static void getTasks() {
        List<Task> tasks = taskManager.getTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    public static void getEpics() {
        List<Epic> epics = taskManager.getEpics();
        for (Epic epic : epics) {
            System.out.println(epic);
        }
    }

    public static void getSubtasks() {
        List<Subtask> subtasks = taskManager.getSubtasks();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }
    }

    public static void deleteAllTasks() {
        taskManager.delAllTasks();
        System.out.println("Все задачи удалены");
    }

    public static void deleteAllEpics() {
        taskManager.delAllEpics();
        System.out.println("Все эпики удалены");
    }

    public static void deleteAllSubtasks() {
        taskManager.delAllSubtasks();
        System.out.println("Все подзадачи удалены");
    }

    public static void getTaskById() {
        System.out.println("Введите id задачи, которую хотите получить: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Task task = taskManager.getTaskById(id);
        System.out.println(task);
    }

    public static void getEpicById() {
        System.out.println("Введите id эпика, который хотите получить: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Epic epic = taskManager.getEpicById(id);
        System.out.println(epic);
    }

    public static void getSubtaskById() {
        System.out.println("Введите id подзадачи, которую хотите получить: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Subtask subtask = taskManager.getSubtaskById(id);
        System.out.println(subtask);
    }

    public static void updateTask() {
        System.out.println("Введите id задачи, которую хотите обновить:");
        int id = scanner.nextInt();
        scanner.nextLine();
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            System.out.println("Введите новый заголовок задачи:");
            String title = scanner.nextLine();
            System.out.println("Введите новое описание задачи:");
            String description = scanner.nextLine();
            task.setTitle(title);
            task.setDescription(description);
            taskManager.updateTask(task);
            System.out.println("Задача обновлена");
        } else {
            System.out.println("Задача с указанным id не найдена");
        }
    }

    public static void updateEpic() {
        System.out.println("Введите id эпика, который хотите обновить:");
        int id = scanner.nextInt();
        scanner.nextLine();
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            System.out.println("Введите новый заголовок эпика:");
            String title = scanner.nextLine();
            System.out.println("Введите новое описание эпика:");
            String description = scanner.nextLine();
            epic.setTitle(title);
            epic.setDescription(description);
            taskManager.updateEpic(epic);
            System.out.println("Эпик обновлен");
        } else {
            System.out.println("Эпик с указанным id не найден");
        }
    }

    public static void updateSubtask() {
        System.out.println("Введите id подзадачи, которую хотите обновить:");
        int id = scanner.nextInt();
        scanner.nextLine();
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask != null) {
            System.out.println("Введите новый заголовок подзадачи:");
            String title = scanner.nextLine();
            System.out.println("Введите новое описание подзадачи:");
            String description = scanner.nextLine();
            System.out.println("Введите id эпика, к которому относится подзадача:");
            int epicId = scanner.nextInt();
            scanner.nextLine();
            subtask.setTitle(title);
            subtask.setDescription(description);
            taskManager.updateSubtask(subtask);
            System.out.println("Подзадача обновлена");
        } else {
            System.out.println("Подзадача с указанным id не найдена");
        }
    }

    public static void deleteTaskById() {
        System.out.println("Введите id задачи, которую хотите удалить: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        taskManager.delTaskById(id);
        System.out.println("Задача удалена");
    }

    public static void deleteEpicById() {
        System.out.println("Введите id эпика, который хотите удалить: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        taskManager.delEpicById(id);
        System.out.println("Эпик удален");
    }

    public static void deleteSubtaskById() {
        System.out.println("Введите id подзадачи, которую хотите удалить: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        taskManager.delSubtaskById(id);
        System.out.println("Подзадача удалена");
    }

    public static void getSubtasksByEpicId() {
        System.out.println("Введите id эпика, к которому относится подзадача: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic);
            for (Subtask subtask : subtasks) {
                System.out.println(subtask);
            }
        } else {
            System.out.println("Эпик с указанным id не найден");
        }
    }

        public static void exit () {
            System.out.println("Выход");
            scanner.close();
        }
    }