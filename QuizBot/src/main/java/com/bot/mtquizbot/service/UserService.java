package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.BotState;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.IRedisRepository;
import com.bot.mtquizbot.repository.IUserRepository;

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

    public void putBotState(String userId, BotState state) {
        log.trace("#### putBotState() [userId={}, state={}]", userId, state.name());
        cache.putBotState(userId, state);
    }

}