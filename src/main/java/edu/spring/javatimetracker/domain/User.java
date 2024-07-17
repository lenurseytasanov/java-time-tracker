package edu.spring.javatimetracker.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private final UUID id;

    private final String username;

    private final String password;

    private final String firstname;

    private final String lastname;

    @OneToMany(mappedBy = "assignee")
    private final Set<Task> tasks = new LinkedHashSet<>();
}
