package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.Role;
import com.bot.mtquizbot.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService extends BaseService {
    protected final IRoleRepository repo;

    public Role getById(String id) {
        log.trace("#### getById() [id={}]", id);
        return repo.getById(id);
    }
    public List<Role> getRoleList() {
        log.trace("#### getRoleLust() - working");
        return repo.getRoleList();
    }
}