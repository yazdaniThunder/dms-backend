package com.sima.dms.errors.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(requiredProperties = {"message"})
public class ApiError {

    private final String message;
    private final Integer status;

    public ApiError(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public ApiError(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }
}