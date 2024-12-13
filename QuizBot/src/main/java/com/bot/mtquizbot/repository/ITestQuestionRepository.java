package com.bot.mtquizbot.repository;

import java.util.List;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.models.TestQuestion;

public interface ITestQuestionRepository {
    QuestionType getQuestionTypeById(String id);

    List<String> getFalseAnswers(TestQuestion question);

    void addFalseAnswer(TestQuestion question, String answerText);

    List<QuestionType> getQuestionTypeList();

    List<TestQuestion> getQuestionsByTestId(String testId, int offset, int count);

    TestQuestion getQuestionById(String questionId);

    TestQuestion addQuestion(String testId, String typeId, Integer weight, String text);

    void updateTestQuestion(TestQuestion question);

}
