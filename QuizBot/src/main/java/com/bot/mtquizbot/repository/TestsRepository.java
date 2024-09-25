package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.models.Test;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.models.mapper.QuestionTypeMapper;
import com.bot.mtquizbot.models.mapper.TestMapper;

@Repository
public class TestsRepository implements ITestsRepository {

    private static final String SQL_SELECT_TYPE_LIST = "" + 
        "SELECT * FROM quizdb.question_type";

    private static final String SQL_SELECT_TYPE_BY_ID = "" + 
        "SELECT * FROM quizdb.question_type WHERE id = ?";

    private static final String SQL_SELECT_TEST_LIST = "" + 
        "SELECT * FROM quizdb.tests WHERE group_id = ? ORDER BY created_ts";
    
    private static final String SQL_INSERT_TEST = "" + 
        "INSERT INTO quizdb.tests(group_id, owner_id, name, min_score, description) " +
        "VALUES (?, ?, ?, ?, ?) RETURNING *";

    private static final String SQL_SELECT_TEST_BY_ID = "" + 
        "SELECT * FROM quizdb.tests WHERE id = ?";

    protected final static QuestionTypeMapper QUESTION_TYPE_MAPPER = new QuestionTypeMapper();
    protected final static TestMapper TEST_MAPPER = new TestMapper();
    protected final JdbcTemplate template;

    public TestsRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public QuestionType getQuestionTypeById(String id) { 
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_TYPE_BY_ID, QUESTION_TYPE_MAPPER, id)
        );
    }

    @Override
    public List<QuestionType> getQuestionTypeList() {
        return template.query(SQL_SELECT_TYPE_LIST, QUESTION_TYPE_MAPPER);
    }

    @Override
    public Test create(User owner, TestGroup group, String name, Integer minScore, String description) {
        return DataAccessUtils.singleResult(
            template.query(SQL_INSERT_TEST, TEST_MAPPER, group.getId(), owner.getId(), name, minScore, description)
        );
    }

    @Override
    public Test getById(String id) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_TEST_BY_ID, TEST_MAPPER, id)
        );
    }

    @Override
    public List<Test> getTestList(TestGroup group) {
        return template.query(SQL_SELECT_TEST_LIST, TEST_MAPPER, group.getId());
    }

    @Override
    public void updateTestDescription(Test test, String description) {
        template.update("UPDATE quizdb.tests SET description = ? WHERE id = ?",
        description, test.getId());
    }

    @Override
    public void updateTestName(Test test, String name) {
        template.update("UPDATE quizdb.tests SET name = ? WHERE id = ?",
        name, test.getId());
    }

    @Override
    public void updateTestScoreToBeat(Test test, Integer score) {
        template.update("UPDATE quizdb.tests SET min_score = ? WHERE id = ?",
        score, test.getId());
    }

}
