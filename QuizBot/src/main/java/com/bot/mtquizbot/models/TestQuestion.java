package com.bot.mtquizbot.models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestQuestion implements IModel {
    @JsonProperty("id")
    private String id;

    @JsonProperty("test_id")
    private String testId;

    @JsonProperty("type_id")
    private String typeId;

    @CanEditObjectField(getPropertyButtonText="Correct answer ✅")
    @JsonProperty("answer")
    private String answer;

    @CanEditObjectField(getPropertyButtonText="Weight 💰")
    @JsonProperty("weight")
    private Integer weight;

    @CanEditObjectField(getPropertyButtonText="Text ❓")
    @JsonProperty("text")
    private String text;

    @JsonProperty("created_ts")
    private Timestamp createdTs;
}