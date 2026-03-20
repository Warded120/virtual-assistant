package com.ivan.bot.dto.response;

import com.ivan.bot.entity.UserProfile;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.enumeration.UpdateIntent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileActionResponse implements BotResponse {

    private UpdateIntent action;
    private Language language;
    private UserProfile profile;
    private boolean profileExists;
    private boolean actionStarted;

    @Override
    public String getMessage() {
        return getMessage(language != null ? language : Language.ENGLISH);
    }

    @Override
    public String getMessage(Language lang) {
        return switch (action) {
            case CREATE_PROFILE -> getCreateProfileMessage(lang);
            case UPDATE_PROFILE -> getUpdateProfileMessage(lang);
            case VIEW_PROFILE -> getViewProfileMessage(lang);
            default -> lang == Language.UKRAINIAN
                    ? "Невідома дія з профілем."
                    : "Unknown profile action.";
        };
    }

    private String getCreateProfileMessage(Language lang) {
        if (profileExists) {
            return lang == Language.UKRAINIAN
                    ? "У вас вже є профіль. Використовуйте 'оновити профіль' для зміни налаштувань або 'показати профіль' для перегляду."
                    : "You already have a profile. Use 'update profile' to modify it or 'show profile' to view it.";
        }
        if (actionStarted) {
            return lang == Language.UKRAINIAN
                    ? "Давайте створимо ваш профіль! 📝\n\nБудь ласка, введіть ваше ім'я:"
                    : "Let's create your profile! 📝\n\nPlease enter your display name:";
        }
        return lang == Language.UKRAINIAN
                ? "Готовий створити ваш профіль."
                : "Ready to create your profile.";
    }

    private String getUpdateProfileMessage(Language lang) {
        if (!profileExists) {
            return lang == Language.UKRAINIAN
                    ? "У вас ще немає профілю. Скажіть 'створити профіль' щоб створити його."
                    : "You don't have a profile yet. Say 'create profile' to create one first.";
        }
        if (actionStarted) {
            return lang == Language.UKRAINIAN
                    ? "Що ви хочете оновити? Введіть номер:\n\n" +
                      "1. Ім'я\n" +
                      "2. Мова\n" +
                      "3. Улюблене місто\n" +
                      "4. Базова валюта\n" +
                      "5. Цільова валюта\n" +
                      "6. Скасувати"
                    : "What would you like to update? Enter a number:\n\n" +
                      "1. Name\n" +
                      "2. Language\n" +
                      "3. Favourite City\n" +
                      "4. Base Currency\n" +
                      "5. Target Currency\n" +
                      "6. Cancel";
        }
        return lang == Language.UKRAINIAN
                ? "Готовий оновити ваш профіль."
                : "Ready to update your profile.";
    }

    private String getViewProfileMessage(Language lang) {
        if (!profileExists || profile == null) {
            return lang == Language.UKRAINIAN
                    ? "У вас ще немає профілю. Скажіть 'створити профіль' щоб створити його."
                    : "You don't have a profile yet. Say 'create profile' to create one.";
        }

        return lang == Language.UKRAINIAN
                ? String.format("👤 Ваш профіль\n\n" +
                        "📝 Ім'я: %s\n" +
                        "🌐 Мова: %s\n" +
                        "🏙️ Улюблене місто: %s\n" +
                        "💱 Базова валюта: %s\n" +
                        "💱 Цільова валюта: %s\n\n" +
                        "Скажіть 'оновити профіль' для зміни налаштувань.",
                        profile.getDisplayName(),
                        profile.getLanguage() == Language.UKRAINIAN ? "Українська" : "English",
                        profile.getFavouriteCity(),
                        profile.getBaseCurrency(),
                        profile.getTargetCurrency())
                : String.format("👤 Your Profile\n\n" +
                        "📝 Name: %s\n" +
                        "🌐 Language: %s\n" +
                        "🏙️ Favourite City: %s\n" +
                        "💱 Base Currency: %s\n" +
                        "💱 Target Currency: %s\n\n" +
                        "Say 'update profile' to modify your settings.",
                        profile.getDisplayName(),
                        profile.getLanguage() == Language.UKRAINIAN ? "Ukrainian" : "English",
                        profile.getFavouriteCity(),
                        profile.getBaseCurrency(),
                        profile.getTargetCurrency());
    }
}

