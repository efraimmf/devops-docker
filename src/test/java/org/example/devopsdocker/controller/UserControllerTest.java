package org.example.devopsdocker.controller;

import org.example.devopsdocker.dto.CreateUserDTO;
import org.example.devopsdocker.dto.UpdateUserDTO;
import org.example.devopsdocker.dto.UserResponseDTO;
import org.example.devopsdocker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserResponseDTO createTestUser(Long id, String username, String email) {
        return UserResponseDTO.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    @Test
    void deveCriarNovoUsuario() throws Exception {
        UserResponseDTO responseDTO = createTestUser(1L, "testuser", "test@example.com");

        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void deveBuscarTodosOsUsuarios() throws Exception {
        List<UserResponseDTO> users = List.of(
                createTestUser(1L, "user1", "user1@example.com"),
                createTestUser(2L, "user2", "user2@example.com")
        );

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void deveBuscarUsuarioPorUsername() throws Exception {
        UserResponseDTO user = createTestUser(1L, "testuser", "test@example.com");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void deveRetornarNotFoundQuandoUsernameNaoExiste() throws Exception {
        when(userService.findByUsername("inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/username/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        UserResponseDTO user = createTestUser(1L, "testuser", "test@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void deveRetornarNotFoundQuandoEmailNaoExiste() throws Exception {
        when(userService.findByEmail("inexistente@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/email/inexistente@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        UserResponseDTO user = createTestUser(1L, "testuser", "test@example.com");

        when(userService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornarNotFoundQuandoIdNaoExiste() throws Exception {
        when(userService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        UserResponseDTO updatedUser = createTestUser(1L, "novoUsername", "novo@example.com");

        when(userService.updateUser(eq(1L), any(UpdateUserDTO.class))).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"novoUsername\",\"email\":\"novo@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("novoUsername"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarIdInexistente() throws Exception {
        when(userService.updateUser(eq(999L), any(UpdateUserDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"novo\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        UserResponseDTO user = createTestUser(1L, "testuser", "test@example.com");

        when(userService.deleteUser(1L)).thenReturn(user);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
