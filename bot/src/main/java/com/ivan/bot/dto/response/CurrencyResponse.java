package com.ivan.bot.dto.response;

import com.ivan.bot.enumeration.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponse implements BotResponse {
    private String base;
    private String target;
    private Double rate;
    private Language language;

    @Override
    public String getMessage() {
        return getMessage(language != null ? language : Language.ENGLISH);
    }

    @Override
    public String getMessage(Language lang) {
        if (lang == Language.UKRAINIAN) {
            return String.format("💱 Курс обміну %s до %s: %.4f", base, target, rate);
        }
        return String.format("💱 Exchange rate from %s to %s: %.4f", base, target, rate);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalCurrencyResponse {
        private String base;
        private String date;
        private Map<String, Double> rates;
    }
}
