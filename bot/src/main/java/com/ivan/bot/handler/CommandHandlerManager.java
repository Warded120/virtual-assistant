package com.ivan.bot.handler;

import com.ivan.bot.handler.impl.NlpHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandHandlerManager {

    private final Map<String, CommandHandler> handlers;
    private final NlpHandler nlpHandler;

    public SendMessage handle(Update update) {
        var handler = handlers.get(getCommand(update));
        if(handler == null) {
            return nlpHandler.handle(update);
        }
        return handler.handle(update);
    }

    private static String getCommand(Update update) {
        return update.getMessage().getText();
    }
}
