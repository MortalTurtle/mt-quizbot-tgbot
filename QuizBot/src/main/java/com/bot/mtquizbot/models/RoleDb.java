package com.bot.mtquizbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RoleDb {
    @JsonProperty("id")
    private final String id;
    
    @JsonProperty("id")
    private final String name;
}
