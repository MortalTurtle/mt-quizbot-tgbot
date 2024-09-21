package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.RoleDb;

public class RoleMapper implements RowMapper<RoleDb> {

    @Override
    public RoleDb mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new RoleDb(
                rs.getString("id"),
                rs.getString("name")
        );
        return entity;
    }
}