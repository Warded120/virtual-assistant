package com.ivan.bot.handler.impl;

import com.ivan.bot.annotation.Command;
import com.ivan.bot.handler.CommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Command("/weather")
public class WeatherHandler implements CommandHandler {
    @Override
    public SendMessage handle(Update update) {
        return null;
    }
}
