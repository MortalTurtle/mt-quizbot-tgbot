package com.bot.mtquizbot.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.mapper.TestGroupMapper;

@Repository
public class GroupRepository implements IGroupRepository {

    private static final String SQL_SELECT_BY_ID = "" + 
        "SELECT * FROM quizdb.groups WHERE id = ?";
    private static final String SQL_INSERT = "" +
        "INSERT INTO quizdb.groups (name, description) VALUES (?, ?) RETURNING *";

    protected final static TestGroupMapper mapper = new TestGroupMapper();
    protected final JdbcTemplate template;

    public GroupRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public TestGroup getById(String id) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_BY_ID, mapper, id)
        );
    }

    @Override
    public TestGroup create(String name, String description) {
        return DataAccessUtils.singleResult(
            template.query(SQL_INSERT, mapper, name, description)
        );
    }

    @Override
    public void delete(TestGroup entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
