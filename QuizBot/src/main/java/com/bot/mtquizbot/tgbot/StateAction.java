package com.bot.mtquizbot.tgbot;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.bot.mtquizbot.models.BotState;

@Retention(RetentionPolicy.RUNTIME)
public @interface StateAction {
    BotState value();
}
