package edu.spring.javatimetracker.db.repository;

import edu.spring.javatimetracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
}
