package edu.spring.javatimetracker.service;

import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userRepository;

    @Transactional
    public User createUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(user1 -> { throw new RuntimeException(); });
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(String username, User user) {
        User oldUser = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        user.setId(oldUser.getId());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
        userRepository.delete(user);
    }
}
