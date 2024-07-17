package edu.spring.javatimetracker.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private final UUID id;

    private final String description;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime startedAt;

    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime endedAt;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private final User assignee;

    public void start() {
        if (startedAt != null || endedAt != null) {
            throw new RuntimeException();
        }
        startedAt = LocalDateTime.now(ZoneId.of("Z"));
    }

    public void finish() {
        if (startedAt == null || endedAt != null)  {
            throw new RuntimeException();
        }
        endedAt = LocalDateTime.now(ZoneId.of("Z"));
    }
}
