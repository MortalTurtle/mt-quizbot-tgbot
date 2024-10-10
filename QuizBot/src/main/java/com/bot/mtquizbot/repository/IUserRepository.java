package com.bot.mtquizbot.repository;
import  java.util.List;

import com.bot.mtquizbot.models.User;

public interface IUserRepository {
    User getById(String id);
    void updateGroupById(String id, String groupId);
    List<User> getUserList();
    void insert(User entity);
}