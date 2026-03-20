package com.ivan.bot.handler;

import com.ivan.bot.fsm.UserStateManager;
import com.ivan.bot.handler.impl.CreateProfileHandler;
import com.ivan.bot.handler.impl.NlpHandler;
import com.ivan.bot.handler.impl.ProfileHandler;
import com.ivan.bot.handler.impl.UpdateProfileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class CommandHandlerManager {

    private final NlpHandler nlpHandler;
    private final CreateProfileHandler createProfileHandler;
    private final UpdateProfileHandler updateProfileHandler;
    private final ProfileHandler profileHandler;
    private final UserStateManager stateManager;

    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        if (text.startsWith("/")) {
            return handleCommand(text.toLowerCase(), update, chatId);
        }

        if (stateManager.isInCreateProfileFlow(chatId)) {
            return createProfileHandler.handle(update);
        }

        if (stateManager.isInUpdateProfileFlow(chatId)) {
            return updateProfileHandler.handle(update);
        }

        return nlpHandler.handle(update);
    }

    private SendMessage handleCommand(String command, Update update, Long chatId) {
        return switch (command) {
            case "/createprofile" -> {
                stateManager.resetState(chatId);
                yield createProfileHandler.handle(update);
            }
            case "/updateprofile" -> {
                stateManager.resetState(chatId);
                yield updateProfileHandler.handle(update);
            }
            case "/profile" -> profileHandler.handle(update);
            case "/cancel" -> {
                stateManager.resetState(chatId);
                yield SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Operation cancelled. You can start fresh with any command.")
                        .build();
            }
            case "/start" -> SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Welcome to Virtual Assistant Bot! 🤖\n\n" +
                          "I can help you with:\n" +
                          "• Weather forecasts - just ask about weather in any city\n" +
                          "• Currency exchange rates - ask about currency conversions\n\n" +
                          "Commands:\n" +
                          "/createProfile - Create your profile with preferences\n" +
                          "/updateProfile - Update your profile settings\n" +
                          "/profile - View your current profile\n" +
                          "/cancel - Cancel current operation\n" +
                          "/help - Show this help message\n\n" +
                          "Or just type naturally, like 'What's the weather in London?' or 'Convert USD to EUR'")
                    .build();
            case "/help" -> SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("📚 Help\n\n" +
                          "Commands:\n" +
                          "/createProfile - Create your profile\n" +
                          "/updateProfile - Update your profile\n" +
                          "/profile - View your profile\n" +
                          "/cancel - Cancel current operation\n\n" +
                          "Natural Language:\n" +
                          "• Weather: 'Weather in Paris', 'What's the temperature in London?'\n" +
                          "• Currency: 'USD to EUR', 'Convert dollars to euros'\n\n" +
                          "Your saved preferences will be used as defaults!")
                    .build();
            default -> nlpHandler.handle(update);
        };
    }
}
