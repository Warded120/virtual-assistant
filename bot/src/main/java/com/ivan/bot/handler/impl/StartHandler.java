package com.ivan.bot.handler.impl;

import com.ivan.bot.annotation.Command;
import com.ivan.bot.handler.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Command("/start")
public class StartHandler implements CommandHandler {

    @Override
    public SendMessage handle(Update update) {
        long chat_id = update.getMessage().getChatId();

        return SendMessage
                .builder()
                .chatId(chat_id)
                .text("Hello %s".formatted(update.getMessage().getFrom().getFirstName()))
                .build();
    }
}
