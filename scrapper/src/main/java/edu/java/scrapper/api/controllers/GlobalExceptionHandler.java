package edu.java.scrapper.api.controllers;

import edu.java.scrapper.api.controllers.exceptions.ChatAlreadyExistsException;
import edu.java.scrapper.api.controllers.exceptions.ChatNotExistsException;
import edu.java.scrapper.api.controllers.exceptions.ChatNotFoundException;
import edu.java.scrapper.api.controllers.exceptions.LinkAlreadyExistsException;
import edu.java.scrapper.api.controllers.exceptions.LinkNotFoundException;
import edu.java.scrapper.api.schemas.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class,
        HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
        MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInputErrors(Exception ex) {
        return new ApiErrorResponse(
            "Некорректные параметры запроса",
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler({ChatNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleChatNotFoundException(ChatNotFoundException ex) {
        return new ApiErrorResponse(
            "Чат не существует",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler({ChatNotExistsException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiErrorResponse handleChatNotExistsException(ChatNotExistsException ex) {
        return new ApiErrorResponse(
            "Нельзя получить доступ к чату",
            String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler({ChatAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleChatAlreadyExistsException(ChatAlreadyExistsException ex) {
        return new ApiErrorResponse(
            "Чат уже зарегистрирован",
            String.valueOf(HttpStatus.CONFLICT.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler({LinkAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleLinkAlreadyExistsException(LinkAlreadyExistsException ex) {
        return new ApiErrorResponse(
            "Ссылка уже добавлена",
            String.valueOf(HttpStatus.CONFLICT.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }

    @ExceptionHandler({LinkNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleLinkNotFoundException(LinkNotFoundException ex) {
        return new ApiErrorResponse(
            "Ссылка не найдена",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
    }
}
