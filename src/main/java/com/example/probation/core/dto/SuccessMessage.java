package com.example.probation.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessMessage {
    private static final String SUCCESS_MESSAGE = "success";

    private String message = SUCCESS_MESSAGE;
}
