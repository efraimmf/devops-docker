package org.example.devopsdocker.controller;

import org.example.devopsdocker.dto.CreateUserDTO;
import org.example.devopsdocker.dto.UserResponseDTO;
import org.example.devopsdocker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserDTO createUserDTO) {
        UserResponseDTO user = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        Optional<UserResponseDTO> user = userService.findByUsername(username);
        return ResponseEntity.of(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        Optional<UserResponseDTO> user = userService.findByEmail(email);
        return ResponseEntity.of(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        Optional<UserResponseDTO> user = userService.findById(id);
        return ResponseEntity.of(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable Long id) {
        UserResponseDTO user = userService.deleteUser(id);
        return ResponseEntity.ok(user);
    }
}
