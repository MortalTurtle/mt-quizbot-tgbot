package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.Test;

public class TestMapper implements RowMapper<Test> {
    @Override
    public Test mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new Test(
                rs.getString("id"),
                rs.getString("group_id"),
                rs.getString("owner_id"),
                rs.getString("name"),
                rs.getInt("min_score"),
                rs.getString("description"),
                rs.getTimestamp("created_ts"));
        return entity;
    }
}