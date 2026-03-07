package com.ivan.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandHandlerManager {

    private final Map<String, CommandHandler> handlers;
    private final CommandHandler NlpHandler;

    public SendMessage handle(Update update) {
        return handlers.getOrDefault(
                        getCommand(update),
                        NlpHandler
                )
                .handle(update);
    }

    private static String getCommand(Update update) {
        return update.getMessage().getText();
    }
}
