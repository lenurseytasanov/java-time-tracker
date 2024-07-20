package edu.spring.javatimetracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorDto {

    private Integer code;

    private String title;

    private String details;
}
