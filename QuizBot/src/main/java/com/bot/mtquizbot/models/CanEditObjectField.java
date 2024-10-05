package com.bot.mtquizbot.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CanEditObjectField {
    String getPropertyButtonText();
}
