package com.bot.mtquizbot.tgbot;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.bot.mtquizbot.service.TestQuestionService;
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
    private final TestQuestionService questionsService;
    private final HashMap<Long, BotState> botStateByUser = new HashMap<>();
    private final HashMap<BotState, Consumer<Update>> actionByBotState = new HashMap<>();
    private final HashMap<Long, HashMap<IntermediateVariable, String>> intermediateInfoByUser = new HashMap<>();
    private final HashMap<String, Consumer<Update>> actionByCommand = new HashMap<>();
    private final static Integer MAX_TEST_BUTTONS_IN_TESTS_MENU_ROW = 4;
    private final static Integer MAX_BUTTONS_IN_QUESTIONS_MENU_ROW = 6;
    private final static Integer MAX_QUESTIONS_IN_MENU = 20;
    private final static Integer MAX_QUESTIONS_TYPES_IN_MENU_ROW = 3;
    public MtQuizBot(TelegramBotsApi telegramBotsApi,
                     @Value("${telegram.bot.username}") String botUsername,
                     @Value("${telegram.bot.token}") String botToken,
                     UserService userService,
                     GroupService groupService,
                     RoleService roleService,
                     TestsService testsService,
                     TestQuestionService questionService) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
        this.testsService = testsService;
        this.questionsService = questionService;
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
        if (!intermediateInfoByUser.containsKey(id))
            intermediateInfoByUser.put(id, new HashMap<>());
        userService.insert(new User(Long.toString(id), user.getUserName(), null));
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
        if (intermediateInfoByUser.get(id).containsKey(IntermediateVariable.GROUP_NAME))
            intermediateInfoByUser.get(id).remove(IntermediateVariable.GROUP_NAME);
        intermediateInfoByUser.get(id).put(IntermediateVariable.GROUP_NAME, msg.getText());
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
        var group = groupService.create(
            intermediateInfoByUser.get(id).get(IntermediateVariable.GROUP_NAME),
            msg.getText());
        userService.updateGroupById(id, group.getId());
        roleService.addUserRole(group, userService.getById(id), GroupRole.Owner);
        actionByCommand.get("/groupinfo").accept(update);
        intermediateInfoByUser.get(id).remove(IntermediateVariable.GROUP_NAME);
    }

    @StateAction(BotState.waitingForTestName)
    private void BotWaitingForTestName(Update update) {
        var msg = update.getMessage();
        var id = msg.getFrom().getId();
        if (!msg.hasText()) {
            sendText(id, "No text for test name");
            return;
        }
        if (intermediateInfoByUser.get(id).containsKey(IntermediateVariable.TEST_NAME))
            intermediateInfoByUser.get(id).remove(IntermediateVariable.TEST_NAME);
        intermediateInfoByUser.get(id).put(IntermediateVariable.TEST_NAME, msg.getText());
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
            intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.TEST_NAME),
            null,
            msg.getText()
        );
        intermediateInfoByUser.get(user.getLongId()).remove(IntermediateVariable.TEST_NAME);
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
        var testId = intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.TEST_TO_EDIT);
        var property = intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.TEST_PROPERTY_TO_EDIT);
        intermediateInfoByUser.get(user.getLongId()).remove(IntermediateVariable.TEST_PROPERTY_TO_EDIT);
        intermediateInfoByUser.get(user.getLongId()).remove(IntermediateVariable.TEST_TO_EDIT);
        var test = testsService.getById(testId);
        if (test == null ) {
            botStateByUser.replace(user.getLongId(), BotState.idle);
            sendText(user.getLongId(), "No test found, try againg :(");
        }
        try {
            testsService.updateTestProperty(test, property , msg.getText());
        } catch (NumberFormatException e) {
            sendText(user.getLongId(), "Oops... Something went wrong, maybe wrong input format?");
            return;
        } catch (NoSuchFieldException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
        var updatedTest = testsService.getById(testId);
        sendInlineMenu(id,
            testsService.getTestFullDescription(test) ,
            testsService.getEditMenu(updatedTest));
        botStateByUser.replace(user.getLongId(), BotState.idle);
    }

    @StateAction(BotState.waitingForQuestionText)
    private void botWaitingForQuestionText(Update update) {
        var msg = update.getMessage();
        var id = msg.getFrom().getId();
        var user = userService.getById(id);
        if (!msg.hasText()) {
            sendText(id, "No text");
            return;
        }
        var questionText = msg.getText();
        var testId = intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.TEST_TO_EDIT);
        var questionType = intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.QUESTION_TYPE);
        questionsService.addQuestion(testId ,questionType, 0, questionText);
        botStateByUser.replace(id, BotState.idle);
        sendText(id, "Question added go to your test to edit");
    }

    @StateAction(BotState.waitingForNewQuestionProperty)
    private void botWatitingForNewQuestionProperty(Update update) {
        var msg = update.getMessage();
        var id = msg.getFrom().getId();
        var user = userService.getById(id);
        if (!msg.hasText()) {
            sendText(id, "No text");
            return;
        }
        var propertyVal = msg.getText();
        var questionId = intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.QUESTION_TO_EDIT);
        var question = questionsService.getQuestionById(questionId);
        if (question == null) {
            sendText(user.getLongId(), "Ooops... somethig went wrong :(" );
            return;
        }
        botStateByUser.replace(id, BotState.idle);
        var questionFieldName = intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.QUESTION_PROPERTY_TO_EDIT);
        questionsService.updateQuestionProperty(question, questionFieldName, propertyVal);
        var menuB = questionsService.getQuestionEditMenu(question);
        sendInlineMenu(id, questionsService.getQuestionDescriptionMessage(question), menuB.build());
    }

    @StateAction(BotState.waitingForNewFalseAnswer)
    private void botWaitingForNewFalseAnswer(Update update) {
        var msg = update.getMessage();
        var id = msg.getFrom().getId();
        var user = userService.getById(id);
        if (!msg.hasText()) {
            sendText(id, "No text");
            return;
        }
        var ans = msg.getText();
        var questionId = intermediateInfoByUser.get(user.getLongId()).get(IntermediateVariable.QUESTION_TO_EDIT);
        questionsService.addFalseAnswer(questionsService.getQuestionById(questionId), ans);
        var question = questionsService.getQuestionById(questionId);
        var msgstrB = new StringBuilder();
        msgstrB.append(questionsService.getQuestionDescriptionMessage(question));
        msgstrB.append("\n");
        msgstrB.append(questionsService.getFalseAnswersString(question));
        var menu = InlineKeyboardMarkup.builder().keyboardRow(
            List.of(InlineKeyboardButton.builder()
                    .text("Add false answer ⭕️")
                    .callbackData("/addfalseanswer " + questionId)
                    .build()
            )
        );
        botStateByUser.replace(id, BotState.idle);
        sendInlineMenu(user.getLongId(), msgstrB.toString(), menu.build());
    }


    @StateAction(BotState.waitingForQuestionsAnswer)
    private void botWaitingForQuestionsAnswer(Update update) {

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
        var user = userService.getById(id);
        var group = groupService.getUserGroup(user);
        var textMsg = "" + 
        "Welcome to bot, enter command /join to join group " +
        "\n /creategroup to create one";
        if (group != null)
            textMsg += "\nYou have a group :), type /groupinfo to see info";
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
        sendInlineMenu(id, textMsg , menu);
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
        var testsDescriptionWithMenu = testsService.getGroupTestsMenuWithDescription(group, MAX_TEST_BUTTONS_IN_TESTS_MENU_ROW);
        sendInlineMenu(id, testsDescriptionWithMenu.getSecond(), testsDescriptionWithMenu.getFirst());
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
            sendText(user.getLongId(), "Sorry no such test");
            return;
        }
        var group = groupService.getById(test.getGroup_id());
        if (!group.getId().equals(user.getGroup_id()))
            sendText(user.getLongId(), "You are not a part of this group, sry I guess :(");
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
        var query = update.getCallbackQuery();
        if (!update.hasCallbackQuery()) {
            return;
        }
        var args = query.getData().split(" ");
        var testId = args[1];
        var test = testsService.getById(testId);
        var user = userService.getById(query.getFrom().getId());
        if (test == null) {
            sendText(user.getLongId(), "Sorry, no such test.");
            return;
        }
        var questions = questionsService.getQuestionsByTestId(test.getId(), 0, MAX_QUESTIONS_IN_MENU);
        if (questions == null) {
            sendText(user.getLongId(), "There are no questions in the test.");
            return;
        }
        var group = groupService.getById(test.getGroup_id());
        if (!group.getId().equals(user.getGroup_id())) {
            sendText(user.getLongId(), "You are not part of this group.");
            return;
        }
        var questionIndex = 0;
        var question = questions.get(questionIndex);
        var questionText = question.getText();
        var questionType = question.getTypeId();
        
        botStateByUser.replace(user.getLongId(), BotState.waitingForQuestionsAnswer);

        if ("Choose".equals(questionType)) {
            var answers = questionsService.getFalseAnswersStringList(question);
            answers.add(question.getAnswer()); 
            Collections.shuffle(answers); 
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            for (String answer : answers) {
                var button = InlineKeyboardButton.builder()
                        .text(answer)
                        .callbackData(answer) 
                        .build();
                rows.add(Collections.singletonList(button));
            }
            var keyboard = InlineKeyboardMarkup.builder().keyboard(rows).build();
            sendInlineMenu(user.getLongId(), questionText, keyboard);
        } else {
            sendText(user.getLongId(), questionText + "\nPlease enter your answer.");
        }
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
            sendText(user.getLongId(), "Sorry no such test");
            return;
        }
        var group = groupService.getById(test.getGroup_id());
        if (!group.getId().equals(user.getGroup_id())) {
            sendText(user.getLongId(), "You are not a part of this group, sry I guess :(");
            return;
        }
        var role = roleService.getUserRole(user, group);
        if (role == GroupRole.Participant ||
            role == GroupRole.Contributor &&
            test.getOwner_id() != user.getId()) {
            sendText(user.getLongId(), "You have no rights to edit this test, sry I guess :(");
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
        deleteMsg(user.getLongId(), query.getMessage().getMessageId());
        if (test == null) {
            sendText(user.getLongId(), "Sorry no such test");
            return;
        }
        var group = groupService.getById(test.getGroup_id());
        if (!group.getId().equals(user.getGroup_id()))
            sendText(user.getLongId(), "You are not a part of this group, sry I guess :(");
        var role = roleService.getUserRole(user, group);
        if (role == GroupRole.Participant ||
            role == GroupRole.Contributor &&
            test.getOwner_id() != user.getId()) {
            sendText(user.getLongId(), "You have no rights to edit this test, sry I guess :(");
            return;
        }
        var property = args[2];
        botStateByUser.replace(user.getLongId(), BotState.waitingForNewTestProperty);
        intermediateInfoByUser.get(user.getLongId()).put(IntermediateVariable.TEST_TO_EDIT, test.getId());
        intermediateInfoByUser.get(user.getLongId()).put(IntermediateVariable.TEST_PROPERTY_TO_EDIT, property);
        sendText(user.getLongId(), "Please enter new property value");
    }

    @CommandAction("/backtotests")
    private void backToTests(Update update) {
        deleteMsg(update.getMessage().getFrom().getId(), update.getMessage().getMessageId());
        actionByCommand.get("/tests").accept(update);
    }

    @CommandAction("/editquestions")
    private void editTestQuestions(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var testId = args[1];
        var user = userService.getById(query.getFrom().getId());
        Boolean hasOffsetParameter = args.length >= 3;
        var test = testsService.getById(testId);
        if (test == null) {
            sendText(user.getLongId(), "Sorry no such test");
            return;
        }
        var offset = hasOffsetParameter ? Integer.parseInt(args[2]) : 0;
        var questions = questionsService.getQuestionsByTestId(test.getId(), offset, MAX_QUESTIONS_IN_MENU);
        var menu = questionsService.getQuestionsMenuBuilder(questions, MAX_BUTTONS_IN_QUESTIONS_MENU_ROW);
        var nextPageButton = InlineKeyboardButton.builder()
            .text("⏩")
            .callbackData("/editquestions " + test.getId() + " " + Integer.toString(offset + MAX_QUESTIONS_IN_MENU))
            .build();
        List<InlineKeyboardButton> list = new ArrayList<>();
        if (hasOffsetParameter) {
            var prevPageButton = InlineKeyboardButton.builder()
                .text("⏪")
                .callbackData("editquestions " + 
                    test.getId() + 
                    (offset == MAX_QUESTIONS_IN_MENU ? "" : " " + Integer.toString(offset - MAX_QUESTIONS_IN_MENU)))
                .build();
            list.add(prevPageButton);
        }
        if (questions.size() == MAX_QUESTIONS_IN_MENU)
            list.add(nextPageButton);
        menu.keyboardRow(list);
        var addQuestionButton = InlineKeyboardButton.builder()
            .text("Add ❓")
            .callbackData("/addquestion " + testId)
            .build();
        var textMsg = questionsService.getQuestionDescriptionMessage(questions);
        if (textMsg.equals(""))
            textMsg = "No questions found, maybe add some ^-^";
        menu.keyboardRow(List.of(addQuestionButton));
        buttonTap(query,
            textMsg,
            menu.build());
    }

    @CommandAction("/editquestion")
    private void editTestQuestion(Update update) {
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var questionId = args[1];
        var user = userService.getById(query.getFrom().getId());
        var question = questionsService.getQuestionById(questionId);
        if (question == null) {
            sendText(user.getLongId(), "No such question found, maybe something went wrong :(");
            return;
        }
        var menuB = questionsService.getQuestionEditMenu(question);
        buttonTap(query,
            questionsService.getQuestionDescriptionMessage(question),
            menuB.build()
        );
    }
    
   
    @CommandAction("/setqfield")
    private void editQuestion(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var questionId = args[1];
        var user = userService.getById(query.getFrom().getId());
        var question = questionsService.getQuestionById(questionId);
        if (question == null || args.length <= 2) {
            sendText(user.getLongId(), "Oops... something went wrong :(");
            return;
        }
        var field = args[2];
        deleteMsg(user.getLongId(), query.getMessage().getMessageId());
        intermediateInfoByUser.get(user.getLongId())
            .put(IntermediateVariable.QUESTION_PROPERTY_TO_EDIT, field);
        intermediateInfoByUser.get(user.getLongId())
            .put(IntermediateVariable.QUESTION_TO_EDIT, questionId);
        sendText(user.getLongId(), "Please enter a new value");
        botStateByUser.replace(user.getLongId(), BotState.waitingForNewQuestionProperty);
    }

    @CommandAction("/falseanswers")
    private void getFalseAnswers(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var questionId = args[1];
        var user = userService.getById(query.getFrom().getId());
        var question = questionsService.getQuestionById(questionId);
        if (question == null) {
            sendText(user.getLongId(), "No question found, something went wrong");
            return;
        }
        var msgstrB = new StringBuilder();
        msgstrB.append(questionsService.getQuestionDescriptionMessage(question));
        msgstrB.append("\n");
        msgstrB.append(questionsService.getFalseAnswersString(question));
        var menu = InlineKeyboardMarkup.builder().keyboardRow(
            List.of(InlineKeyboardButton.builder()
                    .text("Add false answer ⭕️")
                    .callbackData("/addfalseanswer " + questionId)
                    .build()
            )
        );
        buttonTap(query, msgstrB.toString(), menu.build());
    }

    @CommandAction("/addfalseanswer")
    private void addFalseAnswer(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var questionId = args[1];
        var user = userService.getById(query.getFrom().getId());
        var question = questionsService.getQuestionById(questionId);
        if (question == null) {
            sendText(user.getLongId(), "No question found, something went wrong");
            return;
        }
        deleteMsg(user.getLongId(), query.getMessage().getMessageId());
        sendText(user.getLongId(), "Enter new false qustion");
        botStateByUser.replace(user.getLongId(), BotState.waitingForNewFalseAnswer);
        intermediateInfoByUser.get(user.getLongId()).put(IntermediateVariable.QUESTION_TO_EDIT, questionId);
    }


    @CommandAction("/addquestion")
    private void addTestQuestion(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var testId = args[1];
        var test = testsService.getById(testId);
        var user = userService.getById(query.getFrom().getId());
        if (test == null) {
            sendText(user.getLongId(), "Sorry no such test");
            return;
        }
        var types = questionsService.getQuestionTypeList();
        var menu = questionsService.getQuestionTypeMenuBuilder(
            types,
            MAX_QUESTIONS_TYPES_IN_MENU_ROW);
        var info = intermediateInfoByUser.get(user.getLongId());
        info.put(IntermediateVariable.TEST_TO_EDIT, test.getId());
        buttonTap(query, questionsService.getQuestionTypeDescriptionMessage(types), menu.build());
    }

    @CommandAction("/addquestionstagetype")
    private void addTestQuestionTypeSelected(Update update) {
        if (!update.hasCallbackQuery())
            return;
        var query = update.getCallbackQuery();
        var args = query.getData().split(" ");
        var typeId = args[1];
        var user = userService.getById(query.getFrom().getId());
        deleteMsg(user.getLongId(), query.getMessage().getMessageId());
        intermediateInfoByUser.get(user.getLongId()).put(IntermediateVariable.QUESTION_TYPE, typeId);
        sendText(user.getLongId(), "Please enter a question text");
        botStateByUser.replace(user.getLongId(), BotState.waitingForQuestionText);
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
