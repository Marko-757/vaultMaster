package vaultmaster.com.vault.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    // Getters and Setters
    private String email;
    private String password; // This will be the already-hashed password from frontend

}

