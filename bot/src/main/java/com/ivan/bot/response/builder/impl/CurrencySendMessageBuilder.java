package com.ivan.bot.response.builder.impl;

import com.ivan.bot.dto.CurrencyResponse;
import com.ivan.bot.response.builder.SendMessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class CurrencySendMessageBuilder implements SendMessageBuilder<CurrencyResponse> {
    @Override
    public String buildResponse(CurrencyResponse data) {
        return String.format(
                "%s %s -> %s = %s.",
                data.getAmount(),
                data.getBase(),
                data.getTarget(),
                data.getResult()
        );
    }
}
