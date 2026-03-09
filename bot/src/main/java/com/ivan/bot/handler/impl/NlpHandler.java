package com.ivan.bot.handler.impl;

import com.ivan.bot.service.NlpDecisionService;
import com.ivan.bot.service.TextAnalyzerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class NlpHandler {

    private final TextAnalyzerService textAnalyzerService;
    private final NlpDecisionService decisionService;

    /**
     * Handles free text input by analyzing it and routing to appropriate handler
     * @param update Telegram update containing the message
     * @return SendMessage response
     */
    public SendMessage handle(Update update) {
        String text = update.getMessage().getText();

        log.info("Processing free text: {}", text);

        // Analyze the intent
        String intent = textAnalyzerService.analyzeIntent(text);

        // Map intent to command
        String command = mapIntentToCommand(intent);

        return decisionService.decideResponse(update, command);
    }

    /**
     * Maps detected intent to a command
     */
    private String mapIntentToCommand(String intent) {
        return switch (intent) {
            case "weather" -> "/weather";
            case "currency" -> "/currency";
            case "news" -> "/news";
            default -> null;
        };
    }

    /**
     * Builds a helpful message when intent cannot be determined
     */
    private String buildHelpMessage(String originalText) {
        return """
                Вибачте, я не зрозумів ваш запит: "%s"
                
                Я можу допомогти з наступним:
                🌤 Погода - напишіть "погода" або "яка погода?"
                💱 Курс валют - напишіть "курс валют" або "долар євро"
                📰 Новини - напишіть "новини" або "що нового?"
                
                Або використовуйте команди:
                /weather - дізнатися погоду
                /currency - курс валют
                /news - останні новини
                /start - початок роботи
                """.formatted(originalText);
    }
}

