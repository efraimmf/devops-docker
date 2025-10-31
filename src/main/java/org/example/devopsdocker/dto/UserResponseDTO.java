package org.example.devopsdocker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponseDTO {
    private final Long id;
    private final String username;
    private final String email;
}
