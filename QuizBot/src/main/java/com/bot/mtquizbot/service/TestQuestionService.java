package com.bot.mtquizbot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.models.TestQuestion;
import com.bot.mtquizbot.repository.ITestQuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestQuestionService {

    private final ITestQuestionRepository testQuestionRepository;

    public List<TestQuestion> getQuestionsByTestId(String testId, int offset, int count) {
        log.trace("#### getQuestionsByTestId() [testId={}]", testId);
        return testQuestionRepository.getQuestionsByTestId(testId, offset, count);
    }

    public TestQuestion getQuestionById(String questionId) {
        log.trace("#### getQuestionById() [questionId={}]", questionId);
        return testQuestionRepository.getQuestionById(questionId);
    }

    public TestQuestion addQuestion(String testId ,String typeId, Integer weight, String text) {
        log.trace("#### addQuestion() [testId={} typeId={}, weight={}, text={}]", testId, typeId, weight, text);
        return testQuestionRepository.addQuestion(testId, typeId, weight, text);
    }

    public InlineKeyboardMarkupBuilder getQuestionsMenuBuilder(List<TestQuestion> questions, int buttonsInSingleRow) {
        var menu = InlineKeyboardMarkup.builder();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int cnt = 0; 
        for (var question : questions) {
            cnt++;
            row.add(InlineKeyboardButton.builder()
                .text(Integer.toString(cnt) + " ✅")
                .callbackData("/editquestion " + question.getId())
                .build());
            if (row.size() == buttonsInSingleRow) {
                menu.keyboardRow(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty())
            menu.keyboardRow(row);
        return menu;
    }

    public InlineKeyboardMarkupBuilder getQuestionTypeMenuBuilder(List<QuestionType> types, int typeButtonsInARow) {
        var menu = InlineKeyboardMarkup.builder();
        List<InlineKeyboardButton> list = new ArrayList<>();
        for (var type : types) {
            list.add(InlineKeyboardButton.builder()
            .text(type.getType())
            .callbackData("/addquestionstagetype " + type.getId())
            .build()); 
            if (list.size() == typeButtonsInARow) {
                menu.keyboardRow(list);
                list = new ArrayList<>();
            }
        }
        if (!list.isEmpty())
            menu.keyboardRow(list);
        return menu;
    }

    public String getQuestionTypeDescriptionMessage(List<QuestionType> types) {
        var strB = new StringBuilder();
        for (var type : types) {
            strB.append(type.getType() + " - " + type.getDescription() + "\n");
        }
        return strB.toString();
    }
    
    public String getQuestionDescriptionMessage(List<TestQuestion> questions) {
        var strB = new StringBuilder();
        int cnt = 0;
        for (var question : questions) {
            cnt++;
            strB.append(Integer.toString(cnt) + "): " + question.getText() + 
                "\nWeight - " + Integer.toString(question.getWeight()) +
                "\n");
        }
        return strB.toString();
    }
}