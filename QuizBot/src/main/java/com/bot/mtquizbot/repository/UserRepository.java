package com.bot.mtquizbot.repository;

import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.models.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository implements IUserRepository {

    // constants
    private static final String SQL_SELECT_BY_NAME = "" +
            "SELECT id, username FROM quizdb.users WHERE id=?";
    private static final String SQL_SELECT_LIST = "" +
            "SELECT id, username FROM quizdb.users";
    private static final String SQL_INSERT = "" +
            "INSERT INTO quizdb.users (id, username) VALUES (?,?)";
    private static final String SQL_DELETE = "" +
            "DELETE FROM quizdb.users WHERE id = ?";

    protected final static UserMapper USER_MAPPER = new UserMapper();
    protected final JdbcTemplate template;

    public UserRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }
    @Override
    public User getById(long id) {
        return DataAccessUtils.singleResult(
                template.query(SQL_SELECT_BY_NAME, USER_MAPPER, id));
    }
    @Override
    public List<User> getUserList() {
        return template.query(SQL_SELECT_LIST, USER_MAPPER);
    }
    @Override
    public void insert(User entity) {
        var result = template.update(SQL_INSERT,
            entity.getId(),
            entity.getUsername());
    }
    @Override
    public void delete(User entity){
        var result = template.update(SQL_DELETE, entity.getId());
    }
}