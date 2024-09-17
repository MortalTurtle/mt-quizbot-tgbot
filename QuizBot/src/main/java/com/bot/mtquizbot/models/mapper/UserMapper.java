package com.bot.mtquizbot.models.mapper;
import org.springframework.jdbc.core.RowMapper;
import com.bot.mtquizbot.models.User;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        var entity = new User(
                rs.getLong("id"),
                rs.getString("username")
        );
        return entity;
    }
}