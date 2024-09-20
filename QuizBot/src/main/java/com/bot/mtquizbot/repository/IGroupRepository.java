package com.bot.mtquizbot.repository;

import com.bot.mtquizbot.models.Role;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;

public interface IGroupRepository {
    TestGroup getById(String id);
    void insert(TestGroup entity);
    void delete(TestGroup entity);
    void addUserRole(TestGroup group, User user, Role role);
}
