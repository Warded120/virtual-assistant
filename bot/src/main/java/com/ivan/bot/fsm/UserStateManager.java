package com.ivan.bot.fsm;

import com.ivan.bot.enumeration.UserState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Finite State Machine Manager for tracking user conversation states.
 * Uses in-memory storage with ConcurrentHashMap for thread safety.
 */
@Component
public class UserStateManager {

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> temporaryData = new ConcurrentHashMap<>();

    /**
     * Get the current state for a user. Returns IDLE if no state is set.
     */
    public UserState getState(Long chatId) {
        return userStates.getOrDefault(chatId, UserState.IDLE);
    }

    /**
     * Set the state for a user.
     */
    public void setState(Long chatId, UserState state) {
        if (state == UserState.IDLE) {
            userStates.remove(chatId);
        } else {
            userStates.put(chatId, state);
        }
    }

    /**
     * Reset user state to IDLE and clear temporary data.
     */
    public void resetState(Long chatId) {
        userStates.remove(chatId);
        temporaryData.remove(chatId);
    }

    /**
     * Check if user is in any active flow (not IDLE).
     */
    public boolean isInActiveFlow(Long chatId) {
        return getState(chatId) != UserState.IDLE;
    }

    /**
     * Store temporary data during a multi-step flow.
     */
    public void setTemporaryData(Long chatId, String key, String value) {
        temporaryData.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    /**
     * Get temporary data for a user.
     */
    public String getTemporaryData(Long chatId, String key) {
        Map<String, String> userData = temporaryData.get(chatId);
        return userData != null ? userData.get(key) : null;
    }

    /**
     * Get all temporary data for a user.
     */
    public Map<String, String> getAllTemporaryData(Long chatId) {
        return temporaryData.getOrDefault(chatId, Map.of());
    }

    /**
     * Clear temporary data for a user.
     */
    public void clearTemporaryData(Long chatId) {
        temporaryData.remove(chatId);
    }

    /**
     * Check if user is in create profile flow.
     */
    public boolean isInCreateProfileFlow(Long chatId) {
        UserState state = getState(chatId);
        return state.name().startsWith("CREATE_PROFILE_");
    }

    /**
     * Check if user is in update profile flow.
     */
    public boolean isInUpdateProfileFlow(Long chatId) {
        UserState state = getState(chatId);
        return state.name().startsWith("UPDATE_PROFILE_");
    }
}

