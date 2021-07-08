package com.example.probation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorsWrapper {
    private String field;
    private String message;
}
