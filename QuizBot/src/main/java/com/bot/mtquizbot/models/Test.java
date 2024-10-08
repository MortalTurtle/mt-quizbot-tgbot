/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.bot.mtquizbot.models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Test implements IModel {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("group_id")
    private String group_id;
    
    @JsonProperty("owner_id")
    private Long owner_id;

    @CanEditObjectField(getPropertyButtonText="Test name 🎆")
    @JsonProperty("name")
    private String name;

    @CanEditObjectField(getPropertyButtonText="Min score to beat 🥇")
    @JsonProperty("min_score")
    private Integer min_score;
    
    @CanEditObjectField(getPropertyButtonText="Description ✏️")
    @JsonProperty("description")
    private String description;

    @JsonProperty("created_ts")
    private Timestamp created_ts;
}
