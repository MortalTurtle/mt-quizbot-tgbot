package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.RoleDb;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.models.mapper.RoleMapper;

@Repository
public class RoleRepository implements IRoleRepository{

    private static final String SQL_SELECT_BY_ID = "" + 
        "SELECT * FROM quizdb.group_roles WHERE id = ?";

    private static final String SQL_SELECT_BY_USER_GROUP_ID = "" + 
        "SELECT group_roles.id, group_roles.name FROM quizdb.group_roles, quizdb.group_users" + 
        "WHERE group_users.user_id = ? AND group_users.group_id = ? AND group_roles.id = group_users.group_role_id";

    private static final String SQL_SELECT_LIST = "" + 
        "SELECT * FROM quizdb.group_roles";

        
    private static final String SQL_ADD_ROLE = "" +
        "INSERT INTO quizdb.group_users (group_id, user_id, group_role_id) VALUES(?, ?, ?)" +
        "ON CONFLICT(group_id, user_id) DO UPDATE SET group_role_id = $3";
    protected final static RoleMapper roleMapper = new RoleMapper();
    protected final JdbcTemplate template;

    public RoleRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public RoleDb getById(String id) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_BY_ID, roleMapper, id)
        );
    }

    @Override
    public List<RoleDb> getRoleList() {
        return template.query(SQL_SELECT_LIST, roleMapper);
    }

    @Override
    public RoleDb getUserRole(User user, TestGroup group) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_BY_USER_GROUP_ID, roleMapper, user.getId(), group.getId())
        );
    }

    @Override
    public void addUserRole(TestGroup group, User user, RoleDb role) {
        var result = template.update(SQL_ADD_ROLE,
        group.getId(),
        user.getId(),
        role.getId());
    }
}
