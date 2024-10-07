package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.TestQuestion;

public class TestQuestionMapper implements RowMapper<TestQuestion> {
    @Override
    public TestQuestion mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new TestQuestion(
                rs.getString("id"),
                rs.getString("test_id"),
                rs.getString("type_id"),
                rs.getInt("weight"),
                rs.getString("text"),
                rs.getTimestamp("created_ts")
        );
        return entity;
    }
}