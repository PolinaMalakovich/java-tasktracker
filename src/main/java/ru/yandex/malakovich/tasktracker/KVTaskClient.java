package ru.yandex.malakovich.tasktracker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static ru.yandex.malakovich.tasktracker.HttpStatus.OK;

public class KVTaskClient {
    private final HttpClient httpClient;
    private final String host;
    private final String apiToken;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        this.host = url;
        apiToken = register(httpClient, host);
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(host + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != OK) {
            throw new RuntimeException("Не удалось сохранить значение");
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(host + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == OK) {
            return response.body();
        } else {
            throw new RuntimeException("Не удалось получить значение от сервера");
        }
    }

    private static String register(HttpClient httpClient, String host) throws IOException, InterruptedException {
        URI uri = URI.create(host + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == OK) {
            return response.body();
        } else {
            throw new RuntimeException("Не удалось получить apiToken от сервера");
        }
    }
}
