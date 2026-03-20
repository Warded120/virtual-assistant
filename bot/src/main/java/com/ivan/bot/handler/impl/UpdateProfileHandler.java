package com.ivan.bot.handler.impl;

import com.ivan.bot.enumeration.Language;
import com.ivan.bot.enumeration.UserState;
import com.ivan.bot.fsm.UserStateManager;
import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProfileHandler implements CommandHandler {

    private final UserStateManager stateManager;
    private final UserProfileService userProfileService;

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        if (!userProfileService.profileExists(chatId) && !stateManager.isInUpdateProfileFlow(chatId)) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("You don't have a profile yet. Use /createProfile to create one first.")
                    .build();
        }

        UserState currentState = stateManager.getState(chatId);

        return switch (currentState) {
            case IDLE -> startUpdateProfile(chatId);
            case UPDATE_PROFILE_AWAITING_FIELD -> handleFieldSelection(chatId, text);
            case UPDATE_PROFILE_AWAITING_NAME -> handleNameUpdate(chatId, text);
            case UPDATE_PROFILE_AWAITING_LANGUAGE -> handleLanguageUpdate(chatId, text);
            case UPDATE_PROFILE_AWAITING_CITY -> handleCityUpdate(chatId, text);
            case UPDATE_PROFILE_AWAITING_BASE_CURRENCY -> handleBaseCurrencyUpdate(chatId, text);
            case UPDATE_PROFILE_AWAITING_TARGET_CURRENCY -> handleTargetCurrencyUpdate(chatId, text);
            default -> {
                stateManager.resetState(chatId);
                yield startUpdateProfile(chatId);
            }
        };
    }

    private SendMessage startUpdateProfile(Long chatId) {
        stateManager.setState(chatId, UserState.UPDATE_PROFILE_AWAITING_FIELD);
        Language language = userProfileService.getLanguage(chatId);
        String message = language == Language.UKRAINIAN
                ? "Що ви хочете оновити? Введіть номер:\n\n1. Ім'я\n2. Мова\n3. Улюблене місто\n4. Базова валюта\n5. Цільова валюта\n6. Скасувати"
                : "What would you like to update? Enter a number:\n\n1. Name\n2. Language\n3. Favourite City\n4. Base Currency\n5. Target Currency\n6. Cancel";
        return SendMessage.builder().chatId(chatId.toString()).text(message).build();
    }

    private SendMessage handleFieldSelection(Long chatId, String selection) {
        Language language = userProfileService.getLanguage(chatId);
        return switch (selection) {
            case "1" -> { stateManager.setState(chatId, UserState.UPDATE_PROFILE_AWAITING_NAME); yield SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть нове ім'я:" : "Enter your new name:").build(); }
            case "2" -> { stateManager.setState(chatId, UserState.UPDATE_PROFILE_AWAITING_LANGUAGE); yield SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Виберіть мову:\n• 'english' або 'en'\n• 'ukrainian' або 'uk'" : "Choose language:\n• 'english' or 'en'\n• 'ukrainian' or 'uk'").build(); }
            case "3" -> { stateManager.setState(chatId, UserState.UPDATE_PROFILE_AWAITING_CITY); yield SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть нове улюблене місто:" : "Enter your new favourite city:").build(); }
            case "4" -> { stateManager.setState(chatId, UserState.UPDATE_PROFILE_AWAITING_BASE_CURRENCY); yield SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть нову базову валюту:" : "Enter your new base currency:").build(); }
            case "5" -> { stateManager.setState(chatId, UserState.UPDATE_PROFILE_AWAITING_TARGET_CURRENCY); yield SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть нову цільову валюту:" : "Enter your new target currency:").build(); }
            case "6" -> { stateManager.resetState(chatId); yield SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Оновлення скасовано." : "Update cancelled.").build(); }
            default -> SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть число від 1 до 6:" : "Enter a number from 1 to 6:").build();
        };
    }

    private SendMessage handleNameUpdate(Long chatId, String name) {
        Language language = userProfileService.getLanguage(chatId);
        if (name.isBlank() || name.length() > 100) {
            return SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть коректне ім'я:" : "Enter a valid name:").build();
        }
        userProfileService.updateDisplayName(chatId, name);
        stateManager.resetState(chatId);
        return SendMessage.builder().chatId(chatId.toString()).text((language == Language.UKRAINIAN ? "✅ Ім'я оновлено: " : "✅ Name updated to: ") + name).build();
    }

    private SendMessage handleLanguageUpdate(Long chatId, String languageInput) {
        Language newLanguage = Language.fromString(languageInput);
        userProfileService.updateLanguage(chatId, newLanguage);
        stateManager.resetState(chatId);
        return SendMessage.builder().chatId(chatId.toString()).text(newLanguage == Language.UKRAINIAN ? "✅ Мову оновлено: Українська" : "✅ Language updated to: English").build();
    }

    private SendMessage handleCityUpdate(Long chatId, String city) {
        Language language = userProfileService.getLanguage(chatId);
        if (city.isBlank() || city.length() > 100) {
            return SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть коректну назву міста:" : "Enter a valid city name:").build();
        }
        userProfileService.updateFavouriteCity(chatId, city);
        stateManager.resetState(chatId);
        return SendMessage.builder().chatId(chatId.toString()).text((language == Language.UKRAINIAN ? "✅ Місто оновлено: " : "✅ City updated to: ") + city).build();
    }

    private SendMessage handleBaseCurrencyUpdate(Long chatId, String currency) {
        Language language = userProfileService.getLanguage(chatId);
        String code = currency.toUpperCase().trim();
        if (code.length() != 3) {
            return SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть 3-літерний код валюти:" : "Enter a valid 3-letter currency code:").build();
        }
        userProfileService.updateBaseCurrency(chatId, code);
        stateManager.resetState(chatId);
        return SendMessage.builder().chatId(chatId.toString()).text((language == Language.UKRAINIAN ? "✅ Базову валюту оновлено: " : "✅ Base currency updated to: ") + code).build();
    }

    private SendMessage handleTargetCurrencyUpdate(Long chatId, String currency) {
        Language language = userProfileService.getLanguage(chatId);
        String code = currency.toUpperCase().trim();
        if (code.length() != 3) {
            return SendMessage.builder().chatId(chatId.toString()).text(language == Language.UKRAINIAN ? "Введіть 3-літерний код валюти:" : "Enter a valid 3-letter currency code:").build();
        }
        userProfileService.updateTargetCurrency(chatId, code);
        stateManager.resetState(chatId);
        return SendMessage.builder().chatId(chatId.toString()).text((language == Language.UKRAINIAN ? "✅ Цільову валюту оновлено: " : "✅ Target currency updated to: ") + code).build();
    }
}

