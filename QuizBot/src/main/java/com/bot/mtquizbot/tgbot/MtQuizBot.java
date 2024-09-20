package com.bot.mtquizbot.tgbot;
import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.bot.mtquizbot.models.BotState;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.service.GroupService;
import com.bot.mtquizbot.service.RoleService;
import com.bot.mtquizbot.service.UserService;

import lombok.Getter;
@Component
@Getter
public class MtQuizBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final UserService userService;
    private final GroupService groupService;
    private final RoleService roleService;
    private final HashMap<Long, BotState> botStateByUser = new HashMap<>();
    private final HashMap<BotState, Consumer<Update>> actionByBotState = new HashMap<>();
    private final HashMap<Long, HashMap<String, String>> infoByUser = new HashMap<>();
    private final HashMap<String, Consumer<Update>> actionByCommand = new HashMap<>();
    public MtQuizBot(TelegramBotsApi telegramBotsApi,
                     @Value("${telegram.bot.username}") String botUsername,
                     @Value("${telegram.bot.token}") String botToken,
                     UserService userService,
                     GroupService groupService,
                     RoleService roleService) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
        actionByBotState.put(BotState.idle, (Update update) -> {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            sendText(id, "Please enter a valid command");
        });
        actionByBotState.put(BotState.waitingForGroupCode, (Update update) -> {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            TestGroup group;
            if (msg.hasText()) {
                group = groupService.getById(msg.getText());
                if (group == null) {
                    sendText(id, "Wrong group code");
                    return;
                }
                userService.updateGroupById(id, group.getId());
            }
        });
        actionByBotState.put(BotState.waitingForGroupName, (Update update) -> {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            if (!msg.hasText()) {
                sendText(id, "No text for group name");
                return;
            }
            botStateByUser.replace(id, BotState.waitingForGroupDescription);
            infoByUser.get(id).put("Name", msg.getText());
            sendText(id, "Please enter group description");
        });
        actionByBotState.put(BotState.waitingForGroupDescription, (Update update) -> {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            if (!msg.hasText()) {
                sendText(id, "No text for group description");
                return;
            }
            botStateByUser.replace(id, BotState.idle);
            var group = groupService.create(infoByUser.get(id).get("Name"), msg.getText());
            sendText(id, "Your group: " + 
                group.getName() + 
                " - " +
                group.getDescription() + 
                "\n Was created, its ID is " + group.getId() + 
                " Please write it down");
        });
        actionByCommand.put("/start", (Update update) -> {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            sendText(id, "" + 
            "Welcome to bot, enter command /join to join group " +
            "\n /creategroup to create one");
        });
        actionByCommand.put("/join", (Update update) -> {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            botStateByUser.replace(id, BotState.waitingForGroupCode);
            sendText(id, "Please enter a group code ");
        });
        actionByCommand.put("/creategroup", (Update update) -> {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            botStateByUser.replace(id, BotState.waitingForGroupName);
            sendText(id, "Please enter a group name");
        });
        telegramBotsApi.registerBot(this);
    }

    private void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();
        if (!infoByUser.containsKey(id))
            infoByUser.put(id, new HashMap<>());
        userService.insert(new User(id, user.getUserName(), null));
        if (!botStateByUser.containsKey(id))
            botStateByUser.put(id, BotState.idle);
        if (msg.hasText() && actionByCommand.containsKey(msg.getText())) {
            actionByCommand.get(msg.getText()).accept(update);
            return;
        }
        actionByBotState.get(botStateByUser.get(id)).accept(update);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
    @Override
    public String getBotToken() {
        return botToken;
    }
}
