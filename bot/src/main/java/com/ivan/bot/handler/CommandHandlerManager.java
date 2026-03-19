package com.ivan.bot.handler;

import com.ivan.bot.handler.impl.NlpHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandHandlerManager {

    private final NlpHandler nlpHandler;

    public SendMessage handle(Update update) {
        return nlpHandler.handle(update);
    }

    private static String getCommand(Update update) {
        return update.getMessage().getText();
    }
}
