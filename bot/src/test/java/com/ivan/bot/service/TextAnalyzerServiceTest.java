package com.ivan.bot.service;

import com.ivan.bot.dto.TextAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextAnalyzerServiceTest {

    private TextAnalyzerService textAnalyzerService;

    @BeforeEach
    void setUp() {
        textAnalyzerService = new TextAnalyzerService();
    }

    @Test
    void testWeatherIntent_Ukrainian() {
        String intent = textAnalyzerService.analyzeIntent("Яка сьогодні погода?");
        assertEquals("weather", intent);
    }

    @Test
    void testWeatherIntent_English() {
        String intent = textAnalyzerService.analyzeIntent("What's the weather like today?");
        assertEquals("weather", intent);
    }

    @Test
    void testCurrencyIntent_Ukrainian() {
        String intent = textAnalyzerService.analyzeIntent("Який курс долара?");
        assertEquals("currency", intent);
    }

    @Test
    void testCurrencyIntent_English() {
        String intent = textAnalyzerService.analyzeIntent("What's the exchange rate for USD?");
        assertEquals("currency", intent);
    }

    @Test
    void testNewsIntent_Ukrainian() {
        String intent = textAnalyzerService.analyzeIntent("Які останні новини?");
        assertEquals("news", intent);
    }

    @Test
    void testNewsIntent_English() {
        String intent = textAnalyzerService.analyzeIntent("What's new today?");
        assertEquals("news", intent);
    }

    @Test
    void testUnknownIntent() {
        String intent = textAnalyzerService.analyzeIntent("Hello there!");
        assertEquals("unknown", intent);
    }

    @Test
    void testExtractLocation_Ukrainian() {
        String location = textAnalyzerService.extractLocation("Яка погода у Києві?");
        assertEquals("Києві", location);
    }

    @Test
    void testExtractLocation_English() {
        String location = textAnalyzerService.extractLocation("What's the weather in London?");
        assertEquals("London", location);
    }

    @Test
    void testExtractCurrency_USD() {
        String currency = textAnalyzerService.extractCurrency("Курс USD сьогодні");
        assertEquals("USD", currency);
    }

    @Test
    void testExtractCurrency_EUR() {
        String currency = textAnalyzerService.extractCurrency("Скільки коштує євро?");
        assertEquals("EUR", currency);
    }

    @Test
    void testFullAnalysis() {
        TextAnalysisResult result = textAnalyzerService.analyzeText("Яка погода у Києві?");

        assertNotNull(result);
        assertEquals("weather", result.getIntent());
        assertEquals("Києві", result.getLocation());
        assertTrue(result.getConfidence() > 0);
    }

    @Test
    void testFullAnalysis_Currency() {
        TextAnalysisResult result = textAnalyzerService.analyzeText("Курс долара USD");

        assertNotNull(result);
        assertEquals("currency", result.getIntent());
        assertEquals("USD", result.getCurrency());
        assertTrue(result.getConfidence() > 0);
    }
}

