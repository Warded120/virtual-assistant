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
public class CurrencyDictionary {
    private final Map<String, String> currenciesEn = new HashMap<>();
    private final Map<String, String> currenciesUk = new HashMap<>();

    @PostConstruct
    private void loadCurrencies() {
        loadCsv("dictionaries/currencies_en.csv", currenciesEn);
        loadCsv("dictionaries/currencies_uk.csv", currenciesUk);
    }

    //TODO: add different key variations for en csv file (e.g. "dollar", "dollars", "usd", "us dollar" etc.)
    //TODO: add different key variations for uk csv file (e.g. "долар", "долари", "доларів", "долар США" etc.)
    private void loadCsv(String resourcePath, Map<String, String> map) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String currency = parts[0].trim();
                    String text = parts[1].trim();
                    if (!text.isEmpty()) {
                        map.put(text.toLowerCase(), currency);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load currency dictionary: " + resourcePath, e);
        }
    }

    public Optional<String> resolve(String token) {
        String lowerToken = token.toLowerCase();
        // Search English map
        for (Map.Entry<String, String> entry : currenciesEn.entrySet()) {
            if (entry.getKey().contains(lowerToken)) {
                return Optional.of(entry.getValue());
            }
        }
        // Search Ukrainian map
        for (Map.Entry<String, String> entry : currenciesUk.entrySet()) {
            if (entry.getKey().contains(lowerToken)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }
}