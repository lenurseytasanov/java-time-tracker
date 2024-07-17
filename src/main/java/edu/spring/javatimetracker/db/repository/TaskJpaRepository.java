package edu.spring.javatimetracker.db.repository;

import edu.spring.javatimetracker.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<Task, UUID> {
}
