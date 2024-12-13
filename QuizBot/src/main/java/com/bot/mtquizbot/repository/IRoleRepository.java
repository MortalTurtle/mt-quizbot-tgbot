package com.bot.mtquizbot.repository;

import java.util.List;

import com.bot.mtquizbot.models.RoleDb;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;

public interface IRoleRepository {
    RoleDb getById(String id);

    List<RoleDb> getRoleList();

    RoleDb getUserRole(User user, TestGroup group);

    void addUserRole(TestGroup group, User user, RoleDb role);
}
