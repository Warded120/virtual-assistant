package com.ivan.bot.dto.request;

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
public class ProfileActionRequest implements BotRequest {
    private Long chatId;
    private String telegramUsername;
    private UpdateIntent action;
    private Language detectedLanguage;
}

