package me.study.mylog.common.exception;

import org.springframework.http.HttpStatus;

public class CustomFileUploadException extends CustomApiException {
    public CustomFileUploadException(String message) {
        super(message);
    }
    public CustomFileUploadException(String message, HttpStatus status) {
        super(message, status);
    }
}
