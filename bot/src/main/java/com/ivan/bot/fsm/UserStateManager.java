package com.ivan.bot.fsm;

import com.ivan.bot.enumeration.UserState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStateManager {

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> temporaryData = new ConcurrentHashMap<>();

    public UserState getState(Long chatId) {
        return userStates.getOrDefault(chatId, UserState.IDLE);
    }

    public void setState(Long chatId, UserState state) {
        if (state == UserState.IDLE) {
            userStates.remove(chatId);
        } else {
            userStates.put(chatId, state);
        }
    }

    public void resetState(Long chatId) {
        userStates.remove(chatId);
        temporaryData.remove(chatId);
    }

    public boolean isInActiveFlow(Long chatId) {
        return getState(chatId) != UserState.IDLE;
    }

    public void setTemporaryData(Long chatId, String key, String value) {
        temporaryData.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    public String getTemporaryData(Long chatId, String key) {
        Map<String, String> userData = temporaryData.get(chatId);
        return userData != null ? userData.get(key) : null;
    }

    public Map<String, String> getAllTemporaryData(Long chatId) {
        return temporaryData.getOrDefault(chatId, Map.of());
    }

    public void clearTemporaryData(Long chatId) {
        temporaryData.remove(chatId);
    }

    public boolean isInCreateProfileFlow(Long chatId) {
        UserState state = getState(chatId);
        return state.name().startsWith("CREATE_PROFILE_");
    }

    public boolean isInUpdateProfileFlow(Long chatId) {
        UserState state = getState(chatId);
        return state.name().startsWith("UPDATE_PROFILE_");
    }

    public boolean isInCreateEventFlow(Long chatId) {
        UserState state = getState(chatId);
        return state.name().startsWith("CREATE_EVENT_");
    }
}

