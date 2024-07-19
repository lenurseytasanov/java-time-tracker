package edu.spring.javatimetracker.service;

import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.User;
import edu.spring.javatimetracker.util.exception.NotFoundException;
import edu.spring.javatimetracker.util.exception.ResourceExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserService userService;

    private User user1;

    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User("username_1", "pass", "name", "name");
        user1.setId(UUID.randomUUID());
        user2 = new User("username_2", "pass", "name", "name");
        user2.setId(UUID.randomUUID());
        when(userJpaRepository.findByUsername("username_1")).thenReturn(Optional.of(user1));
        when(userJpaRepository.findByUsername("username_2")).thenReturn(Optional.empty());
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Если имя пользователя существует - исключение</li>
     *     <li>Если нет - сохранить пользователя в бд</li>
     * </ul>
     */
    @Test
    public void createUserTest() {
        Exception ex = assertThrows(ResourceExistsException.class, () -> userService.createUser(user1));
        assertEquals("User 'username_1' already exists", ex.getMessage());

        userService.createUser(user2);
        verify(userJpaRepository).save(eq(user2));
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>Новый пользователь имеет тот же id, что и старый</li>
     *     <li>Новый пользователь сохранен в бд</li>
     *     <li>Исключение, если пользователь с данным именем не найден</li>
     * </ul>
     */
    @Test
    public void updateUserTest() {
        userService.updateUser("username_1", user2);
        assertEquals(user1.getId(), user2.getId());
        verify(userJpaRepository).save(user2);

        Exception ex = assertThrows(NotFoundException.class, () -> userService.updateUser("username_2", user2));
        assertEquals("User 'username_2' not found", ex.getMessage());
    }

    /**
     * Проверяется:
     * <ul>
     *     <li>пользователь удален из бд</li>
     *     <li>Исключение, если пользователь с данным именем не найден</li>
     * </ul>
     */
    @Test
    public void deleteUserTest() {
        userService.deleteUser("username_1");
        verify(userJpaRepository).delete(user1);

        Exception ex = assertThrows(NotFoundException.class, () -> userService.deleteUser("username_2"));
        assertEquals("User 'username_2' not found", ex.getMessage());
    }
}
