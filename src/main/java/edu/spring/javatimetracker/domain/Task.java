package edu.spring.javatimetracker.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    private final String description;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime startedAt;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime finishedAt;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private final User assignee;

    public void start() {
        if (startedAt != null || finishedAt != null) {
            throw new RuntimeException();
        }
        startedAt = LocalDateTime.now(ZoneId.of("Z"));
    }

    public void finish() {
        if (startedAt == null || finishedAt != null)  {
            throw new RuntimeException();
        }
        finishedAt = LocalDateTime.now(ZoneId.of("Z"));
    }
}
