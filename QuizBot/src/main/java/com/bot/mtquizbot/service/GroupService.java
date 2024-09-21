package com.bot.mtquizbot.service;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.IGroupRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService extends BaseService {
    protected final IGroupRepository repo;

    public TestGroup getById(String id) {
        log.trace("#### getById() [id={}]", id);
        return repo.getById(id);
    }
    
    public TestGroup create(String name, String description) {
        log.trace("#### create() [name={}, description={}]", name, description);
        return repo.create(name, description);
    }

    public TestGroup getUserGroup(User user) {
        log.trace("#### getUserGroup() [user={}]", user);
        return repo.getUserGroup(user);
    }
}