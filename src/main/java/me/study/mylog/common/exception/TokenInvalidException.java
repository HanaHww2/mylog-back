package me.study.mylog.common.exception;

public class TokenInvalidException extends RuntimeException {

    public TokenInvalidException() {
        super("Failed to generate Token.");
    }

    private TokenInvalidException(String message) {
        super(message);
    }
}
