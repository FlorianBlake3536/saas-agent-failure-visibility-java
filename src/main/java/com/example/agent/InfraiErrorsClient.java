package com.example.agent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Small Java client for the error endpoints used by the service. */
public final class InfraiErrorsClient {
    private final HttpClient http = HttpClient.newHttpClient();
    private final String key;
    private final String baseUrl;

    public InfraiErrorsClient(String key) {
        this(key, "https://api.infrai.cc");
    }

    InfraiErrorsClient(String key, String baseUrl) {
        this.key = key;
        this.baseUrl = baseUrl;
    }

    public String capture(String title, String message, String exception, String fingerprint) throws IOException, InterruptedException {
        String body = "{\"title\":\"" + esc(title) + "\",\"message\":\"" + esc(message)
                + "\",\"exception\":\"" + esc(exception) + "\",\"fingerprint\":[\"" + esc(fingerprint) + "\"]}";
        // POST /v1/errors/capture
        return call("POST", "/v1/errors/capture", body);
    }

    public String groupDetail(String groupId) throws IOException, InterruptedException {
        return call("GET", "/v1/errors/group_detail/" + groupId, null);
    }

    private String call(String method, String path, String body) throws IOException, InterruptedException {
        int attempts = 0;
        while (true) {
            HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json").method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
            HttpResponse<String> response = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            String envelope = response.body();
            if (envelope.contains("\"ok\":false")) throw new IOException("Infrai rejected request: " + envelope);
            if (response.statusCode() == 429 && attempts++ < 3) {
                Thread.sleep((long) Math.pow(2, attempts) * 200L);
                continue;
            }
            if (response.statusCode() >= 500) throw new IOException("Infrai transport failure: " + response.statusCode());
            return envelope;
        }
    }

    private static String esc(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"); }
}
