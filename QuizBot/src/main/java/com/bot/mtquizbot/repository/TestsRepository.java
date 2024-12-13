package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.Test;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.TestResult;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.models.mapper.TestMapper;
import com.bot.mtquizbot.models.mapper.TestResultMapper;

@Repository
public class TestsRepository implements ITestsRepository {

    private static final String SQL_SELECT_TEST_LIST = "" +
            "SELECT * FROM quizdb.tests WHERE group_id = ? ORDER BY created_ts";
  
    private static final String SQL_INSERT_TEST = "" +
            "INSERT INTO quizdb.tests(group_id, owner_id, name, min_score, description) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING *";

    private static final String SQL_SELECT_TEST_BY_ID = "" +
            "SELECT * FROM quizdb.tests WHERE id = ?";
  
  
    private static final String SQL_SELECT_TEST_LIST_RESULTS = "" +
        "SELECT test_results.user_id, test_results.test_id, test_results.score, " +
        "test_results.finished_ts FROM quizdb.test_results, quizdb.tests " +
        "WHERE test_results.user_id = ? AND test_results.test_id = tests.id AND " +
        "tests.group_id = ? ORDER BY test_results.finished_ts LIMIT ? OFFSET ?";

    protected final static TestMapper TEST_MAPPER = new TestMapper();
    protected final static TestResultMapper TEST_RESULT_MAPPER = new TestResultMapper();
    protected final JdbcTemplate template;

    public TestsRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Test create(User owner, TestGroup group, String name, Integer minScore, String description) {
        return DataAccessUtils.singleResult(
                template.query(SQL_INSERT_TEST, TEST_MAPPER, group.getId(), owner.getId(), name, minScore,
                        description));
    }

    @Override
    public Test getById(String id) {
        return DataAccessUtils.singleResult(
                template.query(SQL_SELECT_TEST_BY_ID, TEST_MAPPER, id));
    }

    @Override
    public List<Test> getTestList(TestGroup group) {
        return template.query(SQL_SELECT_TEST_LIST, TEST_MAPPER, group.getId());
    }

    @Override
    public void updateTest(Test test) {
        template.update("" +
                "UPDATE quizdb.tests SET name = ?, min_score = ?, description = ? WHERE id = ?",
                test.getName(),
                test.getMin_score(),
                test.getDescription(),
                test.getId());
    }

    @Override
    public List<TestResult> getTestResultList(User user, Integer limit, Integer offset) {
        return template.query(SQL_SELECT_TEST_LIST_RESULTS, TEST_RESULT_MAPPER,
                            user.getId(), user.getGroup_id(), limit, offset
        );
    }


}
