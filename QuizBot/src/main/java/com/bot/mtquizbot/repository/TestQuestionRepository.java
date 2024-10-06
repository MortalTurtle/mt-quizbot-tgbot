package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.TestQuestion;
import com.bot.mtquizbot.models.mapper.TestQuestionMapper; 

@Repository
public class TestQuestionRepository {

    private static final String SQL_SELECT_QUESTIONS_BY_TEST_ID = 
        "SELECT * FROM quizdb.test_questions WHERE test_id = ? ORDER BY created_ts";

    private static final String SQL_INSERT_QUESTION = 
        "INSERT INTO quizdb.test_questions(type_id, weight, text) " +
        "VALUES (?, ?, ?, ?) RETURNING *";

    private static final String SQL_SELECT_QUESTION_BY_ID = 
        "SELECT * FROM quizdb.test_questions WHERE id = ?";

    private static final TestQuestionMapper TEST_QUESTIONS_MAPPER = new TestQuestionMapper();
    private final JdbcTemplate template;

    public TestQuestionRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    public List<TestQuestion> getQuestionsByTestId(String testId) {
        return template.query(SQL_SELECT_QUESTIONS_BY_TEST_ID, TEST_QUESTIONS_MAPPER, testId);
    }

    public TestQuestion getQuestionById(String questionId) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_QUESTION_BY_ID, TEST_QUESTIONS_MAPPER, questionId)
        );
    }

    public TestQuestion addQuestion(String typeId, Integer weight, String text) {
        return DataAccessUtils.singleResult(
            template.query(SQL_INSERT_QUESTION, TEST_QUESTIONS_MAPPER, typeId, weight, text)
        );
    }
}