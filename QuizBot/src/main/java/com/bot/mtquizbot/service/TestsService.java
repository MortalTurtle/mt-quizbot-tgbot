package com.bot.mtquizbot.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.glassfish.grizzly.utils.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.models.Test;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.repository.ITestsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestsService extends BaseService {
    protected final ITestsRepository repo;
    private final HashMap<Class<?>, Function<String, Object>> convertStringValueToSomeClass = new HashMap<>();

    public TestsService(ITestsRepository repo) {
        this.repo = repo;
        convertStringValueToSomeClass.put(String.class, (String str) -> str);
        convertStringValueToSomeClass.put(Integer.class, (String str) -> Integer.valueOf(str));
    }

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

    public String getTestFullDescription(Test test) {
        return test.getName() + " - " + test.getDescription() + "\n" +
        (test.getMin_score() == null ? "" : "Min score to complete - " + Integer.toString(test.getMin_score()));
    }

    public InlineKeyboardMarkup getEditMenu(Test test) {
        var editQuestionsButton = InlineKeyboardButton.builder()
            .callbackData("/editquestions " + test.getId())
            .text("Questions 📌").build();
        var menu = BaseService.getEditMenuBuilder(test, "/ststfield");
        menu.keyboardRow(List.of(editQuestionsButton));
        var backButton = InlineKeyboardButton.builder().callbackData("/backtotests").text("Back 🚫").build();
        menu.keyboardRow(List.of(backButton));
        return menu.build();
    }

    public Pair<InlineKeyboardMarkup, String> getGroupTestsMenuWithDescription(TestGroup group, Integer maxTestButtonsInTestsMenuRow) {
        var tests = getTestList(group);
        StringBuilder strB = new StringBuilder();
        List<InlineKeyboardButton> testButtons = new ArrayList();
        var menu = InlineKeyboardMarkup.builder();
        int buttonsInRowleft = maxTestButtonsInTestsMenuRow;
        int cnt = 0;
        strB.append("your groups tests:\n");
        for (var test : tests) {
            cnt++;
            strB.append(Integer.toString(cnt) + "): " + 
            test.getName() + " - " + 
            test.getDescription() + "\n");
            buttonsInRowleft--;
            testButtons.add(
                InlineKeyboardButton.builder()
                .callbackData("/test " + test.getId())
                .text(Integer.toString(cnt) + "✅")
                .build()
            );
            if (buttonsInRowleft == 0) {
                menu.keyboardRow(testButtons);
                testButtons = new ArrayList<>();
                buttonsInRowleft = maxTestButtonsInTestsMenuRow;
            }
        }
        if (buttonsInRowleft > 0)
            menu.keyboardRow(testButtons);
        return new Pair(menu.build(), strB.toString());
    }

    public void updateTestProperty(Test test, String propertyName, String strVal) throws NoSuchFieldException,
        IllegalArgumentException,
        NumberFormatException {
        var field = test.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        var fieldType = field.getType();
        try {
            field.set(test, convertStringValueToSomeClass.get(fieldType).apply(strVal));
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        repo.updateTest(test);
    }
}
