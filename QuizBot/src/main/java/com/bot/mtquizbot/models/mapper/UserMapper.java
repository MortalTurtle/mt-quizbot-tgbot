package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.User;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new User(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("group_id"));
        return entity;
    }
}