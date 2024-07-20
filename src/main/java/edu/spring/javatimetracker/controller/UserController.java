package edu.spring.javatimetracker.controller;

import edu.spring.javatimetracker.controller.dto.UserDto;
import edu.spring.javatimetracker.domain.User;
import edu.spring.javatimetracker.service.UserService;
import edu.spring.javatimetracker.util.validation.Username;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto userDto) {
        User user = new User(userDto.getUsername(), userDto.getPassword(),
                userDto.getFirstname(), userDto.getLastname());

        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Void> updateUser(
            @Username @PathVariable String username, @Valid @RequestBody UserDto userDto) {
        User user = new User(userDto.getUsername(), userDto.getPassword(),
                userDto.getFirstname(), userDto.getLastname());

        userService.updateUser(username, user);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@Username @PathVariable String username) {
        userService.deleteUser(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
