package ru.yandex.malakovich.tasktracker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.malakovich.tasktracker.HttpStatus.OK;

public class HttpTaskServerTest {
    private static HttpTaskServer taskServer;
    private static KVServer kvServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
    }

    @AfterAll
    static void afterAll() {
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> tasks = new Gson().fromJson(response.body(), listType);

        assertEquals(OK, response.statusCode());
        assertEquals(new ArrayList<>(), tasks);
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> subtasks = new Gson().fromJson(response.body(), listType);

        assertEquals(OK, response.statusCode());
        assertEquals(new ArrayList<>(), subtasks);
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Epic>>(){}.getType();
        List<Epic> epics = new Gson().fromJson(response.body(), listType);

        assertEquals(OK, response.statusCode());
        assertEquals(new ArrayList<>(), epics);
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Epic> history = new Gson().fromJson(response.body(), listType);

        assertEquals(OK, response.statusCode());
        assertEquals(new ArrayList<>(), history);
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Epic> history = new Gson().fromJson(response.body(), listType);

        assertEquals(OK, response.statusCode());
        assertEquals(new ArrayList<>(), history);
    }
}
