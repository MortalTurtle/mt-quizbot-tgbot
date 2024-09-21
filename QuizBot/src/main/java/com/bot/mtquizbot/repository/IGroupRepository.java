package com.bot.mtquizbot.repository;

import com.bot.mtquizbot.models.TestGroup;

public interface IGroupRepository {
    TestGroup getById(String id);
    TestGroup create(String name, String descritpion);
    void delete(TestGroup entity);
}
