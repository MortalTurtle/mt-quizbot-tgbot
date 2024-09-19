package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.TestGroup;

public class TestGroupMapper implements RowMapper<TestGroup> {
    @Override
    public TestGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new TestGroup(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("description")
        );
        return entity;
    }
}