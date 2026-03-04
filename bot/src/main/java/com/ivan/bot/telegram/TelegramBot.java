package com.ivan.bot.telegram;

import com.ivan.bot.config.BotConfig;
import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.handler.impl.FreeTextHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final BotConfig botConfig;
    private final FreeTextHandler freeTextHandler;

    private final Map<String, CommandHandler> handlers;

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        log.info("Received update");
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            SendMessage message;

            // Check if it's a command (starts with /)
            if (messageText.startsWith("/")) {
                CommandHandler commandHandler = handlers.get(messageText);
                if (commandHandler != null) {
                    log.info("Processing command: {}", messageText);
                    message = commandHandler.handle(update);
                } else {
                    log.warn("Unknown command: {}", messageText);
                    message = SendMessage.builder()
                            .chatId(update.getMessage().getChatId().toString())
                            .text("Невідома команда. Використовуйте /start для допомоги.")
                            .build();
                }
            } else {
                // Process as free text
                log.info("Processing free text message");
                message = freeTextHandler.handle(update);
            }

            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error("Error sending message", e);
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}