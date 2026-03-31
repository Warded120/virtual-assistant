package com.ivan.bot.service;

import com.ivan.bot.enumeration.Language;
import com.ivan.bot.enumeration.UpdateIntent;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class IntentDetectorService {

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

    private static final Set<String> CREATE_PROFILE_KW = Set.of(
            "create", "new", "register", "signup", "make",
            "створи", "створити", "новий", "реєстрація", "зареєструватися", "зареєструй"
    );

    private static final Set<String> UPDATE_PROFILE_KW = Set.of(
            "update", "change", "edit", "modify", "settings",
            "онови", "оновити", "зміни", "змінити", "редагувати", "налаштування", "редагуй"
    );

    private static final Set<String> VIEW_PROFILE_KW = Set.of(
            "view", "show", "display", "get", "see",
            "переглянути", "покажи", "показати", "подивитися", "глянь", "перегляд"
    );

    private static final Set<String> PROFILE_KW = Set.of(
            "profile", "account", "me", "my",
            "профіль", "акаунт", "мене", "мій", "мої", "мною"
    );

    private static final Set<String> REMINDER_KW = Set.of(
            "remind", "reminder", "reminders", "notification", "notify", "alert", "schedule",
            "нагадай", "нагадати", "нагадування", "нагадувати", "сповіщення", "сповісти", "сповістити"
    );

    private static final Set<String> EVENT_KW = Set.of(
            "event", "events", "calendar", "meeting", "appointment", "schedule",
            "подія", "події", "подію", "календар", "зустріч", "нарада", "запланувати"
    );

    private static final Set<String> CREATE_EVENT_KW = Set.of(
            "create", "make", "add", "new", "schedule", "plan",
            "створи", "створити", "додай", "додати", "запланувати", "заплануй"
    );

    public UpdateIntent detect(String[] tokens) {
        int weatherScore = 0;
        int currencyScore = 0;
        int createScore = 0;
        int updateScore = 0;
        int viewScore = 0;
        int profileScore = 0;
        int reminderScore = 0;
        int eventScore = 0;
        int createEventScore = 0;

        for (String token : tokens) {
            if (token.length() > 3) {
                if (WEATHER_KW.contains(token) || WEATHER_KW.stream().anyMatch(token::contains))
                    weatherScore++;
                if (CURRENCY_KW.contains(token) || CURRENCY_KW.stream().anyMatch(token::contains))
                    currencyScore++;
                if (CREATE_PROFILE_KW.contains(token) || CREATE_PROFILE_KW.stream().anyMatch(token::contains))
                    createScore++;
                if (UPDATE_PROFILE_KW.contains(token) || UPDATE_PROFILE_KW.stream().anyMatch(token::contains))
                    updateScore++;
                if (VIEW_PROFILE_KW.contains(token) || VIEW_PROFILE_KW.stream().anyMatch(token::contains))
                    viewScore++;
                if (PROFILE_KW.contains(token) || PROFILE_KW.stream().anyMatch(token::contains))
                    profileScore++;
                if (REMINDER_KW.contains(token) || REMINDER_KW.stream().anyMatch(token::contains))
                    reminderScore++;
                if (EVENT_KW.contains(token) || EVENT_KW.stream().anyMatch(token::contains))
                    eventScore++;
                if (CREATE_EVENT_KW.contains(token) || CREATE_EVENT_KW.stream().anyMatch(token::contains))
                    createEventScore++;
            }
        }

        if (reminderScore > 0) return UpdateIntent.REMINDER;

        // Check for event intent - higher priority than profile actions
        if (eventScore > 0 && createEventScore > 0) return UpdateIntent.EVENT;
        if (eventScore > 0) return UpdateIntent.EVENT;

        boolean hasProfileContext = profileScore > 0;

        if (hasProfileContext) {
            if (createScore > 0) return UpdateIntent.CREATE_PROFILE;
            if (updateScore > 0) return UpdateIntent.UPDATE_PROFILE;
            if (viewScore > 0) return UpdateIntent.VIEW_PROFILE;
        }

        if (createScore > 0 && profileScore > 0) return UpdateIntent.CREATE_PROFILE;
        if (updateScore > 0 && profileScore > 0) return UpdateIntent.UPDATE_PROFILE;
        if (viewScore > 0 && profileScore > 0) return UpdateIntent.VIEW_PROFILE;

        if (weatherScore == 0 && currencyScore == 0) return UpdateIntent.UNKNOWN;
        if (weatherScore > currencyScore) return UpdateIntent.WEATHER;
        if (currencyScore > weatherScore) return UpdateIntent.CURRENCY;

        return UpdateIntent.UNKNOWN;
    }

    public Language detectLanguage(String[] tokens) {
        for (String token : tokens) {
            for (char c : token.toCharArray()) {
                if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                    return Language.UKRAINIAN;
                }
            }
        }
        return Language.ENGLISH;
    }
}