package com.bot.mtquizbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TestGroup {
    @JsonProperty("id")
    private final String id;
    
    @JsonProperty("name")
    private final String name;
    
    @JsonProperty("description")
    private final String description;

    @Override
    public String toString() { return id + name + " " + description; }
}
