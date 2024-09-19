package com.bot.mtquizbot.repository;

import com.bot.mtquizbot.models.TestGroup;

public interface IGroupRepository {
    TestGroup getById(String id);
    void insert(TestGroup entity);
    void delete(TestGroup entity);
}
