package com.ivan.bot.config;

import com.ivan.bot.annotation.Command;
import com.ivan.bot.handler.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class CommandHandlerConfig {

    @Bean("handlers")
    public Map<String, CommandHandler> commandHandlerMap(List<CommandHandler> handlers) {
        Map<String, CommandHandler> map = new HashMap<>();
        for (CommandHandler handler : handlers) {
            Command command = handler.getClass().getAnnotation(Command.class);
            if (command != null) {
                map.put(command.value(), handler);
            } else {
                log.warn("CommandHandler {} is missing @Command annotation", handler.getClass().getName());
            }
        }
        return map;
    }
}