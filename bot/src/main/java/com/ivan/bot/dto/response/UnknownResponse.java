package com.ivan.bot.dto.response;

import com.ivan.bot.enumeration.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnknownResponse implements BotResponse {

    private Language language;

    @Override
    public String getMessage() {
        return getMessage(language != null ? language : Language.ENGLISH);
    }

    @Override
    public String getMessage(Language lang) {
        return lang == Language.UKRAINIAN
                ? "Я не можу зрозуміти ваш запит. Спробуйте щось інше.\n\n" +
                  "Ви можете запитати про:\n" +
                  "• Погоду: 'яка погода в Києві?'\n" +
                  "• Курс валют: 'курс долара до гривні'\n" +
                  "• Профіль: 'покажи мій профіль', 'створи профіль', 'оновити профіль'"
                : "I cannot understand your request. Please try something else.\n\n" +
                  "You can ask about:\n" +
                  "• Weather: 'what's the weather in London?'\n" +
                  "• Currency: 'USD to EUR rate'\n" +
                  "• Profile: 'show my profile', 'create profile', 'update profile'";
    }
}
