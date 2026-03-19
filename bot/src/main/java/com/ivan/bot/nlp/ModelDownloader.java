package com.ivan.bot.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class ModelDownloader {

    private static final Logger log = LoggerFactory.getLogger(ModelDownloader.class);

    // Базовий URL офіційного репозиторію Apache OpenNLP
    private static final String BASE_URL =
            "https://dlcdn.apache.org/opennlp/models/ud-models-1.0/";

    // Старіші, але більш сумісні моделі 1.5
    private static final String LEGACY_BASE_URL =
            "https://opennlp.sourceforge.net/models-1.5/";

    private final Path modelsDir;
    private final HttpClient httpClient;

    public ModelDownloader(Path modelsDir) {
        this.modelsDir = modelsDir;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public Path downloadIfAbsent(String modelFileName) throws IOException {
        Path localPath = modelsDir.resolve(modelFileName);

        if (Files.exists(localPath) && Files.size(localPath) > 0) {
            log.info("Модель вже існує: {}", modelFileName);
            return localPath;
        }

        log.info("Завантаження моделі: {} ...", modelFileName);
        Files.createDirectories(modelsDir);

        String url = LEGACY_BASE_URL + modelFileName;
        try {
            download(url, localPath);
            log.info("Модель успішно завантажена: {}", modelFileName);
        } catch (Exception e) {
            log.warn("Не вдалось завантажити з основного URL, спроба резервного: {}", e.getMessage());
            // Видаляємо пошкоджений файл
            Files.deleteIfExists(localPath);
            throw new IOException("Не вдалось завантажити модель: " + modelFileName, e);
        }

        return localPath;
    }

    private void download(String url, Path destination) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " для URL: " + url);
        }

        try (InputStream in = response.body()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}