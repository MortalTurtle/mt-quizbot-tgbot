package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.BotState;
import com.bot.mtquizbot.models.TestQuestion;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.IRedisRepository;
import com.bot.mtquizbot.repository.IUserRepository;
import com.bot.mtquizbot.tgbot.IntermediateVariable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService extends BaseService {
    protected final IUserRepository repo;
    protected final IRedisRepository cache;
    public List<User> getUserList() {
        log.trace("#### getUserList() - working");
        return repo.getUserList();
    }
    public User getById(long id) {
        log.trace("#### getById() [id={}]", id);
        return repo.getById(Long.toString(id));
    }

    public User getById(String id) {
        log.trace("#### getById() [id={}]", id);
        return repo.getById(id);
    }
    public void insert(User entity) {
        log.trace("#### insert() [entity={}]", entity);
        repo.insert(entity);
    }
    public void updateGroupById(long id, String groupId) {
        log.trace("#### updateGroup_id() [group_id={}, user_id={}]", id, groupId);
        repo.updateGroupById(Long.toString(id), groupId);
    }
    public void updateGroupById(String id, String groupId) {
        log.trace("#### updateGroup_id() [group_id={}, user_id={}]", id, groupId);
        repo.updateGroupById(id, groupId);
    }

    public BotState getBotState(String userId) {
        log.trace("#### getBotState() [userId={}]", userId);
        return cache.getBotStateByUser(userId);
    }

    public String getIntermediateVarString(String userId, IntermediateVariable var) {
        log.trace("#### getIntermediateVarString() [user_id={}, var={}]", userId, var);
        return cache.getIntermediateVar(userId, var);
    }

    public void putIntermediateVar(String userId, IntermediateVariable var, String value) {
        log.trace("#### putIntermediateVar() [user_id={}, var={}, value={}]", userId, var, value);
        cache.putIntermediateVar(userId, var, value);
    }

    public void putBotState(String userId, BotState state) {
        log.trace("#### putBotState() [userId={}, state={}]", userId, state.name());
        cache.putBotState(userId, state);
    }

    public void putQuestionsId(String userId, List<TestQuestion> questions){
        log.trace("#### putQuestionsId() [user_id={}, questions={}]", userId, questions);
        cache.putQuestionsId(userId, questions);
    }

    public String getQuestionId(String userId, Integer index){
        log.trace("#### getQuestionId() [user_id={}, index={}]", userId, index);
        return cache.getQuestionId(userId, index);
    }

    public Integer getUserScore(String userId, String testId){
        log.trace("#### getUserScore() [user_id={}, testId={}]", userId, testId);
        return cache.getUserScore(userId, testId);
    }

    public void putUserScore(String userId, String testId, Integer score){
        log.trace("#### putUserScore() [user_id={}, testId={}, score={}]", userId, testId, score);
        cache.putUserScore(userId, testId, score);
    }

}