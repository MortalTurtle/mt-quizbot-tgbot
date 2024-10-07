package com.bot.mtquizbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QuestionType {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("type")
    private final String type;

    @JsonProperty("description")
    private final String description;
}
