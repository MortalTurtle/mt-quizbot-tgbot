package com.bot.mtquizbot.repository;

import java.util.List;

import com.bot.mtquizbot.models.QuestionType;

public interface IQuestionRepository {
    QuestionType getQuestionTypeById(String id);
    List<QuestionType> getQuestionTypeList();
}
