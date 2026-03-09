package com.ivan.bot.service;

import com.ivan.bot.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NlpDecisionService {
    private final Map<String, CommandHandler> handlers;

    public SendMessage decideResponse(Update update, String command) {
        CommandHandler handler = handlers.get(command);
        return handler.handle(update);
    }
}
