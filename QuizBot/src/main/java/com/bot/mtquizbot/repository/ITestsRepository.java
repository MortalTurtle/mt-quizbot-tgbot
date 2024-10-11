package com.bot.mtquizbot.repository;

import java.util.List;

import com.bot.mtquizbot.models.Test;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;

public interface ITestsRepository {
    Test create(User owner,
                TestGroup group,
                String name,
                Integer minScore,
                String description);
    Test getById(String id);
    List<Test> getTestList(TestGroup group);
    void updateTest(Test test);
}
