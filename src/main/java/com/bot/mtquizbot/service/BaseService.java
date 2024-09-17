package com.bot.mtquizbot.service;
import com.bot.mtquizbot.exceptions.NotFoundException;

import java.util.List;

public class BaseService {
    public <T> T wrapResult(T result) {
        if(result == null)
            throw new NotFoundException();
        return result;
    }
    public <T> List<T> wrapResults(List<T> result) {
        if(result == null || result.size() == 0)
            throw new NotFoundException();
        return result;
    }
}