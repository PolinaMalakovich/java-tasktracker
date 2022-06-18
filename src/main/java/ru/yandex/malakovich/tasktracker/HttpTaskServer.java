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

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final int RESPONSE_LENGTH = 0;
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();
        gson = new Gson();

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/task", new TasksHandler());
        server.createContext("/tasks/subtask", new SubtasksHandler());
        server.createContext("/tasks/epic", new EpicsHandler());
        server.createContext("/tasks/subtask/epic", new EpicSubtasksHandler());
        server.createContext("/tasks/history", new TaskHistoryHandler());
        server.createContext("/tasks", new PrioritizedTasksHandler());

        server.start();
    }

    class TasksHandler implements HttpHandler {
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

                    httpExchange.sendResponseHeaders(OK, RESPONSE_LENGTH);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                    break;

                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
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

    class SubtasksHandler implements HttpHandler {
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

                    httpExchange.sendResponseHeaders(OK, RESPONSE_LENGTH);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                    break;

                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
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

    class EpicsHandler implements HttpHandler {
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

                    httpExchange.sendResponseHeaders(OK, RESPONSE_LENGTH);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                    break;

                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
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

    class EpicSubtasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            URI requestURI = httpExchange.getRequestURI();
            String query = requestURI.getQuery();
            String[] queryElements = query.split("=");
            int id = Integer.parseInt(queryElements[1]);
            Epic epic = taskManager.getEpicById(id);
            Set<Subtask> subtasks = taskManager.getEpicSubtasks(epic);
            String response = gson.toJson(subtasks);

            httpExchange.sendResponseHeaders(OK, RESPONSE_LENGTH);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class TaskHistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<Task> history = taskManager.history();
            String response = gson.toJson(history);

            httpExchange.sendResponseHeaders(OK, RESPONSE_LENGTH);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class PrioritizedTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasksList();
            String response = gson.toJson(prioritizedTasks);

            httpExchange.sendResponseHeaders(OK, RESPONSE_LENGTH);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public void stop() {
        server.stop(0);
    }
}
