package com.bot.mtquizbot.models;

public enum BotState {
    idle,
    waitingForGroupCode,
    waitingForGroupName,
    waitingForGroupDescription,
    waitingForTestName,
    waitingForTestDescription,
    waitingForNewTestProperty,
    waitingForQuestionText,
    waitingForNewQuestionProperty,
    waitingForNewFalseAnswer,
    waitingForQuestionsAnswer
}
