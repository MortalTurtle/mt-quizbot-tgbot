package com.bot.mtquizbot.repository;
import  java.util.List;

import com.bot.mtquizbot.models.User;

public interface IUserRepository {
    User getById(long id);
    void updateGroupById(long id, String groupId);
    List<User> getUserList();
    void insert(User entity);
}