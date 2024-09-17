package com.bot.mtquizbot.repository;
import  com.bot.mtquizbot.models.User;
import java.util.List;

public interface IUserRepository {
    User getById(long id);
    List<User> getUserList();
    void insert(User entity);
    void delete(User entity);
}