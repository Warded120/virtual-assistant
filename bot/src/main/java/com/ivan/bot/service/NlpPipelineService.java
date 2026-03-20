package com.ivan.bot.service;

import com.ivan.bot.builder.RequestBuilder;
import com.ivan.bot.dto.request.BotRequest;
import com.ivan.bot.dto.request.ProfileActionRequest;
import com.ivan.bot.dto.request.UnknownRequest;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.enumeration.UpdateIntent;
import lombok.RequiredArgsConstructor;
import opennlp.tools.tokenize.TokenizerME;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class NlpPipelineService {

    private final TokenizerME tokenizer;
    private final IntentDetectorService intentDetectorService;
    private final RequestBuilder weatherRequestBuilder;
    private final RequestBuilder currencyRequestBuilder;

    public BotRequest parse(String sentence, Long chatId) {
        return parse(sentence, chatId, null);
    }

    public BotRequest parse(String sentence, Long chatId, String telegramUsername) {
        String[] tokens = tokenizer.tokenize(sentence);
        String[] tokensLower = Arrays.stream(tokens)
                                     .map(String::toLowerCase)
                                     .toArray(String[]::new);

        UpdateIntent intent = intentDetectorService.detect(tokensLower);
        Language detectedLanguage = intentDetectorService.detectLanguage(tokens);

        return switch (intent) {
            case WEATHER -> weatherRequestBuilder.buildRequest(tokensLower, chatId);
            case CURRENCY -> currencyRequestBuilder.buildRequest(tokensLower, chatId);
            case CREATE_PROFILE, UPDATE_PROFILE, VIEW_PROFILE -> ProfileActionRequest.builder()
                    .chatId(chatId)
                    .telegramUsername(telegramUsername)
                    .action(intent)
                    .detectedLanguage(detectedLanguage)
                    .build();
            case UNKNOWN -> UnknownRequest.builder()
                    .chatId(chatId)
                    .detectedLanguage(detectedLanguage)
                    .build();
        };
    }
}