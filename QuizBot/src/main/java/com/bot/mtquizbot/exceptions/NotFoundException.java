package com.bot.mtquizbot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseException {

    private final static String msg = "Not Found";

    public NotFoundException(Throwable t) {
        super(msg, t);
    }

    public NotFoundException() {
        super(msg);
    }
}