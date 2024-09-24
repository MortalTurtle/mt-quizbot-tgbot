package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.models.Test;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.ITestsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestsService extends BaseService {
    protected final ITestsRepository repo;

    public QuestionType getQuestionTypeById(String id) {
        log.trace("#### getQuestionTypeById() [id={}]", id);
        return repo.getQuestionTypeById(id);
    }

    public List<QuestionType> getQuestionTypeList() {
        log.trace("#### getQuestionTypeList() - working");
        return repo.getQuestionTypeList();
    }

    public Test create(User owner, TestGroup group, String name, Integer minScore, String description) {
        log.trace("#### create() [owner={}, group={}, name={}, minScore={}, description={}]",
            owner,group,name,minScore,description);
        return repo.create(owner, group, name, minScore, description);
    }

    public Test getById(String id) {
        log.trace("#### getById() [id={}]", id);
        return repo.getById(id);
    }

    public List<Test> getTestList(TestGroup group) {
        log.trace("#### getTestList() [group={}]", group);
        return repo.getTestList(group);
    }
}
