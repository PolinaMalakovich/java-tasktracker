package ru.yandex.malakovich.tasktracker;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.malakovich.tasktracker.manager.TaskManager;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;
import ru.yandex.malakovich.tasktracker.util.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static ru.yandex.malakovich.tasktracker.HttpStatus.CREATED;
import static ru.yandex.malakovich.tasktracker.HttpStatus.NO_CONTENT;
import static ru.yandex.malakovich.tasktracker.HttpStatus.OK;
import static ru.yandex.malakovich.tasktracker.HttpStatus.BAD_REQUEST;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final int RESPONSE_LENGTH = 0;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        TaskManager taskManager = Managers.getDefault();
        Gson gson = new Gson();

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/task", new TasksHandler(taskManager, gson));
        server.createContext("/tasks/subtask", new SubtasksHandler(taskManager, gson));
        server.createContext("/tasks/epic", new EpicsHandler(taskManager, gson));
        server.createContext("/tasks/subtask/epic", new EpicSubtasksHandler(taskManager, gson));
        server.createContext("/tasks/history", new TaskHistoryHandler(taskManager, gson));
        server.createContext("/tasks", new PrioritizedTasksHandler(taskManager, gson));

        server.start();
    }

    private static class TasksHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;

        public TasksHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();
            String query = requestURI.getQuery();
            String response;

            switch (method) {
                case "GET":
                    if (query != null) {
                        String[] queryElements = query.split("=");
                        int id = Integer.parseInt(queryElements[1]);
                        Task task = taskManager.getTaskById(id);
                        response = gson.toJson(task);
                    } else {
                        List<Task> tasks = taskManager.getTasks();
                        response = gson.toJson(tasks);
                    }

                    sendText(httpExchange, response);

                    break;

                case "POST":
                    String body = readText(httpExchange);
                    if (body.isEmpty()) {
                        httpExchange.sendResponseHeaders(BAD_REQUEST, RESPONSE_LENGTH);
                        return;
                    }
                    Task task = gson.fromJson(body, Task.class);
                    taskManager.createTask(task);

                    httpExchange.sendResponseHeaders(CREATED, RESPONSE_LENGTH);

                    break;

                case "DELETE":
                    if (query != null) {
                        String[] queryElements = query.split("=");
                        int id = Integer.parseInt(queryElements[1]);
                        taskManager.deleteTaskById(id);
                    } else {
                        taskManager.deleteAllTasks();
                    }

                    httpExchange.sendResponseHeaders(NO_CONTENT, RESPONSE_LENGTH);

                    break;
            }
        }
    }

    private static class SubtasksHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;

        public SubtasksHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();
            String query = requestURI.getQuery();
            String response;

            switch (method) {
                case "GET":
                    if (query != null) {
                        String[] queryElements = query.split("=");
                        int id = Integer.parseInt(queryElements[1]);
                        Subtask subtask = taskManager.getSubtaskById(id);
                        response = gson.toJson(subtask);
                    } else {
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        response = gson.toJson(subtasks);
                    }

                    sendText(httpExchange, response);

                    break;

                case "POST":
                    String body = readText(httpExchange);
                    if (body.isEmpty()) {
                        httpExchange.sendResponseHeaders(BAD_REQUEST, RESPONSE_LENGTH);
                        return;
                    }
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    taskManager.createSubtask(subtask);

                    httpExchange.sendResponseHeaders(CREATED, RESPONSE_LENGTH);

                    break;

                case "DELETE":
                    if (query != null) {
                        String[] queryElements = query.split("=");
                        int id = Integer.parseInt(queryElements[1]);
                        taskManager.deleteSubtaskById(id);
                    } else {
                        taskManager.deleteAllSubtasks();
                    }

                    httpExchange.sendResponseHeaders(NO_CONTENT, RESPONSE_LENGTH);

                    break;

                default:
            }
        }
    }

    private static class EpicsHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;

        public EpicsHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();
            String query = requestURI.getQuery();
            String response;

            switch (method) {
                case "GET":
                    if (query != null) {
                        String[] queryElements = query.split("=");
                        int id = Integer.parseInt(queryElements[1]);
                        Epic epic = taskManager.getEpicById(id);
                        response = gson.toJson(epic);
                    } else {
                        List<Epic> epics = taskManager.getEpics();
                        response = gson.toJson(epics);
                    }

                    sendText(httpExchange, response);

                    break;

                case "POST":
                    String body = readText(httpExchange);
                    if (body.isEmpty()) {
                        httpExchange.sendResponseHeaders(BAD_REQUEST, RESPONSE_LENGTH);
                        return;
                    }
                    Epic epic = gson.fromJson(body, Epic.class);
                    taskManager.createTask(epic);

                    httpExchange.sendResponseHeaders(CREATED, RESPONSE_LENGTH);

                    break;

                case "DELETE":
                    if (query != null) {
                        String[] queryElements = query.split("=");
                        int id = Integer.parseInt(queryElements[1]);
                        taskManager.deleteEpicById(id);
                    } else {
                        taskManager.deleteAllEpics();
                    }

                    httpExchange.sendResponseHeaders(NO_CONTENT, RESPONSE_LENGTH);

                    break;

                default:
            }
        }
    }

    private static class EpicSubtasksHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;

        public EpicSubtasksHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            URI requestURI = httpExchange.getRequestURI();
            String query = requestURI.getQuery();
            String[] queryElements = query.split("=");
            int id = Integer.parseInt(queryElements[1]);
            Epic epic = taskManager.getEpicById(id);
            Set<Subtask> subtasks = taskManager.getEpicSubtasks(epic);
            String response = gson.toJson(subtasks);

            sendText(httpExchange, response);
        }
    }

    private static class TaskHistoryHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;

        public TaskHistoryHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<Task> history = taskManager.history();
            String response = gson.toJson(history);

            sendText(httpExchange, response);
        }
    }

    private static class PrioritizedTasksHandler implements HttpHandler {
        private final TaskManager taskManager;
        private final Gson gson;

        public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
            this.taskManager = taskManager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasksList();
            String response = gson.toJson(prioritizedTasks);

            sendText(httpExchange, response);
        }
    }

    public void stop() {
        server.stop(0);
    }

    private static void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(OK, response.length);
        httpExchange.getResponseBody().write(response);
    }

    private static String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
    }
}
