package com.ivan.bot.handler.impl;

import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.service.TextAnalyzerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class NlpHandler implements CommandHandler {

    private final TextAnalyzerService textAnalyzerService;

    /**
     * Handles free text input by analyzing it and routing to appropriate handler
     * @param update Telegram update containing the message
     * @return SendMessage response
     */
    public SendMessage handle(Update update) {
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        log.info("Processing free text: {}", text);

        // Analyze the intent
        String intent = textAnalyzerService.analyzeIntent(text);

        // Map intent to command
        String command = mapIntentToCommand(intent);

//        if (command != null && handlers.containsKey(command)) {
//            log.info("Routing to command: {}", command);
//            CommandHandler handler = handlers.get(command);
//            return handler.handle(update);
//        }

        // If no intent detected, provide helpful message
        return SendMessage.builder()
                .chatId(chatId)
                .text(buildHelpMessage(text))
                .build();
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

