package com.bot.mtquizbot.repository;

import com.bot.mtquizbot.models.BotState;
import com.bot.mtquizbot.tgbot.IntermediateVariable;

public interface IRedisRepository {
    void putIntermediateVar(String userId, IntermediateVariable varKey, String value);
    String getIntermediateVar(String userId, IntermediateVariable varKey);
    BotState getBotStateByUser(String userId);
    void putBotState(String userId, BotState state);
}