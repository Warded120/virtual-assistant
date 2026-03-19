package com.ivan.bot.dictionary;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Transliteration-aware city dictionary.
 *
 * Why this exists:
 *   OpenNLP's en-ner-location.bin is trained on English text.
 *   Ukrainian city names written in Cyrillic ("Київ", "Харків") are
 *   invisible to it.  This dictionary maps every known spelling to a
 *   canonical English name that can be forwarded to weather APIs.
 *
 * Lookup is case-insensitive.
 *
 * The map key is the form the user might type; the value is the
 * canonical name used when calling the weather API.
 */
@Component
public class CityDictionary {

    private static final Map<String, String> CITIES = Map.ofEntries(
        Map.entry("київ",            "Kyiv"),
        Map.entry("киев",            "Kyiv"),
        Map.entry("харків",          "Kharkiv"),
        Map.entry("харьков",         "Kharkiv"),
        Map.entry("одеса",           "Odesa"),
        Map.entry("одесса",          "Odesa"),
        Map.entry("дніпро",          "Dnipro"),
        Map.entry("дніпропетровськ", "Dnipro"),
        Map.entry("запоріжжя",       "Zaporizhzhia"),
        Map.entry("львів",           "Lviv"),
        Map.entry("львов",           "Lviv"),
        Map.entry("миколаїв",        "Mykolaiv"),
        Map.entry("вінниця",         "Vinnytsia"),
        Map.entry("полтава",         "Poltava"),
        Map.entry("херсон",          "Kherson"),
        Map.entry("черкаси",         "Cherkasy"),
        Map.entry("суми",            "Sumy"),
        Map.entry("луцьк",           "Lutsk"),
        Map.entry("рівне",           "Rivne"),
        Map.entry("ужгород",         "Uzhhorod"),
        Map.entry("чернівці",        "Chernivtsi"),
        Map.entry("чернігів",        "Chernihiv"),
        Map.entry("тернопіль",       "Ternopil"),
        Map.entry("хмельницький",    "Khmelnytskyi"),
        Map.entry("івано-франківськ","Ivano-Frankivsk"),
        Map.entry("житомир",         "Zhytomyr"),
        Map.entry("кропивницький",   "Kropyvnytskyi"),

        Map.entry("kyiv",            "Kyiv"),
        Map.entry("kiev",            "Kyiv"),
        Map.entry("kharkiv",         "Kharkiv"),
        Map.entry("odesa",           "Odesa"),
        Map.entry("odessa",          "Odesa"),
        Map.entry("dnipro",          "Dnipro"),
        Map.entry("lviv",            "Lviv"),
        Map.entry("zaporizhzhia",    "Zaporizhzhia"),

        Map.entry("лондон",          "London"),
        Map.entry("париж",           "Paris"),
        Map.entry("берлін",          "Berlin"),
        Map.entry("варшава",         "Warsaw"),
        Map.entry("прага",           "Prague"),
        Map.entry("відень",          "Vienna"),
        Map.entry("рим",             "Rome"),
        Map.entry("мадрид",          "Madrid"),
        Map.entry("амстердам",       "Amsterdam"),
        Map.entry("стокгольм",       "Stockholm"),
        Map.entry("нью-йорк",        "New York"),
        Map.entry("токіо",           "Tokyo"),
        Map.entry("пекін",           "Beijing")
    );

    public Optional<String> resolve(String word) {
        return Optional.ofNullable(CITIES.get(word.toLowerCase()));
    }
}