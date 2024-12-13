package com.bot.mtquizbot.repository;

import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;

public interface IGroupRepository {
    TestGroup getById(String id);

    TestGroup create(String name, String descritpion);

    void delete(TestGroup entity);

    TestGroup getUserGroup(User user);
}
