package com.ivan.bot.service;

import com.ivan.bot.enumeration.UpdateIntent;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class IntentDetector {

    private static final Set<String> WEATHER_KW = Set.of(
        "weather", "forecast", "temperature", "rain", "snow",
        "sunny", "cloudy", "wind", "humid", "storm", "climate",

        "погода", "погоду", "погоди", "прогноз", "температура",
        "температуру", "дощ", "сніг", "вітер", "хмарно", "сонячно",
        "мороз", "гроза", "туман", "опади", "холодно", "тепло", "тепла", "жарко", "прохолодно"
    );

    private static final Set<String> CURRENCY_KW = Set.of(
        "currency", "exchange", "rate", "convert", "conversion",
        "forex", "price", "cost", "worth",

        "курс", "валюта", "валют", "гроші", "валюти", "обмін", "конвертація",
        "конвертувати", "ціна", "вартість"
    );

    public UpdateIntent detect(String[] tokens) {
        int weatherScore  = 0;
        int currencyScore = 0;

        for (String token : tokens) {
            if (WEATHER_KW.contains(token))  weatherScore++;
            if (CURRENCY_KW.contains(token)) currencyScore++;
        }

        if (weatherScore == 0 && currencyScore == 0) return UpdateIntent.UNKNOWN;
        if (weatherScore > currencyScore)             return UpdateIntent.WEATHER;
        if (currencyScore > weatherScore)             return UpdateIntent.CURRENCY;

        return UpdateIntent.UNKNOWN;
    }
}