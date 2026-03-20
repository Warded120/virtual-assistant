package com.ivan.bot.repository;

import com.ivan.bot.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);

    Optional<UserProfile> findByTelegramUsername(String telegramUsername);
}

