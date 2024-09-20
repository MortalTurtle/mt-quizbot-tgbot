package com.bot.mtquizbot.repository;

import java.util.List;

import com.bot.mtquizbot.models.Role;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;

public interface IRoleRepository {
    Role getById(String id);
    List<Role> getRoleList();
    Role getUserRole(User user, TestGroup group);
}
