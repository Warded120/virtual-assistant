package com.ivan.bot.service;

import com.ivan.bot.entity.UserProfile;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.ivan.bot.constant.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public Optional<UserProfile> getProfile(Long chatId) {
        return userProfileRepository.findByChatId(chatId);
    }

    public boolean profileExists(Long chatId) {
        return userProfileRepository.existsByChatId(chatId);
    }

    @Transactional
    public UserProfile createProfile(Long chatId, String telegramUsername, String displayName,
                                     Language language, String favouriteCity,
                                     String baseCurrency, String targetCurrency) {
        if (profileExists(chatId)) {
            throw new IllegalStateException("Profile already exists for this user");
        }

        UserProfile profile = UserProfile.builder()
                .chatId(chatId)
                .telegramUsername(telegramUsername)
                .displayName(displayName)
                .language(language != null ? language : Language.ENGLISH)
                .favouriteCity(favouriteCity != null ? favouriteCity : DEFAULT_CITY)
                .baseCurrency(baseCurrency != null ? baseCurrency : DEFAULT_CURRENCY)
                .targetCurrency(targetCurrency != null ? targetCurrency : DEFAULT_CURRENCY_TO)
                .build();

        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateDisplayName(Long chatId, String displayName) {
        UserProfile profile = getProfileOrThrow(chatId);
        profile.setDisplayName(displayName);
        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateLanguage(Long chatId, Language language) {
        UserProfile profile = getProfileOrThrow(chatId);
        profile.setLanguage(language);
        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateFavouriteCity(Long chatId, String city) {
        UserProfile profile = getProfileOrThrow(chatId);
        profile.setFavouriteCity(city);
        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateBaseCurrency(Long chatId, String baseCurrency) {
        UserProfile profile = getProfileOrThrow(chatId);
        profile.setBaseCurrency(baseCurrency);
        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateTargetCurrency(Long chatId, String targetCurrency) {
        UserProfile profile = getProfileOrThrow(chatId);
        profile.setTargetCurrency(targetCurrency);
        return userProfileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(Long chatId) {
        userProfileRepository.deleteById(chatId);
    }

    public String getFavouriteCity(Long chatId) {
        return getProfile(chatId).map(UserProfile::getFavouriteCity).orElse(DEFAULT_CITY);
    }

    public String getBaseCurrency(Long chatId) {
        return getProfile(chatId).map(UserProfile::getBaseCurrency).orElse(DEFAULT_CURRENCY);
    }

    public String getTargetCurrency(Long chatId) {
        return getProfile(chatId).map(UserProfile::getTargetCurrency).orElse(DEFAULT_CURRENCY_TO);
    }

    public Language getLanguage(Long chatId) {
        return getProfile(chatId).map(UserProfile::getLanguage).orElse(Language.ENGLISH);
    }

    private UserProfile getProfileOrThrow(Long chatId) {
        return getProfile(chatId)
                .orElseThrow(() -> new IllegalStateException("Profile not found for chatId: " + chatId));
    }
}

