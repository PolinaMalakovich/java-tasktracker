package ru.yandex.malakovich.tasktracker.manager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.malakovich.tasktracker.KVServer;

import java.io.IOException;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static KVServer kvServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }

    @Override
    protected HttpTaskManager createManager() {
        return new HttpTaskManager("http://localhost:8087");
    }
}
