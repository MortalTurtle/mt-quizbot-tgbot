package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.models.TestQuestion;
import com.bot.mtquizbot.models.mapper.FalseAnswerMapper;
import com.bot.mtquizbot.models.mapper.QuestionTypeMapper;
import com.bot.mtquizbot.models.mapper.TestQuestionMapper;

@Repository
public class TestQuestionRepository implements ITestQuestionRepository {

    private static final String SQL_SELECT_QUESTIONS_BY_TEST_ID = 
        "SELECT * FROM quizdb.test_questions WHERE test_id = ? ORDER BY created_ts LIMIT ? OFFSET ?";

    private static final String SQL_INSERT_QUESTION = 
        "INSERT INTO quizdb.test_questions(test_id, type_id, weight, text) " +
        "VALUES (?, ?, ?, ?) RETURNING *";

    private static final String SQL_SELECT_QUESTION_BY_ID = 
        "SELECT * FROM quizdb.test_questions WHERE id = ?";

    private static final String SQL_SELECT_TYPE_LIST = "" + 
        "SELECT * FROM quizdb.question_type";

    private static final String SQL_SELECT_TYPE_BY_ID = "" + 
        "SELECT * FROM quizdb.question_type WHERE id = ?";


    protected final static QuestionTypeMapper QUESTION_TYPE_MAPPER = new QuestionTypeMapper();
    private static final TestQuestionMapper TEST_QUESTIONS_MAPPER = new TestQuestionMapper();
    private static final FalseAnswerMapper FALSE_ANSWER_MAPPER = new FalseAnswerMapper();
    private final JdbcTemplate template;

    public TestQuestionRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<TestQuestion> getQuestionsByTestId(String testId, int offset, int count) {
        return template.query(SQL_SELECT_QUESTIONS_BY_TEST_ID, TEST_QUESTIONS_MAPPER, testId, count, offset);
    }

    @Override
    public TestQuestion getQuestionById(String questionId) {
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_QUESTION_BY_ID, TEST_QUESTIONS_MAPPER, questionId)
        );
    }

    @Override
    public TestQuestion addQuestion(String testId ,String typeId, Integer weight, String text) {
        return DataAccessUtils.singleResult(
            template.query(SQL_INSERT_QUESTION, TEST_QUESTIONS_MAPPER, testId, typeId, weight, text)
        );
    }

    @Override
    public void updateTestQuestion(TestQuestion question) {
        template.update("" +
            "UPDATE quizdb.test_questions SET type_id = ?, answer = ?, weight = ?, text = ? WHERE id = ?",
            question.getTypeId(),
            question.getAnswer(),
            question.getWeight(),
            question.getText(),
            question.getId()
        );
    }

    @Override
    public QuestionType getQuestionTypeById(String id) { 
        return DataAccessUtils.singleResult(
            template.query(SQL_SELECT_TYPE_BY_ID, QUESTION_TYPE_MAPPER, id)
        );
    }

    @Override
    public List<QuestionType> getQuestionTypeList() {
        return template.query(SQL_SELECT_TYPE_LIST, QUESTION_TYPE_MAPPER);
    }

    @Override
    public List<String> getFalseAnswers(TestQuestion question) {
        return template.query(
            "SELECT text FROM quizdb.question_false_answers WHERE question_id = ?",
            FALSE_ANSWER_MAPPER,
            question.getId()
        );
    }

    @Override
    public void addFalseAnswer(TestQuestion question, String answerText) {
        template.update(
            "INSERT INTO quizdb.question_false_answers(question_id, text) VALUES (?, ?)",
            question.getId(),
            answerText
        );
    }
}