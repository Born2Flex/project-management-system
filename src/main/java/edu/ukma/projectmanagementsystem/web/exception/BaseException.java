package edu.ukma.projectmanagementsystem.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus code;

    protected BaseException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}