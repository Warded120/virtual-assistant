package com.ivan.bot.service;

import com.ivan.bot.builder.RequestBuilder;
import com.ivan.bot.dto.request.BotRequest;
import com.ivan.bot.dto.request.UnknownRequest;
import com.ivan.bot.enumeration.UpdateIntent;
import lombok.RequiredArgsConstructor;
import opennlp.tools.tokenize.TokenizerME;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class NlpPipeline {

    private final TokenizerME    tokenizer;
    private final IntentDetector intentDetector;
    private final RequestBuilder weatherRequestBuilder;
    private final RequestBuilder currencyRequestBuilder;

    //TODO: handle when no cities or currencies found
    public BotRequest parse(String sentence) {
        String[] tokens        = tokenizer.tokenize(sentence);
        String[] tokensLower   = Arrays.stream(tokens)
                                       .map(String::toLowerCase)
                                       .toArray(String[]::new);

        UpdateIntent intent = intentDetector.detect(tokensLower);

        return switch (intent) {
            case WEATHER  -> weatherRequestBuilder.buildRequest(tokensLower);
            case CURRENCY -> currencyRequestBuilder.buildRequest(tokensLower);
            case UNKNOWN  -> new UnknownRequest();
        };
    }
}