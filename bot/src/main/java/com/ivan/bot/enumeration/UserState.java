package com.ivan.bot.enumeration;

/**
 * Represents the different states a user can be in during conversation.
 * Used by the FSM (Finite State Machine) to track user progress through multi-step flows.
 */
public enum UserState {
    // Default state - no active flow
    IDLE,

    // Create profile flow states
    CREATE_PROFILE_AWAITING_NAME,
    CREATE_PROFILE_AWAITING_LANGUAGE,
    CREATE_PROFILE_AWAITING_CITY,
    CREATE_PROFILE_AWAITING_BASE_CURRENCY,
    CREATE_PROFILE_AWAITING_TARGET_CURRENCY,

    // Update profile flow states
    UPDATE_PROFILE_AWAITING_FIELD,
    UPDATE_PROFILE_AWAITING_NAME,
    UPDATE_PROFILE_AWAITING_LANGUAGE,
    UPDATE_PROFILE_AWAITING_CITY,
    UPDATE_PROFILE_AWAITING_BASE_CURRENCY,
    UPDATE_PROFILE_AWAITING_TARGET_CURRENCY,

    // Create event flow states
    CREATE_EVENT_AWAITING_TITLE,
    CREATE_EVENT_AWAITING_START,
    CREATE_EVENT_AWAITING_END
}

