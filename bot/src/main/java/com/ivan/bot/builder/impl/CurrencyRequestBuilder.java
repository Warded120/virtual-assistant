package com.ivan.bot.builder.impl;

import com.ivan.bot.builder.RequestBuilder;
import com.ivan.bot.dictionary.CurrencyDictionary;
import com.ivan.bot.dto.request.CurrencyBotRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

import static com.ivan.bot.constant.Constants.DEFAULT_CURRENCY;
import static com.ivan.bot.constant.Constants.DEFAULT_CURRENCY_TO;

@RequiredArgsConstructor
@Component("currencyRequestBuilder")
public class CurrencyRequestBuilder implements RequestBuilder {

    private final CurrencyDictionary currencyDictionary;

    @Override
    public CurrencyBotRequest buildRequest(String[] tokensLower) {
        List<String> found = new ArrayList<>();

        for (String token : tokensLower) {
            var code = currencyDictionary.resolve(token);
            if (code != null && !found.contains(code)) {
                found.add(code);
            }
            if (found.size() == 2) break;
        }

        return switch (found.size()) {
            case 2  -> new CurrencyBotRequest(found.get(0), found.get(1));
            case 1  -> new CurrencyBotRequest(found.get(0), DEFAULT_CURRENCY_TO);
            default -> new CurrencyBotRequest(DEFAULT_CURRENCY, DEFAULT_CURRENCY_TO);
        };
    }
}
