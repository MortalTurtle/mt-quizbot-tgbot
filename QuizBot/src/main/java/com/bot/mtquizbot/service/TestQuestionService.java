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
public class TestQuestionService extends BaseService {

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

    public void addFalseAnswer(TestQuestion question, String ansTextString) {
        log.trace("#### addFalseAnswer() [question={}, text={}]", question, ansTextString);
        testQuestionRepository.addFalseAnswer(question, ansTextString);
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

    public QuestionType getQuestionTypeById(String id) {
        log.trace("#### getQuestionTypeById() [id={}]", id);
        return testQuestionRepository.getQuestionTypeById(id);
    }

    public List<QuestionType> getQuestionTypeList() {
        log.trace("#### getQuestionTypeList() - working");
        return testQuestionRepository.getQuestionTypeList();
    }

    public InlineKeyboardMarkupBuilder getQuestionEditMenu(TestQuestion question) {
        var menu = BaseService.getEditMenuBuilder(question, "/setqfield");
        if (this.getQuestionTypeById(question.getTypeId()).getType().equals("Choose"))
            menu.keyboardRow(
                List.of(
                    InlineKeyboardButton.builder()
                    .text("Check false answers 🙈")
                    .callbackData("/falseanswers " + question.getId()).build()
                )
            );
        menu.keyboardRow(
            List.of(
                InlineKeyboardButton.builder()
                .text("Back to questions 📍")
                .callbackData("/editquestions " + question.getTestId())
                .build()
            )
        );
        return menu;
    }

    public InlineKeyboardMarkupBuilder getFalseAnswersMenu(String questionId) {
        return InlineKeyboardMarkup.builder().keyboardRow(
            List.of(
                InlineKeyboardButton.builder()
                .text("Add false answer ⭕️")
                .callbackData("/addfalseanswer " + questionId)
                .build(),
                InlineKeyboardButton.builder()
                .text("Back to question ❓")
                .callbackData("/editquestion " + questionId)
                .build()
            )
        );
    }

    public String getFalseAnswersString(TestQuestion question) {
        var strB = new StringBuilder();
        strB.append("False answers: ");
        var falseAnswers = testQuestionRepository.getFalseAnswers(question);
        for (var ans : falseAnswers)
            strB.append("\n" + ans);
        return strB.toString();
    }

    public List<String>  getFalseAnswersStringList (TestQuestion question){
        var listFalseAnswer = testQuestionRepository.getFalseAnswers(question);
        return listFalseAnswer;
    }

    public String getQuestionDescriptionMessage(TestQuestion question) {
        return question.getText() + 
            "\nAnswer: " + (question.getAnswer() == null ? "No answer, please add one" : question.getAnswer()) + 
            "\nWeight: " + Integer.toString(question.getWeight());
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

    public void updateQuestionProperty(TestQuestion q, String propertyName, String strVal) throws NumberFormatException  {
        try {
            setNewFieldValueFromString(q, propertyName, strVal);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
        testQuestionRepository.updateTestQuestion(q);
    }
}