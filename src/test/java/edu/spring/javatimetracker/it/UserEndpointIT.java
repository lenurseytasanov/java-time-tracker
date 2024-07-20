package edu.spring.javatimetracker.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.spring.javatimetracker.controller.dto.UserDto;
import edu.spring.javatimetracker.db.repository.UserJpaRepository;
import edu.spring.javatimetracker.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserEndpointIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userJpaRepository.deleteAll();

        userJpaRepository.save(new User("default", "default", "default", "default"));
    }

    @Test
    public void createUserTest() throws Exception {
        UserDto userDto = new UserDto("username", "password", "firstname", "lastname");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        Optional<User> user = userJpaRepository.findByUsername(userDto.getUsername());
        assertTrue(user.isPresent());
        assertEquals(userDto.getUsername(), user.get().getUsername());
        assertEquals(userDto.getPassword(), user.get().getPassword());
        assertEquals(userDto.getFirstname(), user.get().getFirstname());
        assertEquals(userDto.getLastname(), user.get().getLastname());
    }

    @Test
    public void updateUserTest() throws Exception {
        UserDto userDto = new UserDto("username", "password", "firstname", "lastname");

        mockMvc.perform(put("/api/users/default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        Optional<User> user = userJpaRepository.findByUsername(userDto.getUsername());
        assertTrue(user.isPresent());
        assertEquals(userDto.getUsername(), user.get().getUsername());
        assertEquals(userDto.getPassword(), user.get().getPassword());
        assertEquals(userDto.getFirstname(), user.get().getFirstname());
        assertEquals(userDto.getLastname(), user.get().getLastname());
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/api/users/default"))
                .andExpect(status().isOk());

        assertTrue(userJpaRepository.findByUsername("default").isEmpty());
    }
}
