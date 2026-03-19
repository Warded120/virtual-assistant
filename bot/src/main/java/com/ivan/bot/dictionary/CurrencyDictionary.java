package com.ivan.bot.dictionary;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.ivan.bot.constant.Constants.DEFAULT_CURRENCY;

@Component
public class CurrencyDictionary {
    private final Map<String, String> currenciesEn = new HashMap<>();
    private final Map<String, String> currenciesUk = new HashMap<>();

    @PostConstruct
    private void loadCurrencies() {
        loadCsv("dictionaries/currencies_en.csv", currenciesEn);
        loadCsv("dictionaries/currencies_uk.csv", currenciesUk);
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
                .map(currenciesEn::get)
                .orElseGet(() -> matchEn(token));
    }

    private String matchEn(String token) {
        for (Map.Entry<String, String> entry : currenciesEn.entrySet()) {
            if (entry.getKey().contains(token)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String resolveUk(String token) {
        return Optional.of(token)
                .map(currenciesUk::get)
                .orElseGet(() -> matchUk(token));
    }

    private String matchUk(String token) {
        for (Map.Entry<String, String> entry : currenciesUk.entrySet()) {
            if (entry.getKey().contains(token)) {
                return entry.getValue();
            }
        }
        return null;
    }
}