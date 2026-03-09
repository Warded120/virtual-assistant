package com.ivan.bot.service;

import com.ivan.bot.dto.TextAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TextAnalyzerService {

    private static final Map<String, List<String>> INTENT_KEYWORDS = new HashMap<>();

    static {
        // Weather-related keywords
        INTENT_KEYWORDS.put("weather", Arrays.asList(
                "погода", "weather", "температура", "temperature", "дощ", "rain",
                "сонце", "sun", "хмарно", "cloudy", "холодно", "cold", "тепло", "warm"
        ));

        // Currency-related keywords
        INTENT_KEYWORDS.put("currency", Arrays.asList(
                "валюта", "currency", "курс", "exchange", "долар", "dollar", "євро", "euro",
                "гривня", "hryvnia", "usd", "eur", "uah", "обмін", "exchange rate"
        ));

        // News-related keywords
        INTENT_KEYWORDS.put("news", Arrays.asList(
                "новини", "news", "новость", "стаття", "article", "події", "events",
                "що нового", "what's new", "що сталося", "happened"
        ));
    }

    /**
     * Performs complete text analysis including intent, entities, and confidence
     * @param text User input text
     * @return TextAnalysisResult with all extracted information
     */
    public TextAnalysisResult analyzeText(String text) {
        String intent = analyzeIntent(text);
        String location = extractLocation(text);
        String currency = extractCurrency(text);
        double confidence = calculateConfidence(text, intent);

        return TextAnalysisResult.builder()
                .intent(intent)
                .location(location)
                .currency(currency)
                .confidence(confidence)
                .build();
    }

    /**
     * Calculates confidence score for the detected intent
     */
    private double calculateConfidence(String text, String intent) {
        if ("unknown".equals(intent)) {
            return 0.0;
        }

        String normalizedText = text.toLowerCase().trim();
        List<String> keywords = INTENT_KEYWORDS.get(intent);

        long matchCount = keywords.stream()
                .filter(keyword -> normalizedText.contains(keyword.toLowerCase()))
                .count();

        // Confidence is based on keyword match ratio
        return Math.min(1.0, (double) matchCount / 3.0);
    }

    /**
     * Analyzes text and determines the intent based on keywords
     * @param text User input text
     * @return Detected intent or "unknown" if no intent matched
     */
    public String analyzeIntent(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "unknown";
        }

        String normalizedText = text.toLowerCase().trim();
        log.info("Analyzing text: {}", normalizedText);

        Map<String, Integer> intentScores = new HashMap<>();

        // Score each intent based on keyword matches
        for (Map.Entry<String, List<String>> entry : INTENT_KEYWORDS.entrySet()) {
            String intent = entry.getKey();
            List<String> keywords = entry.getValue();

            int score = 0;
            for (String keyword : keywords) {
                if (normalizedText.contains(keyword.toLowerCase())) {
                    score++;
                }
            }

            if (score > 0) {
                intentScores.put(intent, score);
            }
        }

        // Return the intent with the highest score
        if (intentScores.isEmpty()) {
            log.info("No intent detected");
            return "unknown";
        }

        String detectedIntent = intentScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");

        log.info("Detected intent: {} with score: {}", detectedIntent, intentScores.get(detectedIntent));
        return detectedIntent;
    }

    /**
     * Extracts location from text if present
     * @param text User input text
     * @return Extracted location or null
     */
    public String extractLocation(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        // Simple location extraction - looks for "в/у/in" followed by a word
        Pattern locationPattern = Pattern.compile("(?:в|у|in)\\s+([А-Яа-яA-Za-z]+)", Pattern.CASE_INSENSITIVE);
        var matcher = locationPattern.matcher(text);

        if (matcher.find()) {
            String location = matcher.group(1);
            log.info("Extracted location: {}", location);
            return location;
        }

        return null;
    }

    /**
     * Extracts currency code from text if present
     * @param text User input text
     * @return Extracted currency code or null
     */
    public String extractCurrency(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String normalizedText = text.toUpperCase();

        // Look for common currency codes
        if (normalizedText.contains("USD") || normalizedText.contains("ДОЛАР")) {
            return "USD";
        }
        if (normalizedText.contains("EUR") || normalizedText.contains("ЄВРО")) {
            return "EUR";
        }
        if (normalizedText.contains("UAH") || normalizedText.contains("ГРИВН")) {
            return "UAH";
        }

        return null;
    }
}


