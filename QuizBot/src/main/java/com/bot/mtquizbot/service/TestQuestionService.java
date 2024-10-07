package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.TestQuestion;
import com.bot.mtquizbot.repository.TestQuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestQuestionService {

    private final TestQuestionRepository testQuestionRepository;

    public List<TestQuestion> getQuestionsByTestId(String testId) {
        log.trace("#### getQuestionsByTestId() [testId={}]", testId);
        return testQuestionRepository.getQuestionsByTestId(testId);
    }

    public TestQuestion getQuestionById(String questionId) {
        log.trace("#### getQuestionById() [questionId={}]", questionId);
        return testQuestionRepository.getQuestionById(questionId);
    }

    public TestQuestion addQuestion(String typeId, Integer weight, String text) {
        log.trace("#### addQuestion() [typeId={}, weight={}, text={}]",  typeId, weight, text);
        return testQuestionRepository.addQuestion(typeId, weight, text);
    }
    
}