package com.bot.mtquizbot.models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TestQuestion {
    @JsonProperty("id")
    private String id;

    @JsonProperty("test_id")
    private String testId;

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("text")
    private String text;

    @JsonProperty("created_ts")
    private Timestamp createdTs;
}