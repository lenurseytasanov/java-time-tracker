package edu.spring.javatimetracker.service;

import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.User;
import edu.spring.javatimetracker.util.exception.NotFoundException;
import edu.spring.javatimetracker.util.exception.ResourceExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserJpaRepository userRepository;

    private static final String USER_NOT_FOUND = "User '%s' not found";

    @Transactional
    public User createUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(user1 -> {
            throw new ResourceExistsException("User '%s' already exists".formatted(user.getUsername()));
        });
        User result = userRepository.save(user);
        log.info("Saved user '{}' with id '{}'", user.getUsername(), user.getId());
        return result;
    }

    @Transactional
    public User updateUser(String username, User user) {
        User oldUser = userRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.formatted(username)));
        user.setId(oldUser.getId());
        User result = userRepository.save(user);
        log.info("User's '{}' info updated", result.getUsername());
        return result;
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND.formatted(username)));
        userRepository.delete(user);
        log.info("User '{}'", username);
    }
}
