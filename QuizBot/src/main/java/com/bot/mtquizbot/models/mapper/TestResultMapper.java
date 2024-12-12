package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.TestResult;

public class TestResultMapper implements RowMapper<TestResult> {
    @Override
    public TestResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new TestResult(
                rs.getString("user_id"),
                rs.getString("test_id"),
                rs.getInt("score"),
                rs.getTimestamp("finished_ts")
        );
        return entity;
    }
}