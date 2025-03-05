package vaultmaster.com.vault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String message;
}

