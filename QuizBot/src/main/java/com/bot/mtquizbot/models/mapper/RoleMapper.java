package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.Role;

public class RoleMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new Role(
                rs.getString("id"),
                rs.getString("name")
        );
        return entity;
    }
}