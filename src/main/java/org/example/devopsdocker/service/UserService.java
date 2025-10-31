package org.example.devopsdocker.service;

import org.example.devopsdocker.dto.CreateUserDTO;
import org.example.devopsdocker.dto.UserResponseDTO;
import org.example.devopsdocker.model.User;
import org.example.devopsdocker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(createUserDTO.getPassword());
        User userCreated = userRepository.save(user);
        return toResponseDTO(userCreated);
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(this::toResponseDTO).collect(java.util.stream.Collectors.toList());
    }

    public Optional<UserResponseDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toResponseDTO);
    }

    public Optional<UserResponseDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toResponseDTO);
    }

    public Optional<UserResponseDTO> findById(Long id) {
        return userRepository.findById(id).map(this::toResponseDTO);
    }

    public UserResponseDTO deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        userRepository.deleteById(user.getId());
        return toResponseDTO(user);
    }
}
