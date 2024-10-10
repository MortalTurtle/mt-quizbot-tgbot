package com.bot.mtquizbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User implements IModel {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("username")
    private final String group_id;

    @Override
    public String toString() { return id; }

    public Long getLongId() { return Long.parseLong(id); } 
}
