package com.bot.mtquizbot.tgbot;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.bot.mtquizbot.models.BotState;
import com.bot.mtquizbot.models.GroupRole;
import com.bot.mtquizbot.models.TestGroup;
import com.bot.mtquizbot.models.User;
import com.bot.mtquizbot.service.GroupService;
import com.bot.mtquizbot.service.RoleService;
import com.bot.mtquizbot.service.TestsService;
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
    private final TestsService testsService;
    private final HashMap<Long, BotState> botStateByUser = new HashMap<>();
    private final HashMap<BotState, Consumer<Update>> actionByBotState = new HashMap<>();
    private final HashMap<Long, HashMap<String, String>> infoByUser = new HashMap<>();
    private final HashMap<String, Consumer<Update>> actionByCommand = new HashMap<>();
    public final static Integer maxTestButtonsInTestsMenuRow = 4;
    public MtQuizBot(TelegramBotsApi telegramBotsApi,
                     @Value("${telegram.bot.username}") String botUsername,
                     @Value("${telegram.bot.token}") String botToken,
                     UserService userService,
                     GroupService groupService,
                     RoleService roleService,
                     TestsService testsService) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
        this.testsService = testsService;
        RegisterCommands();
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

    private void sendInlineMenu(Long who, String txt, InlineKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteMsg(Long who, Integer messageId) {
        var del = DeleteMessage.builder().chatId(who).messageId(messageId).build();
        try {
            execute(del);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTap(CallbackQuery query, String newTxtStr, InlineKeyboardMarkup newMenu) {
        var user = query.getFrom();
        var id = user.getId();
        var msgId = query.getMessage().getMessageId();
        
        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(query.getId()).build();
        try {
            execute(close);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    
        if (newTxtStr != null) {
            EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text(newTxtStr).build();
            try {
                execute(newTxt);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        if (newMenu != null) {
            EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                    .chatId(id.toString()).messageId(msgId).build();
            newKb.setReplyMarkup(newMenu);
            try {
                execute(newKb);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg;
        if (update.hasCallbackQuery()) {
            var callbackData = update.getCallbackQuery();
            var data = callbackData.getData();
            msg = new Message();
            buttonTap(callbackData, null, null);
            msg.setFrom(callbackData.getFrom());
            msg.setText(data);
            update.setMessage(msg);
        }
        msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();
        if (!infoByUser.containsKey(id))
            infoByUser.put(id, new HashMap<>());
        userService.insert(new User(id, user.getUserName(), null));
        if (!botStateByUser.containsKey(id))
            botStateByUser.put(id, BotState.idle);
        if (msg.hasText()) {
            var command = msg.getText().split(" ")[0];
            if (actionByCommand.containsKey(command)) {
                actionByCommand.get(command).accept(update);
                return;
            }
        }
        actionByBotState.get(botStateByUser.get(id)).accept(update);
    }

    private void RegisterCommands() {
        var methods = this.getClass().getDeclaredMethods();
        for (var method : methods) {
            boolean hasCommand = method.isAnnotationPresent(CommandAction.class);
            boolean hasActionByState = method.isAnnotationPresent(StateAction.class);
            if ((hasCommand || hasActionByState) && 
                method.getReturnType().equals(Void.TYPE) &&
                method.getParameterCount() == 1 &&
                method.getParameters()[0].getType().equals(Update.class)) {
                method.setAccessible(true);
                Consumer<Update> consumer= (Update upd) -> {
                    try { method.invoke(this,upd); }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
                if (hasCommand) {
                    String command = method.getAnnotation(CommandAction.class).value();
                    actionByCommand.put(command, consumer);
                }
                if (hasActionByState) {
                    BotState state = method.getAnnotation(StateAction.class).value();
                    actionByBotState.put(state, consumer);
                }
            }
        }
    }

    @StateAction(BotState.idle)
    private void BotIdle(Update update) {
    }

    @StateAction(BotState.waitingForGroupCode)
    private void BotWaitingForGroupCode(Update update) {
        var msg = update.getMessage();
        var id = update.getMessage().getFrom().getId();
        TestGroup group;
        if (msg.hasText()) {
            group = groupService.getById(msg.getText());
            if (group == null) {
                sendText(id, "Wrong group code");
                return;
            }
            var user = userService.getById(id);
            var role = roleService.getUserRole(user, group);
            if (role == null) {
                roleService.addUserRole(group, user, GroupRole.Participant);
            }
            userService.updateGroupById(id, group.getId());
        }
    }

    @StateAction(BotState.waitingForGroupName)
    private void BotWaingForGroupName(Update update) {
        var msg = update.getMessage();
        var id = update.getMessage().getFrom().getId();
        if (!msg.hasText()) {
            sendText(id, "No text for group name");
            return;
        }
        botStateByUser.replace(id, BotState.waitingForGroupDescription);
        if (infoByUser.get(id).containsKey("GROUP_NAME"))
            infoByUser.get(id).remove("GROUP_NAME");
        infoByUser.get(id).put("GROUP_NAME", msg.getText());
        sendText(id, "Please enter group description");
    }

    @StateAction(BotState.waitingForGroupDescription)
    private void BotWaitingForGroupDescription(Update update) {
        var msg = update.getMessage();
        var id = update.getMessage().getFrom().getId();
        if (!msg.hasText()) {
            sendText(id, "No text for group description");
            return;
        }
        botStateByUser.replace(id, BotState.idle);
        var group = groupService.create(infoByUser.get(id).get("GROUP_NAME"), msg.getText());
        userService.updateGroupById(id, group.getId());
        roleService.addUserRole(group, userService.getById(id), GroupRole.Owner);
        actionByCommand.get("/groupinfo").accept(update);
        infoByUser.get(id).remove("GROUP_NAME");
    }

    @StateAction(BotState.waitingForTestName)
    private void BotWaitingForTestName(Update update) {
        var msg = update.getMessage();
        var id = msg.getFrom().getId();
        if (!msg.hasText()) {
            sendText(id, "No text for test name");
            return;
        }
        if (infoByUser.get(id).containsKey("TEST_NAME"))
            infoByUser.get(id).remove("TEST_NAME");
        infoByUser.get(id).put("TEST_NAME", msg.getText());
        sendText(id, "Please enter a test description");
        botStateByUser.replace(id, BotState.waitingForTestDescription);
    }

    @StateAction(BotState.waitingForTestDescription)
    private void BotWaitingForTestDescription(Update update) {
        var msg = update.getMessage();
        var id = msg.getFrom().getId();
        if (!msg.hasText()) {
            sendText(id, "No text for test description");
            return;
        }
        var user = userService.getById(id);
        testsService.create(user,
            groupService.getUserGroup(user),
            infoByUser.get(id).get("TEST_NAME"),
            null,
            msg.getText()
        );
        botStateByUser.replace(id, BotState.idle);
        sendText(id,"Test created succesefully, go to /tests to add questions to your test");
    }

    @StateAction(BotState.waitingForNewTestProperty)
    private void botWaitingForNewProperty(Update update) {
        var msg = update.getMessage();
        var id = msg.getFrom().getId();
        var user = userService.getById(id);
        if (!msg.hasText()) {
            sendText(id, "No text");
            return;
        }
        var testId = infoByUser.get(user.getId()).get("TEST_TO_EDIT");
        var property = infoByUser.get(user.getId()).get("PROPERTY_TO_EDIT");
        var test = testsService.getById(testId);
        if (test == null ) {
            botStateByUser.replace(user.getId(), BotState.idle);
            sendText(user.getId(), "No test found, try againg :(");
        }
        try {
            testsService.updateTestProperty(test, property , msg.getText());
        } catch (NumberFormatException e) {
            sendText(user.getId(), "Oops... Something went wrong, maybe wrong input format?");
            return;
        } catch (NoSuchFieldException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
        var updatedTest = testsService.getById(testId);
        sendInlineMenu(id,
            testsService.getTestFullDescription(test) ,
            testsService.getEditMenu(updatedTest));
        botStateByUser.replace(user.getId(), BotState.idle);
    }

    @CommandAction("/creategroup")
    private void CreateGroupCommand(Update update) {
        var id = update.getMessage().getFrom().getId();
        botStateByUser.replace(id, BotState.waitingForGroupName);
        sendText(id, "Please enter a group name");
    }

    @CommandAction("/join")
    private void JoinCommand(Update update) {
        var id = update.getMessage().getFrom().getId();
        botStateByUser.replace(id, BotState.waitingForGroupCode);
        var group = groupService.getUserGroup(userService.getById(id));
        if (group != null)
            sendText(id, "Warning: you will leave your current group");
        sendText(id, "Please enter a group code ");
    }
    
    @CommandAction("/start")
    private void StartCommand(Update update) {
        var id = update.getMessage().getFrom().getId();
        var joinButton = InlineKeyboardButton.builder()
        .text("Join 👥")
        .callbackData("/join")
        .build();
        var createButton = InlineKeyboardButton.builder()
        .text("Create 👤")
        .callbackData("/creategroup")
        .build();
        var menu = InlineKeyboardMarkup.builder()
        .keyboardRow(List.of(joinButton,createButton))
        .build();
        sendInlineMenu(id, "" + 
        "Welcome to bot, enter command /join to join group " +
        "\n /creategroup to create one", menu);
    }
    
    @CommandAction("/groupinfo")
    private void GroupInfoCommand(Update update) {
        var id = update.getMessage().getFrom().getId();
        var user = userService.getById(id);
        var group = groupService.getUserGroup(user);
        if (group == null) {
            sendText(id, "No group found, please enter /join to enter a group or" +
            "\n /creategroup to create one");
            return;
        }
        var role = roleService.getUserRole(user, group);
        var testsButton = InlineKeyboardButton.builder()
        .text("Tests 🔴")
        .callbackData("/tests")
        .build();
        var createTestButton = InlineKeyboardButton.builder()
        .text("Create test ✅")
        .callbackData("/createtest")
        .build();
        var menu = InlineKeyboardMarkup.builder();
        menu.keyboardRow(List.of(testsButton));
        if (role == GroupRole.Owner || role == GroupRole.Contributor)
            menu.keyboardRow(List.of(createTestButton));
        sendInlineMenu(id, "Your group: " + 
        group.getName() + 
        " - " +
        group.getDescription() + 
        "\nWas created, its ID is\n" + group.getId() + 
        "\nPlease write it down",
        menu.build()
        );
    }

    @CommandAction("/tests")
    private void GetTestsCommand(Update update) {
        var id = update.getMessage().getFrom().getId();
        var user = userService.getById(id);
        var group = groupService.getUserGroup(user);
        if (group == null) {
            sendText(id, "No group found, please enter /join to enter a group or" +
            "\n /creategroup to create one");
            return;
        }
        var tests = testsService.getTestList(group);
        StringBuilder strB = new StringBuilder();
        List<InlineKeyboardButton> testButtons = new ArrayList();
        var menu = InlineKeyboardMarkup.builder();
        int buttonsInRowleft = maxTestButtonsInTestsMenuRow;
        int cnt = 0;
        strB.append("your groups tests:\n");
        for (var test : tests) {
            cnt++;
            strB.append(Integer.toString(cnt) + "): " + test.getName() + " - " + test.getDescription() + "\n");
            buttonsInRowleft--;
            testButtons.add(
                InlineKeyboardButton.builder()
                .callbackData("/test " + test.getId())
                .text(Integer.toString(cnt) + "✅")
                .build()
            );
            if (buttonsInRowleft == 0) {
                menu.keyboardRow(testButtons);
                testButtons = new ArrayList<>();
                buttonsInRowleft = maxTestButtonsInTestsMenuRow;
            }
        }
        if (buttonsInRowleft > 0)
            menu.keyboardRow(testButtons);
        sendInlineMenu(id, strB.toString(), menu.build());
    }

    @CommandAction("/createtest")
    private void CreateTestCommand(Update update) {
        var id = update.getMessage().getFrom().getId();
        var user = userService.getById(id);
        var group = groupService.getUserGroup(user);
        if (group == null) {
            sendText(id, "No group found, please enter /join to enter a group or" +
            "\n /creategroup to create one");
            return;
        }
        var role = roleService.getUserRole(user, group);
        if (role == GroupRole.Participant) {
            sendText(id, "You dont have rights to create tests");
            return;
        }
        botStateByUser.replace(id, BotState.waitingForTestName);
        sendText(id, "Please enter a test name");
    }

    @CommandAction("/test")
    private void TestMenuCommand(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var testId = query.getData().split(" ")[1];
        var test = testsService.getById(testId);
        var user = userService.getById(query.getFrom().getId());
        if (test == null) {
            sendText(user.getId(), "Sorry no such test");
            return;
        }
        var group = groupService.getById(test.getGroup_id());
        if (!group.getId().equals(user.getGroup_id()))
            sendText(user.getId(), "You are not a part of this group, sry I guess :(");
        var role = roleService.getUserRole(user, group);
        var menu = InlineKeyboardMarkup.builder();
        var startButton = InlineKeyboardButton.builder()
            .callbackData("/starttest " + test.getId())
            .text("Start test 🎓").build();
        menu.keyboardRow(List.of(startButton));
        if (role == GroupRole.Owner ||
            role == GroupRole.Contributor && test.getOwner_id() == user.getId()) {
            var editButton = InlineKeyboardButton.builder()
            .callbackData("/edittest " + test.getId())
            .text("Edit 📝").build();
            menu.keyboardRow(List.of(editButton));
        }
        buttonTap(query,
            testsService.getTestFullDescription(test),
            menu.build());
    }

    @CommandAction("/starttest")
    private void StartPassingTestMenuCommand(Update update) {
        if (!update.hasCallbackQuery())
            return;
    }

    @CommandAction("/edittest")
    private void EditTestMenuCommand(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var testId = args[1];
        var test = testsService.getById(testId);
        var user = userService.getById(query.getFrom().getId());
        if (test == null) {
            sendText(user.getId(), "Sorry no such test");
            return;
        }
        var group = groupService.getById(test.getGroup_id());
        if (!group.getId().equals(user.getGroup_id())) {
            sendText(user.getId(), "You are not a part of this group, sry I guess :(");
            return;
        }
        var role = roleService.getUserRole(user, group);
        if (role == GroupRole.Participant ||
            role == GroupRole.Contributor &&
            test.getOwner_id() != user.getId()) {
            sendText(user.getId(), "You have no rights to edit this test, sry I guess :(");
            return;
        }
        buttonTap(query,
            null,
            testsService.getEditMenu(test));
    }

    @CommandAction("/ststfield")
    private void setTestProperty(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var testId = args[1];
        var test = testsService.getById(testId);
        var user = userService.getById(query.getFrom().getId());
        deleteMsg(user.getId(), query.getMessage().getMessageId());
        if (test == null) {
            sendText(user.getId(), "Sorry no such test");
            return;
        }
        var group = groupService.getById(test.getGroup_id());
        if (!group.getId().equals(user.getGroup_id()))
            sendText(user.getId(), "You are not a part of this group, sry I guess :(");
        var role = roleService.getUserRole(user, group);
        if (role == GroupRole.Participant ||
            role == GroupRole.Contributor &&
            test.getOwner_id() != user.getId()) {
            sendText(user.getId(), "You have no rights to edit this test, sry I guess :(");
        }
        var property = args[2];
        botStateByUser.replace(user.getId(), BotState.waitingForNewTestProperty);
        infoByUser.get(user.getId()).put("TEST_TO_EDIT", test.getId());
        infoByUser.get(user.getId()).put("PROPERTY_TO_EDIT", property);
        sendText(user.getId(), "Please enter new property value");
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
