package edu.spring.javatimetracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeIntervalDto {

    private OffsetDateTime startedAt;

    private OffsetDateTime finishedAt;

    private String taskDescription;
}
