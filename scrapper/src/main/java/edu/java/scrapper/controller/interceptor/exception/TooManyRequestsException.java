package edu.java.scrapper.controller.interceptor.exception;

public class TooManyRequestsException extends RuntimeException {

    private static final String ERROR_MESSAGE = "По ip-адресу %s превышено число запросов в минуту!";

    public TooManyRequestsException(String ip) {
        super(String.format(ERROR_MESSAGE, ip));
    }
}
