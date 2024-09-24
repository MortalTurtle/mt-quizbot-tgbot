package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.repository.IQuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService extends BaseService {
    protected final IQuestionRepository repo;

    public QuestionType getQuestionTypeById(String id) {
        log.trace("#### getQuestionTypeById() [id={}]", id);
        return repo.getQuestionTypeById(id);
    }

    public List<QuestionType> getQuestionTypeList() {
        log.trace("#### getQuestionTypeList() - working");
        return repo.getQuestionTypeList();
    }
}
