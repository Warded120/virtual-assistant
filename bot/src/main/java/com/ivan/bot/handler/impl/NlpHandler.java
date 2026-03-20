package com.ivan.bot.handler.impl;

import com.ivan.bot.dto.request.BotRequest;
import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.service.NlpDecisionService;
import com.ivan.bot.service.NlpPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class NlpHandler implements CommandHandler {

    private final NlpDecisionService decisionService;
    private final NlpPipeline nlpPipeline;

    public SendMessage handle(Update update) {
        String text = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        Long chatIdLong = update.getMessage().getChatId();
        String telegramUsername = update.getMessage().getFrom().getUserName();

        log.info("Processing free text: {}", text);

        BotRequest request = nlpPipeline.parse(text, chatIdLong, telegramUsername);

        var response = decisionService.decideResponse(request);

        return SendMessage.builder()
                .chatId(chatId)
                .text(response.getMessage())
                .build();
    }
}
