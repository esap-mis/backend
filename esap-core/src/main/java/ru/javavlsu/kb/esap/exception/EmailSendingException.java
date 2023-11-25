package ru.javavlsu.kb.esap.exception;

public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String msg) {
        super(msg);
    }
}
