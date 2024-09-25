package com.bot.mtquizbot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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

    public void updateTestName(Test test, String name) {
        log.trace("#### updateTestName() [test={}, name={}]", test, name);
        repo.updateTestName(test, name);
    }

    public void updateTestDescription(Test test, String description) {
        log.trace("#### updateTestDescription() [test={}, description={}]", test, description);
        repo.updateTestDescription(test, description);
    }

    public void updateTestScoreToBeat(Test test, Integer score) {
        log.trace("#### updateTestScoreToBeat() [test={}, score={}]", test, score);
        repo.updateTestScoreToBeat(test, score);
    }

    public InlineKeyboardMarkup getEditMenu(Test test) {
        var editQuestionsButton = InlineKeyboardButton.builder()
            .callbackData("/editquestions " + test.getId())
            .text("Questions 📌").build();
        var editNameButton = InlineKeyboardButton.builder()
            .callbackData("/settestproperty " + test.getId() + " TEST_NAME")
            .text("Test name 🎆").build();
        var editDescriptionButton = InlineKeyboardButton.builder()
            .callbackData("/settestproperty " + test.getId() + " TEST_D")
            .text("Description ✏️").build();
        var editMinScoreToBeatButton = InlineKeyboardButton.builder()
            .callbackData("/settestproperty " + test.getId() + " TEST_MS")
            .text("Min score to beat 🥇").build();
        var menu = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(editQuestionsButton))
            .keyboardRow(List.of(editNameButton))
            .keyboardRow(List.of(editDescriptionButton))
            .keyboardRow(List.of(editMinScoreToBeatButton))
            .build();
        return menu;
    }
}
