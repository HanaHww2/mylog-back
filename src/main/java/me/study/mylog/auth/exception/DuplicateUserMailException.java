package me.study.mylog.auth.exception;

public class DuplicateUserMailException extends RuntimeException {

    public DuplicateUserMailException() {
        super();
    }
    public DuplicateUserMailException(String message, Throwable cause) {
        super(message, cause);
    }
    public DuplicateUserMailException(String message) {
        super(message);
    }
    public DuplicateUserMailException(Throwable cause) {
        super(cause);
    }
}
