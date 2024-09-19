package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService extends BaseService {
    protected final IUserRepository repo;
    public List<User> getUserList() {
        log.trace("#### getUserList() - working");
        return repo.getUserList();
    }
    public User getById(long id) {
        log.trace("#### getById() [id={}]", id);
        return repo.getById(id);
    }
    public void insert(User entity) {
        log.trace("#### insert() [entity={}]", entity);
        repo.insert(entity);
    }
    public void updateGroupById(long id, String groupId) {
        log.trace("#### updateGroup_id() [group_id={}, user_id={}]", id, groupId);
        repo.updateGroupById(id, groupId);
    }

}