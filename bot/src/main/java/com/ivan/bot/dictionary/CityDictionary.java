package com.ivan.bot.dictionary;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CityDictionary {

    private final Map<String, String> citiesEn = new HashMap<>();
    private final Map<String, String> citiesUk = new HashMap<>();

    @PostConstruct
    private void loadCurrencies() {
        loadCsv("dictionaries/cities_en.csv", citiesEn);
        loadCsv("dictionaries/cities_uk.csv", citiesUk);
    }

    private void loadCsv(String resourcePath, Map<String, String> map) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String city = parts[0].trim();
                    String name = parts[1].trim();
                    if (!name.isEmpty()) {
                        map.put(name.toLowerCase(), city);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load city dictionary: " + resourcePath, e);
        }
    }

    public String resolve(String token) {
        var lowerToken = Optional.of(token)
                .map(String::toLowerCase)
                .orElse(null);

        return Optional.of(lowerToken)
                .map(this::resolveEn)
                .orElseGet(() ->
                        Optional.of(lowerToken)
                                .map(this::resolveUk)
                                .orElse(null)
                );
    }

    private String resolveEn(String token) {
        return Optional.of(token)
                .map(citiesEn::get)
                .orElseGet(() -> matchEn(token));
    }

    private String matchEn(String token) {
        if(token == null || token.length() <= 3) {
            return null;
        }
        for (Map.Entry<String, String> entry : citiesEn.entrySet()) {
            if (entry.getKey().contains(token)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String resolveUk(String token) {
        return Optional.of(token)
                .map(citiesUk::get)
                .orElseGet(() -> matchUk(token));
    }

    private String matchUk(String token) {
        if(token == null || token.length() <= 3) {
            return null;
        }
        for (Map.Entry<String, String> entry : citiesUk.entrySet()) {
            if (entry.getKey().contains(token)) {
                return entry.getValue();
            }
        }
        return null;
    }
}