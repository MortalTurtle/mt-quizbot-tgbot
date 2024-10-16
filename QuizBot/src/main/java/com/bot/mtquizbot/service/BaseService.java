package com.bot.mtquizbot.service;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.bot.mtquizbot.exceptions.NotFoundException;
import com.bot.mtquizbot.models.CanEditObjectField;
import com.bot.mtquizbot.models.IModel;

public class BaseService {
    private final HashMap<Class<?>, Function<String, Object>> convertStringValueToSomeClass = new HashMap<>();

    public BaseService() {
        convertStringValueToSomeClass.put(String.class, (String str) -> str);
        convertStringValueToSomeClass.put(Integer.class, (String str) -> Integer.valueOf(str));
    }

    public <T> T wrapResult(T result) {
        if(result == null)
            throw new NotFoundException();
        return result;
    }
    public <T> List<T> wrapResults(List<T> result) {
        if(result == null || result.isEmpty())
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

    protected void setNewFieldValueFromString(IModel model, String fieldName, String value) throws NoSuchFieldException, NumberFormatException  {
        var field = model.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        var fieldType = field.getType();
        try {
            field.set(model, convertStringValueToSomeClass.get(fieldType).apply(value));
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}