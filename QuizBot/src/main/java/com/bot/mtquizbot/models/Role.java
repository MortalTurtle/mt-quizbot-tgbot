package com.bot.mtquizbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Role {
    @JsonProperty("id")
    private final String id;
    
    @JsonProperty("id")
    private final String name;
}
