package com.bot.mtquizbot.repository;

import java.util.List;

import com.bot.mtquizbot.models.BotState;
import com.bot.mtquizbot.models.TestQuestion;
import com.bot.mtquizbot.tgbot.IntermediateVariable;

public interface IRedisRepository {
    void putIntermediateVar(String userId, IntermediateVariable varKey, String value);
    String getIntermediateVar(String userId, IntermediateVariable varKey);
    BotState getBotStateByUser(String userId);
    void putBotState(String userId, BotState state);
    void putQuestionsId(String userId, List<TestQuestion> questions);
    String getQuestionId(String userId, Integer index);
    Integer getUserScore(String userId, String testId);
    void putUserScore(String userId, String testId, Integer score);
}