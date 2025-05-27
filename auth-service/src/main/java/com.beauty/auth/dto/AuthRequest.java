package com.beauty.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @NotBlank()
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank()
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;
}
