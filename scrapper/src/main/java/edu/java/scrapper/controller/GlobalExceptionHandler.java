package edu.java.scrapper.controller;

import edu.java.scrapper.controller.chatApi.exception.ChatAlreadyExistsException;
import edu.java.scrapper.controller.chatApi.exception.ChatNotFoundException;
import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.controller.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class,
        HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
        MissingRequestHeaderException.class})
    public ResponseEntity<ApiErrorResponse> handleInputErrors(Exception ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Некорректные параметры запроса",
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return ResponseEntity.badRequest()
            .body(apiErrorResponse);
    }

    @ExceptionHandler({ChatNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleChatNotFoundException(ChatNotFoundException ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Чат не существует",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(apiErrorResponse);
    }

    @ExceptionHandler({ChatNotExistsException.class})
    public ResponseEntity<ApiErrorResponse> handleChatNotExistsException(
        ChatNotExistsException ex,
        WebRequest request
    ) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Нельзя получить доступ к чату",
            String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(apiErrorResponse);
    }

    @ExceptionHandler({ChatAlreadyExistsException.class})
    public ResponseEntity<ApiErrorResponse> handleChatAlreadyExistsException(
        ChatAlreadyExistsException ex,
        WebRequest request
    ) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Чат уже зарегистрирован",
            String.valueOf(HttpStatus.CONFLICT.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(apiErrorResponse);
    }

    @ExceptionHandler({LinkAlreadyExistsException.class})
    public ResponseEntity<ApiErrorResponse> handleLinkAlreadyExistsException(
        LinkAlreadyExistsException ex,
        WebRequest request
    ) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Ссылка уже добавлена",
            String.valueOf(HttpStatus.CONFLICT.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(apiErrorResponse);
    }

    @ExceptionHandler({LinkNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleLinkNotFoundException(LinkNotFoundException ex, WebRequest request) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Ссылка не найдена",
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(apiErrorResponse);
    }
}
