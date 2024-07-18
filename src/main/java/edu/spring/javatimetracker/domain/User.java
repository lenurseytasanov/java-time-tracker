package edu.spring.javatimetracker.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@RequiredArgsConstructor
public class User {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private final String username;

    @Column(nullable = false)
    private final String password;

    @Column(nullable = false)
    private final String firstname;

    @Column(nullable = false)
    private final String lastname;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final Set<Task> tasks = new LinkedHashSet<>();

    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(tasks);
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setAssignee(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setAssignee(null);
    }
}
