package com.ivan.bot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextAnalysisResult {
    private String intent;
    private String location;
    private String currency;
    private double confidence;
}

