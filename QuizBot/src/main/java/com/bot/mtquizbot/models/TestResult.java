package com.bot.mtquizbot.models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TestResult {

    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("test_id")
    private final String testId;

    @JsonProperty("score")
    private final Integer score;

    @JsonProperty("finished_ts")
    private final Timestamp finishedTs;
}
