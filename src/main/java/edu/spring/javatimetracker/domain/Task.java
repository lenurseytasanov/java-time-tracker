package edu.spring.javatimetracker.domain;

import edu.spring.javatimetracker.util.exception.TaskNotCreatedException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Clock;
import java.time.OffsetDateTime;

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

    public void start(Clock clock) {
        if (startedAt != null || finishedAt != null) {
            throw new TaskNotCreatedException("Task '%s' already started".formatted(description));
        }
        startedAt = OffsetDateTime.now(clock);
    }

    public void finish(Clock clock) {
        if (startedAt == null || finishedAt != null)  {
            throw new TaskNotCreatedException("Task '%s' already finished or not started".formatted(description));
        }
        finishedAt = OffsetDateTime.now(clock);
    }
}
