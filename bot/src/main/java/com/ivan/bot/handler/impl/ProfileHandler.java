package com.ivan.bot.handler.impl;

import com.ivan.bot.entity.UserProfile;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ProfileHandler implements CommandHandler {

    private final UserProfileService userProfileService;

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        return userProfileService.getProfile(chatId)
                .map(profile -> buildProfileMessage(chatId, profile))
                .orElseGet(() -> SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("You don't have a profile yet. Use /createProfile to create one.")
                        .build());
    }

    private SendMessage buildProfileMessage(Long chatId, UserProfile profile) {
        Language language = profile.getLanguage();

        String message = language == Language.UKRAINIAN
                ? String.format("👤 *Ваш профіль*\n\n" +
                        "📝 Ім'я: %s\n" +
                        "🌐 Мова: Українська\n" +
                        "🏙️ Улюблене місто: %s\n" +
                        "💱 Базова валюта: %s\n" +
                        "💱 Цільова валюта: %s\n\n" +
                        "Використовуйте /updateProfile для зміни налаштувань.",
                        profile.getDisplayName(),
                        profile.getFavouriteCity(),
                        profile.getBaseCurrency(),
                        profile.getTargetCurrency())
                : String.format("👤 *Your Profile*\n\n" +
                        "📝 Name: %s\n" +
                        "🌐 Language: English\n" +
                        "🏙️ Favourite City: %s\n" +
                        "💱 Base Currency: %s\n" +
                        "💱 Target Currency: %s\n\n" +
                        "Use /updateProfile to modify your settings.",
                        profile.getDisplayName(),
                        profile.getFavouriteCity(),
                        profile.getBaseCurrency(),
                        profile.getTargetCurrency());

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .parseMode("Markdown")
                .build();
    }
}

