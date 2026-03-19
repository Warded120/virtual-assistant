package com.ivan.bot.builder;

import com.ivan.bot.dictionary.CityDictionary;
import com.ivan.bot.dto.request.WeatherBotRequest;
import lombok.RequiredArgsConstructor;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Component;
import java.util.Optional;

@RequiredArgsConstructor
@Component("weatherRequestBuilder")
public class WeatherRequestBuilder implements RequestBuilder {
    private final NameFinderME locationFinder;
    private final CityDictionary cityDictionary;

    @Override
    public WeatherBotRequest buildRequest(String[] tokens) {
        Optional<String> city = extractCity(tokens);

        return city.map(WeatherBotRequest::new)
                .orElseGet(() -> new WeatherBotRequest(getFavouriteCity()));
    }

    private String getFavouriteCity() {
        return "Chernivtsi";
    }

    private Optional<String> extractCity(String[] tokens) {
        Optional<String> nerResult = extractCityViaNer(tokens);
        if (nerResult.isPresent()) return nerResult;

        for (String token : tokens) {
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
}
