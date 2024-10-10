package com.bot.mtquizbot.repository;

import java.util.List;

import com.bot.mtquizbot.models.TestQuestion;

public interface ITestQuestionRepository {
    List<TestQuestion> getQuestionsByTestId(String testId, int offset, int count);
    TestQuestion getQuestionById(String questionId);
    TestQuestion addQuestion(String testId, String typeId, Integer weight, String text);
    void updateTestQuestion(TestQuestion question);
}
