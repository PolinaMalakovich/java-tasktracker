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

    public KVTaskClient(String url) {
        httpClient = HttpClient.newHttpClient();
        this.host = url;
        apiToken = register(httpClient, host);
    }

    public void put(String key, String json) {
        URI uri = URI.create(host + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != OK) {
                throw new RuntimeException("Не удалось сохранить значение");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String load(String key) {
        URI uri = URI.create(host + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == OK) {
                return response.body();
            } else {
                throw new RuntimeException("Не удалось получить значение от сервера");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String register(HttpClient httpClient, String host) {
        URI uri = URI.create(host + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == OK) {
                return response.body();
            } else {
                throw new RuntimeException("Не удалось получить apiToken от сервера");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
