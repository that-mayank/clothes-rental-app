package com.nineleaps.leaps.repository;

import com.nineleaps.leaps.model.ChatBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatBotRepository extends JpaRepository<ChatBot,Long> {
}
