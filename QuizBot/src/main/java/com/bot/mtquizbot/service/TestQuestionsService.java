package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.mtquizbot.models.TestQuestions;
import com.bot.mtquizbot.repository.TestQuestionsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestQuestionsService {

    private final TestQuestionsRepository testQuestionsRepository;

    public TestQuestionsService(TestQuestionsRepository testQuestionsRepository) {
        this.testQuestionsRepository = testQuestionsRepository;
    }

    public List<TestQuestions> getQuestionsByTestId(String testId) {
        log.trace("#### getQuestionsByTestId() [testId={}]", testId);
        return testQuestionsRepository.getQuestionsByTestId(testId);
    }

    public TestQuestions getQuestionById(String questionId) {
        log.trace("#### getQuestionById() [questionId={}]", questionId);
        return testQuestionsRepository.getQuestionById(questionId);
    }

    public TestQuestions addQuestion(String testId, String typeId, Integer weight, String text) {
        log.trace("#### addQuestion() [testId={}, typeId={}, weight={}, text={}]", testId, typeId, weight, text);
        return testQuestionsRepository.addQuestion(testId, typeId, weight, text);
    }
    
}
