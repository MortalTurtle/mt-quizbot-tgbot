package com.bot.mtquizbot.service;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.bot.mtquizbot.exceptions.NotFoundException;
import com.bot.mtquizbot.models.CanEditObjectField;
import com.bot.mtquizbot.models.IModel;

public class BaseService {
    public <T> T wrapResult(T result) {
        if(result == null)
            throw new NotFoundException();
        return result;
    }
    public <T> List<T> wrapResults(List<T> result) {
        if(result == null || result.size() == 0)
            throw new NotFoundException();
        return result;
    }

    public static InlineKeyboardMarkupBuilder getEditMenuBuilder(IModel obj, String command) {
        var fields = obj.getClass().getDeclaredFields();
        var menu = InlineKeyboardMarkup.builder();
        for (var field : fields) {
            if (field.isAnnotationPresent(CanEditObjectField.class)) {
                field.setAccessible(true);
                var annotation = field.getAnnotation(CanEditObjectField.class);
                menu.keyboardRow(List.of(InlineKeyboardButton.builder()
                .text(annotation.getPropertyButtonText())
                .callbackData(command + " " + obj.getId() + " " + field.getName())
                .build()
                ));
            }
        }
        return menu;
    }
}