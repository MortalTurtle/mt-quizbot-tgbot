package com.bot.mtquizbot.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bot.mtquizbot.models.QuestionType;
import com.bot.mtquizbot.models.mapper.QuestionTypeMapper;

@Repository
public class QuestionRepository implements IQuestionRepository {

    private static final String SQL_SELECT_TYPE_LIST = "" + 
        "SELECT * FROM quizdb.question_type";

    private static final String SQL_SELECT_TYPE_BY_ID = "" + 
        "SELECT * FROM quizdb.question_type WHERE id = ?";

    protected final static QuestionTypeMapper QUESTION_TYPE_MAPPER = new QuestionTypeMapper();
    protected final JdbcTemplate template;

    public QuestionRepository(@Qualifier("bot-db") JdbcTemplate template) {
        this.template = template;
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

}
