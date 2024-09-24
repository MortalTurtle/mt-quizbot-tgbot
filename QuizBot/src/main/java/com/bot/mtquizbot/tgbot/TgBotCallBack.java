package com.bot.mtquizbot.tgbot;
import java.util.HashMap;
import java.util.function.Function;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import com.bot.mtquizbot.service.GroupService;
import com.bot.mtquizbot.service.RoleService;
import com.bot.mtquizbot.service.UserService;

public class TgBotCallBack {
    private final UserService userService;
    private final GroupService groupService;
    private final RoleService roleService;
    private final HashMap<String, Function<CallbackQuery, String>> msgTextByCommand = new HashMap<>();
    private final HashMap<String, Function<CallbackQuery, InlineKeyboardMarkup>> menuByCommand = new HashMap<>();
    public TgBotCallBack(UserService userService,
                    GroupService groupService,
                    RoleService roleService) {
        this.userService = userService;
        this.groupService = groupService;
        this.roleService = roleService;
    }

    public String GetNewText(CallbackQuery query) {
        String command = query.getData().split(" ")[0];
        if (msgTextByCommand.containsKey(command))
            return msgTextByCommand.get(command).apply(query);
        return null;
    }
    
    public InlineKeyboardMarkup GetNewMenu(CallbackQuery query) {
        String command = query.getData().split(" ")[0];
        if (menuByCommand.containsKey(command))
            return menuByCommand.get(command).apply(query);
        return InlineKeyboardMarkup.builder().clearKeyboard().build();
    }

}
