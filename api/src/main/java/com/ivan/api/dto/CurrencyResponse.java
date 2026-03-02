package com.ivan.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponse {
    
    private Double result;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalCurrencyResponse {
        private String base;
        private String date;
        private Map<String, Double> rates;
    }
}
