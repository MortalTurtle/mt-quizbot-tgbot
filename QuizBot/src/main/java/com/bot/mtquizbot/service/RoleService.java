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

    private HashMap<String, GroupRole> nameToRole = null;
    private HashMap<GroupRole, RoleDb> enumToRoleDb = null;
    public RoleService(IRoleRepository repo) {
        this.repo = repo;
    }
    private void ConfigureMaps() {
        nameToRole = new HashMap<>();
        nameToRole.put("Owner", GroupRole.Owner);
        nameToRole.put("Contributor", GroupRole.Contributor);
        nameToRole.put("Participant", GroupRole.Participant);
        enumToRoleDb = new HashMap<>();
        log.trace("#### getRoleList() - configuring roledb to role enum maps");
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
        if (nameToRole == null)
            ConfigureMaps();
        return nameToRole.get(role.getName());
    }
    
    public List<RoleDb> getRoleDbList() {
        log.trace("#### getRoleList() - working");
        return repo.getRoleList();
    }

    public GroupRole getUserRole(User user, TestGroup group) {
        log.trace("#### getUserRole() [user={}, group={}]", user, group);
        var role = repo.getUserRole(user, group);
        if (nameToRole == null)
            ConfigureMaps();
        return nameToRole.get(role.getName());
    }

    public void addUserRole(TestGroup group, User user, GroupRole role) {
        log.trace("#### addRole() [group={}, user={}, role={}]", group, user, role);
        if (enumToRoleDb == null)
            ConfigureMaps();
        repo.addUserRole(group, user, enumToRoleDb.get(role));
    }
}