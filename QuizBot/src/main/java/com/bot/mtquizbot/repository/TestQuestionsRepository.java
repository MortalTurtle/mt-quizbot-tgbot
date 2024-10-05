package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.TestQuestions;
import com.bot.mtquizbot.models.mapper.TestQuestionsMapper;

@Repository
public class TestQuestionsRepository {

    private static final String SQL_SELECT_QUESTIONS_BY_TEST_ID = 
        "SELECT * FROM quizdb.test_questions WHERE test_id = ? ORDER BY created_ts";

    private static final String SQL_INSERT_QUESTION = 
        "INSERT INTO quizdb.test_questions(test_id, type_id, weight, text) " +
        "VALUES (?, ?, ?, ?) RETURNING *";

    private static final String SQL_SELECT_QUESTION_BY_ID = 
        "SELECT * FROM quizdb.test_questions WHERE id = ?";

    private static final TestQuestionsMapper TEST_QUESTIONS_MAPPER = new TestQuestionsMapper();
    private final JdbcTemplate template;

    public TestQuestionsRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public List<TestQuestions> getQuestionsByTestId(String testId) {
        return template.query(SQL_SELECT_QUESTIONS_BY_TEST_ID, TEST_QUESTIONS_MAPPER, testId);
    }

    public TestQuestions getQuestionById(String questionId) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_QUESTION_BY_ID, TEST_QUESTIONS_MAPPER, questionId)
        );
    }

    public TestQuestions addQuestion(String testId, String typeId, Integer weight, String text) {
        return DataAccessUtils.singleResult(
            template.query(SQL_INSERT_QUESTION, TEST_QUESTIONS_MAPPER, testId, typeId, weight, text)
        );
    }
}
