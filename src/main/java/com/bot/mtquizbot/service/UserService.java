package com.bot.mtquizbot.service;

import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void delete(User entity) {
        log.trace("#### delete() [entity={}]", entity);
        repo.delete(entity);
    }

}