package com.ivan.bot.builder.impl;

import com.ivan.bot.builder.RequestBuilder;
import com.ivan.bot.dictionary.CityDictionary;
import com.ivan.bot.dto.request.WeatherBotRequest;
import lombok.RequiredArgsConstructor;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.ivan.bot.constant.Constants.DEFAULT_CITY;

@RequiredArgsConstructor
@Component("weatherRequestBuilder")
public class WeatherRequestBuilder implements RequestBuilder {
    private final NameFinderME locationFinder;
    private final CityDictionary cityDictionary;

    @Override
    public WeatherBotRequest buildRequest(String[] tokens) {
        String city = extractCity(tokens);

        return new WeatherBotRequest(city);
    }


    private String extractCity(String[] tokens) {
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
                                    .orElse(DEFAULT_CITY)
                    );
            locationFinder.clearAdaptiveData();
            return city;
        } catch (Exception e) {
            locationFinder.clearAdaptiveData();
            return null;
        }
    }
}