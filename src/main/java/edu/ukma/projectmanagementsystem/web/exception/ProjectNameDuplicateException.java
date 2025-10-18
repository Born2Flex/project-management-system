package edu.ukma.projectmanagementsystem.web.exception;

import org.springframework.http.HttpStatus;

public class ProjectNameDuplicateException extends BaseException {
    public ProjectNameDuplicateException() {
        super("A project with this name already exists for the current user", HttpStatus.CONFLICT);
    }
}
