package com.bot.mtquizbot.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.BotState;
import com.bot.mtquizbot.tgbot.IntermediateVariable;

import jakarta.annotation.PostConstruct;

@Repository
public class RedisRepository implements IRedisRepository {
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;    
    private static Map<String, BotState> stateByName;
    private static final String BOT_STATE_KEY = "bot_state";
    @Autowired
    public RedisRepository(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
        if (stateByName == null) {
            stateByName = new HashMap<>();
            var states = BotState.values();
            for (var state : states)
                stateByName.put(state.name(), state);
        }
    }

    @Override
    public void putIntermediateVar(String userId, IntermediateVariable varKey, String value) {
        hashOperations.put(userId, varKey.name(), value);
    }

    @Override
    public String getIntermediateVar(String userId, IntermediateVariable varKey) {
        return (String) hashOperations.get(userId, varKey.name());
    }

    @Override
    public BotState getBotStateByUser(String userId) {
        return stateByName.get((String)hashOperations.get(userId, BOT_STATE_KEY));
    }

    @Override
    public void putBotState(String userId, BotState state) {
        hashOperations.put(userId, BOT_STATE_KEY, state.name());
    }
}