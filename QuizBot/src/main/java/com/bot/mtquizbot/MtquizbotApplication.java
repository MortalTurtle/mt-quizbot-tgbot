package com.bot.mtquizbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class MtquizbotApplication {
    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(MtquizbotApplication.class, args);
    }

}
