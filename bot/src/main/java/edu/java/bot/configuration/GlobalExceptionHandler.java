package edu.java.bot.configuration;

import edu.java.bot.controller.interceptor.exception.TooManyRequestsException;
import edu.java.bot.controller.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class,
        HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handle(Exception ex, WebRequest request) {
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

    @ExceptionHandler({TooManyRequestsException.class})
    public ResponseEntity<ApiErrorResponse> handleTooManyRequestsException(
        TooManyRequestsException ex,
        WebRequest request
    ) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "Превышено допустимое число запросов. Попробуйте позже",
            String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(apiErrorResponse);
    }
}
