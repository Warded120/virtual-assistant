package com.ivan.bot.builder.impl;

import com.ivan.bot.builder.RequestBuilder;
import com.ivan.bot.dictionary.CityDictionary;
import com.ivan.bot.dto.request.WeatherBotRequest;
import com.ivan.bot.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component("weatherRequestBuilder")
public class WeatherRequestBuilder implements RequestBuilder {
    private final NameFinderME locationFinder;
    private final CityDictionary cityDictionary;
    private final UserProfileService userProfileService;

    @Override
    public WeatherBotRequest buildRequest(String[] tokens, Long chatId) {
        String city = extractCity(tokens, chatId);
        return new WeatherBotRequest(city, chatId);
    }

    private String extractCity(String[] tokens, Long chatId) {
        // Get user's favourite city as default
        String defaultCity = userProfileService.getFavouriteCity(chatId);

        try {
            var city = Optional.of(tokens)
                    .map(locationFinder::find)
                    .filter(spans -> spans.length > 0)
                    .map(spans -> Span.spansToStrings(new Span[]{spans[0]}, tokens)[0])
                    .orElseGet(() ->
                            Stream.of(tokens)
                                    .map(cityDictionary::resolve)
                                    .filter(Objects::nonNull)
                                    .findFirst()
                                    .orElse(defaultCity)
                    );
            locationFinder.clearAdaptiveData();
            return city;
        } catch (Exception e) {
            locationFinder.clearAdaptiveData();
            return defaultCity;
        }
    }
}