package com.in4mo.wallet.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

@Data
public class ErrorResponse {

    private String error;
    private String message;
    private int status;
    private String path;
    private Long timestamp;
    @JsonIgnore
    private final String uri = "uri=";

    public ErrorResponse(Exception exception, WebRequest request, HttpStatus status){
        this.error = exception.getClass().getSimpleName();
        this.message = exception.getMessage();
        this.status = status.value();
        this.path = getRequestPath(request);
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(Exception exception, String message, WebRequest request, HttpStatus status){
        this.error = exception.getClass().getSimpleName();
        this.message = message;
        this.status = status.value();
        this.path = getRequestPath(request);
        this.timestamp = System.currentTimeMillis();
    }

    private String getRequestPath(WebRequest request){
        return request.getDescription(false).replace(uri, "");
    }
}
