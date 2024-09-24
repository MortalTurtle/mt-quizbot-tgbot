/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.bot.mtquizbot.models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Test {
    @JsonProperty("id")
    private final String id;
    
    @JsonProperty("group_id")
    private final String group_id;
    
    @JsonProperty("owner_id")
    private final Long owner_id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("min_score")
    private final Integer min_score;
    
    @JsonProperty("description")
    private final String description;

    @JsonProperty("created_ts")
    private final Timestamp created_ts;
}
