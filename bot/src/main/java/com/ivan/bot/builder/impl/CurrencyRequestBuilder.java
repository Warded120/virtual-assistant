package com.ivan.bot.builder.impl;

import com.ivan.bot.builder.RequestBuilder;
import com.ivan.bot.dictionary.CurrencyDictionary;
import com.ivan.bot.dto.request.CurrencyBotRequest;
import com.ivan.bot.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component("currencyRequestBuilder")
public class CurrencyRequestBuilder implements RequestBuilder {

    private final CurrencyDictionary currencyDictionary;
    private final UserProfileService userProfileService;

    @Override
    public CurrencyBotRequest buildRequest(String[] tokensLower, Long chatId) {
        // Get user's preferred currencies as defaults
        String defaultBase = userProfileService.getBaseCurrency(chatId);
        String defaultTarget = userProfileService.getTargetCurrency(chatId);

        List<String> found = new ArrayList<>();

        for (String token : tokensLower) {
            var code = currencyDictionary.resolve(token);
            if (code != null && !found.contains(code)) {
                found.add(code);
            }
            if (found.size() == 2) break;
        }

        return switch (found.size()) {
            case 2  -> new CurrencyBotRequest(found.get(0), found.get(1), chatId);
            case 1  -> new CurrencyBotRequest(found.get(0), defaultTarget, chatId);
            default -> new CurrencyBotRequest(defaultBase, defaultTarget, chatId);
        };
    }
}
