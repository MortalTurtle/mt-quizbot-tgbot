package com.bot.mtquizbot.models.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.mtquizbot.models.QuestionType;

public class QuestionTypeMapper implements RowMapper<QuestionType> {

    @Override
    public QuestionType mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new QuestionType(
                rs.getString("id"),
                rs.getString("type")
        );
        return entity;
    }
}