package com.ivan.bot.enumeration;

public enum Language {
    ENGLISH("en"),
    UKRAINIAN("uk");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return ENGLISH;
    }

    public static Language fromString(String text) {
        if (text == null) return ENGLISH;
        String lower = text.toLowerCase().trim();
        if (lower.contains("ukr") || lower.contains("укр") || lower.equals("uk")) {
            return UKRAINIAN;
        }
        return ENGLISH;
    }
}

