package com.in4mo.wallet.controller;

import com.in4mo.wallet.exception.ErrorResponse;
import com.in4mo.wallet.exception.InvalidRequestException;
import com.in4mo.wallet.exception.RegistryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RegistryControllerExceptionHandler {

    @ExceptionHandler(RegistryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handle(RegistryNotFoundException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception, request, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception, WebRequest request) {
        List<String> errorMessages = new ArrayList<>();

        for (final FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }
        for (final ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }

        String message = String.join(", ", errorMessages);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception, message, request, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handle(InvalidRequestException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception, request, HttpStatus.BAD_REQUEST));
    }
}