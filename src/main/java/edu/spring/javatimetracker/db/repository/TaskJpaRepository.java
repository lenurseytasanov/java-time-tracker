package edu.spring.javatimetracker.db.repository;

import edu.spring.javatimetracker.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskJpaRepository extends JpaRepository<Task, UUID> {
}
