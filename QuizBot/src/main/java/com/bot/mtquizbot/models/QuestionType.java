package com.bot.mtquizbot.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QuestionType {
    private final String id;
    private final String type;
}
