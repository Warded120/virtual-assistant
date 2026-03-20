package com.ivan.bot.handler.impl;

import com.ivan.bot.entity.UserProfile;
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
public class CreateProfileHandler implements CommandHandler {

    private final UserStateManager stateManager;
    private final UserProfileService userProfileService;

    private static final String TEMP_NAME = "name";
    private static final String TEMP_LANGUAGE = "language";
    private static final String TEMP_CITY = "city";
    private static final String TEMP_BASE_CURRENCY = "baseCurrency";

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        String username = update.getMessage().getFrom().getUserName();

        // Check if profile already exists
        if (userProfileService.profileExists(chatId) && !stateManager.isInCreateProfileFlow(chatId)) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("You already have a profile. Use /updateProfile to modify it or /profile to view it.")
                    .build();
        }

        UserState currentState = stateManager.getState(chatId);

        return switch (currentState) {
            case IDLE -> startCreateProfile(chatId);
            case CREATE_PROFILE_AWAITING_NAME -> handleNameInput(chatId, text);
            case CREATE_PROFILE_AWAITING_LANGUAGE -> handleLanguageInput(chatId, text);
            case CREATE_PROFILE_AWAITING_CITY -> handleCityInput(chatId, text);
            case CREATE_PROFILE_AWAITING_BASE_CURRENCY -> handleBaseCurrencyInput(chatId, text);
            case CREATE_PROFILE_AWAITING_TARGET_CURRENCY -> handleTargetCurrencyInput(chatId, text, username);
            default -> {
                stateManager.resetState(chatId);
                yield startCreateProfile(chatId);
            }
        };
    }

    private SendMessage startCreateProfile(Long chatId) {
        stateManager.setState(chatId, UserState.CREATE_PROFILE_AWAITING_NAME);
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Let's create your profile! 📝\n\nPlease enter your display name:")
                .build();
    }

    private SendMessage handleNameInput(Long chatId, String name) {
        if (name.isBlank() || name.length() > 100) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Please enter a valid name (1-100 characters):")
                    .build();
        }

        stateManager.setTemporaryData(chatId, TEMP_NAME, name);
        stateManager.setState(chatId, UserState.CREATE_PROFILE_AWAITING_LANGUAGE);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Great! Now choose your preferred language:\n\n" +
                      "• Type 'english' or 'en' for English\n" +
                      "• Type 'ukrainian' or 'uk' for Ukrainian (Українська)")
                .build();
    }

    private SendMessage handleLanguageInput(Long chatId, String languageInput) {
        Language language = Language.fromString(languageInput);
        stateManager.setTemporaryData(chatId, TEMP_LANGUAGE, language.name());
        stateManager.setState(chatId, UserState.CREATE_PROFILE_AWAITING_CITY);

        String message = language == Language.UKRAINIAN
                ? "Чудово! Тепер введіть ваше улюблене місто для прогнозу погоди:"
                : "Great! Now enter your favourite city for weather forecasts:";

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
    }

    private SendMessage handleCityInput(Long chatId, String city) {
        if (city.isBlank() || city.length() > 100) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Please enter a valid city name (1-100 characters):")
                    .build();
        }

        stateManager.setTemporaryData(chatId, TEMP_CITY, city);
        stateManager.setState(chatId, UserState.CREATE_PROFILE_AWAITING_BASE_CURRENCY);

        Language language = Language.valueOf(stateManager.getTemporaryData(chatId, TEMP_LANGUAGE));
        String message = language == Language.UKRAINIAN
                ? "Введіть вашу базову валюту (наприклад, USD, EUR, UAH):"
                : "Enter your base currency (e.g., USD, EUR, UAH):";

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
    }

    private SendMessage handleBaseCurrencyInput(Long chatId, String baseCurrency) {
        String currency = baseCurrency.toUpperCase().trim();
        if (currency.length() != 3) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Please enter a valid 3-letter currency code (e.g., USD, EUR, UAH):")
                    .build();
        }

        stateManager.setTemporaryData(chatId, TEMP_BASE_CURRENCY, currency);
        stateManager.setState(chatId, UserState.CREATE_PROFILE_AWAITING_TARGET_CURRENCY);

        Language language = Language.valueOf(stateManager.getTemporaryData(chatId, TEMP_LANGUAGE));
        String message = language == Language.UKRAINIAN
                ? "Введіть вашу цільову валюту (наприклад, USD, EUR, UAH):"
                : "Enter your target currency (e.g., USD, EUR, UAH):";

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
    }

    private SendMessage handleTargetCurrencyInput(Long chatId, String targetCurrency, String username) {
        String currency = targetCurrency.toUpperCase().trim();
        if (currency.length() != 3) {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Please enter a valid 3-letter currency code (e.g., USD, EUR, UAH):")
                    .build();
        }

        // Retrieve all temporary data and create profile
        String name = stateManager.getTemporaryData(chatId, TEMP_NAME);
        Language language = Language.valueOf(stateManager.getTemporaryData(chatId, TEMP_LANGUAGE));
        String city = stateManager.getTemporaryData(chatId, TEMP_CITY);
        String baseCurrency = stateManager.getTemporaryData(chatId, TEMP_BASE_CURRENCY);

        try {
            UserProfile profile = userProfileService.createProfile(
                    chatId, username, name, language, city, baseCurrency, currency
            );

            stateManager.resetState(chatId);

            String successMessage = language == Language.UKRAINIAN
                    ? String.format("✅ Профіль успішно створено!\n\n" +
                            "👤 Ім'я: %s\n" +
                            "🌐 Мова: Українська\n" +
                            "🏙️ Улюблене місто: %s\n" +
                            "💱 Базова валюта: %s\n" +
                            "💱 Цільова валюта: %s\n\n" +
                            "Використовуйте /updateProfile для зміни налаштувань.",
                            profile.getDisplayName(), profile.getFavouriteCity(),
                            profile.getBaseCurrency(), profile.getTargetCurrency())
                    : String.format("✅ Profile created successfully!\n\n" +
                            "👤 Name: %s\n" +
                            "🌐 Language: English\n" +
                            "🏙️ Favourite City: %s\n" +
                            "💱 Base Currency: %s\n" +
                            "💱 Target Currency: %s\n\n" +
                            "Use /updateProfile to modify your settings.",
                            profile.getDisplayName(), profile.getFavouriteCity(),
                            profile.getBaseCurrency(), profile.getTargetCurrency());

            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(successMessage)
                    .build();

        } catch (Exception e) {
            log.error("Error creating profile for chatId: {}", chatId, e);
            stateManager.resetState(chatId);
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("❌ Error creating profile. Please try again with /createProfile")
                    .build();
        }
    }
}

