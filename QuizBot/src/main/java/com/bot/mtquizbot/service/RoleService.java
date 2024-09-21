package com.bot.mtquizbot.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.GroupRole;
import com.bot.mtquizbot.models.RoleDb;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.IRoleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleService extends BaseService {
    protected final IRoleRepository repo;
    protected final HashMap<String, GroupRole> nameToRole;
    protected final HashMap<GroupRole, RoleDb> enumToRoleDb;
    public RoleService(IRoleRepository repo) {
        this.repo = repo;
        nameToRole = new HashMap<>();
        enumToRoleDb = new HashMap<>();
        nameToRole.put("Owner", GroupRole.Owner);
        nameToRole.put("Contributor", GroupRole.Contributor);
        log.trace("#### getRoleLust() - working in constructor");
        var roles = repo.getRoleList();
        for (var role : roles)
            enumToRoleDb.put(
                nameToRole.get(role.getName()),
                role
            );
    }

    public GroupRole getById(String id) {
        log.trace("#### getById() [id={}]", id);
        var role = repo.getById(id);
        return nameToRole.get(role.getName());
    }
    public List<RoleDb> getRoleDbList() {
        log.trace("#### getRoleLust() - working");
        return repo.getRoleList();
    }

    public GroupRole getUserRole(User user, TestGroup group) {
        log.trace("#### getUserRole() [user={}, group={}]", user, group);
        var role = repo.getUserRole(user, group);
        return nameToRole.get(role.getName());
    }

    public void addUserRole(TestGroup group, User user, GroupRole role) {
        log.trace("#### addRole() [group={}, user={}, role={}]", group, user, role);
        repo.addUserRole(group, user, enumToRoleDb.get(role));
    }
}