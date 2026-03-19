package com.ivan.bot.dto.response;

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

    @Override
    public String getMessage() {
        return String.format("Exchange rate from %s to %s: %s", base, target, rate);
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
