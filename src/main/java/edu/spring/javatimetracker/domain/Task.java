package edu.spring.javatimetracker.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Task {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String description;

    private OffsetDateTime startedAt;

    private OffsetDateTime finishedAt;

    @Setter(AccessLevel.PACKAGE)
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    public Task(String description, OffsetDateTime startedAt, OffsetDateTime finishedAt) {
        this.description = description;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public Task(String description) {
        this.description = description;
    }

    public void start() {
        if (startedAt != null || finishedAt != null) {
            throw new RuntimeException();
        }
        startedAt = OffsetDateTime.now(ZoneId.of("Z"));
    }

    public void finish() {
        if (startedAt == null || finishedAt != null)  {
            throw new RuntimeException();
        }
        finishedAt = OffsetDateTime.now(ZoneId.of("Z"));
    }
}
