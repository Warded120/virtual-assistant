package com.ivan.bot.dictionary;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class CurrencyDictionary {

    private static final Map<String, String> CURRENCIES = Map.ofEntries(
        Map.entry("usd", "USD"),
        Map.entry("eur", "EUR"),
        Map.entry("uah", "UAH"),
        Map.entry("gbp", "GBP"),
        Map.entry("pln", "PLN"),
        Map.entry("chf", "CHF"),
        Map.entry("jpy", "JPY"),
        Map.entry("cny", "CNY"),
        Map.entry("czk", "CZK"),
        Map.entry("cad", "CAD"),
        Map.entry("aud", "AUD"),

        Map.entry("dollar",   "USD"),
        Map.entry("dollars",  "USD"),
        Map.entry("euro",     "EUR"),
        Map.entry("euros",    "EUR"),
        Map.entry("hryvnia",  "UAH"),
        Map.entry("hryvnias", "UAH"),
        Map.entry("pound",    "GBP"),
        Map.entry("pounds",   "GBP"),
        Map.entry("zloty",    "PLN"),
        Map.entry("franc",    "CHF"),
        Map.entry("yen",      "JPY"),
        Map.entry("yuan",     "CNY"),

        Map.entry("долар",    "USD"),
        Map.entry("долари",   "USD"),
        Map.entry("доларів",  "USD"),
        Map.entry("дол",      "USD"),
        Map.entry("євро",     "EUR"),
        Map.entry("гривня",   "UAH"),
        Map.entry("гривні",   "UAH"),
        Map.entry("гривень",  "UAH"),
        Map.entry("грн",      "UAH"),
        Map.entry("фунт",     "GBP"),
        Map.entry("фунтів",   "GBP"),
        Map.entry("злотий",   "PLN"),
        Map.entry("злотих",   "PLN"),
        Map.entry("франк",    "CHF"),
        Map.entry("єна",      "JPY"),
        Map.entry("юань",     "CNY")
    );

    public Optional<String> resolve(String token) {
        return Optional.ofNullable(CURRENCIES.get(token.toLowerCase()));
    }
}