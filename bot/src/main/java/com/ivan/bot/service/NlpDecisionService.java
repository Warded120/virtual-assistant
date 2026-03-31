package com.ivan.bot.service;

import com.ivan.bot.client.currency.CurrencyClient;
import com.ivan.bot.client.weather.WeatherClient;
import com.ivan.bot.dto.request.*;
import com.ivan.bot.dto.response.BotResponse;
import com.ivan.bot.dto.response.EventResponse;
import com.ivan.bot.dto.response.ProfileActionResponse;
import com.ivan.bot.dto.response.ReminderResponse;
import com.ivan.bot.dto.response.UnknownResponse;
import com.ivan.bot.entity.Reminder;
import com.ivan.bot.entity.UserProfile;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.enumeration.UpdateIntent;
import com.ivan.bot.enumeration.UserState;
import com.ivan.bot.fsm.UserStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NlpDecisionService {
    private final CurrencyClient currencyClient;
    private final WeatherClient weatherClient;
    private final UserProfileService userProfileService;
    private final UserStateManager stateManager;
    private final ReminderService reminderService;

    public BotResponse decideResponse(BotRequest botRequest) {
        return switch (botRequest) {
            case WeatherBotRequest weatherRequest -> handleWeatherRequest(weatherRequest);
            case CurrencyBotRequest currencyRequest -> handleCurrencyRequest(currencyRequest);
            case ProfileActionRequest profileRequest -> handleProfileAction(profileRequest);
            case ReminderRequest reminderRequest -> handleReminderRequest(reminderRequest);
            case EventRequest eventRequest -> handleEventRequest(eventRequest);
            default -> handleUnknownRequest((UnknownRequest) botRequest);
        };
    }

    private BotResponse handleWeatherRequest(WeatherBotRequest weatherRequest) {
        var response = weatherClient.getWeather(weatherRequest);
        response.setLanguage(userProfileService.getLanguage(weatherRequest.chatId()));
        return response;
    }

    private BotResponse handleCurrencyRequest(CurrencyBotRequest currencyRequest) {
        var response = currencyClient.getCurrencyRates(currencyRequest);
        response.setLanguage(userProfileService.getLanguage(currencyRequest.chatId()));
        return response;
    }

    private UnknownResponse handleUnknownRequest(UnknownRequest unknownRequest) {
        return UnknownResponse.builder()
                .language(resolveLanguage(unknownRequest.getChatId(), unknownRequest.getDetectedLanguage()))
                .build();
    }

    private BotResponse handleReminderRequest(ReminderRequest reminderRequest) {
        Language language = resolveLanguage(reminderRequest.getChatId(), reminderRequest.getDetectedLanguage());

        try {
            Reminder reminder = reminderService.createReminder(
                    reminderRequest.getChatId(),
                    reminderRequest.getReminderText(),
                    reminderRequest.getReminderDateTime()
            );

            log.info("Created reminder with id: {} for chatId: {}", reminder.getId(), reminder.getChatId());

            return ReminderResponse.builder()
                    .success(true)
                    .reminderText(reminder.getReminderText())
                    .reminderDateTime(reminder.getReminderDateTime())
                    .language(language)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create reminder for chatId: {}", reminderRequest.getChatId(), e);
            return ReminderResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .language(language)
                    .build();
        }
    }

    private BotResponse handleProfileAction(ProfileActionRequest request) {
        Long chatId = request.getChatId();
        UpdateIntent action = request.getAction();
        boolean profileExists = userProfileService.profileExists(chatId);

        // Determine language: use profile language if exists, otherwise use detected language
        Language language = resolveLanguage(chatId, request.getDetectedLanguage());

        Optional<UserProfile> profileOpt = userProfileService.getProfile(chatId);

        return switch (action) {
            case CREATE_PROFILE -> {
                if (profileExists) {
                    yield ProfileActionResponse.builder()
                            .action(action)
                            .language(language)
                            .profileExists(true)
                            .actionStarted(false)
                            .build();
                }
                // Start FSM for profile creation
                stateManager.setState(chatId, UserState.CREATE_PROFILE_AWAITING_NAME);
                yield ProfileActionResponse.builder()
                        .action(action)
                        .language(language)
                        .profileExists(false)
                        .actionStarted(true)
                        .build();
            }
            case UPDATE_PROFILE -> {
                if (!profileExists) {
                    yield ProfileActionResponse.builder()
                            .action(action)
                            .language(language)
                            .profileExists(false)
                            .actionStarted(false)
                            .build();
                }
                // Start FSM for profile update
                stateManager.setState(chatId, UserState.UPDATE_PROFILE_AWAITING_FIELD);
                yield ProfileActionResponse.builder()
                        .action(action)
                        .language(language)
                        .profileExists(true)
                        .actionStarted(true)
                        .build();
            }
            case VIEW_PROFILE -> ProfileActionResponse.builder()
                    .action(action)
                    .language(language)
                    .profileExists(profileExists)
                    .profile(profileOpt.orElse(null))
                    .actionStarted(false)
                    .build();
            default -> new UnknownResponse();
        };
    }

    private Language resolveLanguage(Long chatId, Language detectedLanguage) {
        if (chatId != null && userProfileService.profileExists(chatId)) {
            return userProfileService.getLanguage(chatId);
        }
        return detectedLanguage != null ? detectedLanguage : Language.ENGLISH;
    }

    private BotResponse handleEventRequest(EventRequest eventRequest) {
        Language language = resolveLanguage(eventRequest.getChatId(), eventRequest.getDetectedLanguage());

        // Start FSM for event creation
        stateManager.setState(eventRequest.getChatId(), UserState.CREATE_EVENT_AWAITING_TITLE);

        return EventResponse.builder()
                .flowStarted(true)
                .language(language)
                .build();
    }
}
