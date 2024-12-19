package com.ekocbiyik.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponse {

    private String message;
    private int code;
    private Date timestamp = new Date();

    public ExceptionResponse(String message, int code) {
        this.message = message;
        this.code = code;
        this.timestamp = new Date();
    }
}
