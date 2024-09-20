package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.Role;
import com.bot.mtquizbot.models.mapper.RoleMapper;

@Repository
public class RoleRepository implements IRoleRepository{

    private static final String SQL_SELECT_BY_ID = "" + 
        "SELECT * FROM quizdb.group_roles WHERE id = ?";

    private static final String SQL_SELECT_LIST = "" + 
        "SELECT * FROM quizdb.group_roles";
    protected final static RoleMapper roleMapper = new RoleMapper();
    protected final JdbcTemplate template;

    public RoleRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Role getById(String id) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_BY_ID, roleMapper, id)
        );
    }

    @Override
    public List<Role> getRoleList() {
        return template.query(SQL_SELECT_LIST, roleMapper);
    }

}
