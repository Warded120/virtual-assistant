package com.ivan.bot.builder;

import com.ivan.bot.dictionary.CurrencyDictionary;
import com.ivan.bot.dto.request.CurrencyBotRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component("currencyRequestBuilder")
public class CurrencyRequestBuilder implements RequestBuilder {

    private final CurrencyDictionary currencyDictionary;

    @Override
    public CurrencyBotRequest buildRequest(String[] tokensLower) {
        List<String> found = new ArrayList<>();

        for (String token : tokensLower) {
            currencyDictionary.resolve(token).ifPresent(code -> {
                if (!found.contains(code)) found.add(code);
            });
            if (found.size() == 2) break;
        }

        return switch (found.size()) {
            case 2  -> new CurrencyBotRequest(found.get(0), found.get(1));
            case 1  -> new CurrencyBotRequest(found.get(0), "UAH");
            default -> new CurrencyBotRequest("USD", "UAH");
        };
    }
}
