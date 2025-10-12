package edu.ukma.projectmanagementsystem.web.exception;

import org.springframework.http.HttpStatus;

public class UsernameDuplicateException extends BaseException {
    public UsernameDuplicateException() {
        super("User with such username already exists", HttpStatus.CONFLICT);
    }
}