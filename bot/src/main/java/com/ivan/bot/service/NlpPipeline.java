package com.ivan.bot.service;

import com.ivan.bot.dictionary.CityDictionary;
import com.ivan.bot.dictionary.CurrencyDictionary;
import com.ivan.bot.dto.request.BotRequest;
import com.ivan.bot.dto.request.CurrencyBotRequest;
import com.ivan.bot.dto.request.WeatherBotRequest;
import com.ivan.bot.enumeration.UpdateIntent;
import lombok.RequiredArgsConstructor;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NlpPipeline {

    private static final Logger log = LoggerFactory.getLogger(NlpPipeline.class);

    private final TokenizerME    tokenizer;
    private final NameFinderME   locationFinder;
    private final IntentDetector intentDetector;
    private final CityDictionary cityDictionary;
    private final CurrencyDictionary currencyDictionary;

    public BotRequest parse(String sentence) {
        String[] tokens        = tokenizer.tokenize(sentence);
        String[] tokensLower   = Arrays.stream(tokens)
                                       .map(String::toLowerCase)
                                       .toArray(String[]::new);

        UpdateIntent intent = intentDetector.detect(tokensLower);

        return switch (intent) {
            case WEATHER  -> buildWeatherRequest(sentence, tokens, tokensLower);
            case CURRENCY -> buildCurrencyRequest(sentence, tokensLower);
            case UNKNOWN  -> null;
        };
    }

    private WeatherBotRequest buildWeatherRequest(String sentence,
                                                  String[] tokens,
                                                  String[] tokensLower) {
        Optional<String> city = extractCity(tokens, tokensLower);

        return city.map(WeatherBotRequest::new)
                   .orElseGet(() -> new WeatherBotRequest(""));
    }

    private Optional<String> extractCity(String[] tokens, String[] tokensLower) {
        Optional<String> nerResult = extractCityViaNer(tokens);
        if (nerResult.isPresent()) return nerResult;

        for (String token : tokensLower) {
            Optional<String> dict = cityDictionary.resolve(token);
            if (dict.isPresent()) {
                return dict;
            }
        }

        return Optional.empty();
    }

    private Optional<String> extractCityViaNer(String[] tokens) {
        try {
            Span[] spans = locationFinder.find(tokens);
            locationFinder.clearAdaptiveData();

            if (spans.length == 0) return Optional.empty();

            String city = Span.spansToStrings(new Span[]{spans[0]}, tokens)[0];
            return Optional.of(city);

        } catch (Exception e) {
            locationFinder.clearAdaptiveData();
            return Optional.empty();
        }
    }

    private CurrencyBotRequest buildCurrencyRequest(String sentence, String[] tokensLower) {
        List<String> found = new ArrayList<>();

        for (String token : tokensLower) {
            currencyDictionary.resolve(token).ifPresent(code -> {
                if (!found.contains(code)) found.add(code);
            });
            if (found.size() == 2) break;
        }

        return switch (found.size()) {
            case 2  -> new CurrencyBotRequest(found.get(0), found.get(1));
            case 1  -> new CurrencyBotRequest(found.get(0), "UAH");
            default -> new CurrencyBotRequest("", "");
        };
    }
}