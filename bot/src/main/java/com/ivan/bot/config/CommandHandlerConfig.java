package com.ivan.bot.config;

import com.ivan.bot.annotation.Command;
import com.ivan.bot.handler.CommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CommandHandlerConfig {

    @Bean("handlers")
    public Map<String, CommandHandler> commandHandlerMap(List<CommandHandler> handlers) {
        Map<String, CommandHandler> map = new HashMap<>();
        for (CommandHandler handler : handlers) {
            Command command = handler.getClass().getAnnotation(Command.class);
            if (command == null) {
                throw new IllegalStateException("CommandHandler " + handler.getClass().getName() + " is missing @Command annotation");
            }
            map.put(command.value(), handler);
        }
        return map;
    }
}