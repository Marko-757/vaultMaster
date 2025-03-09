package vaultmaster.com.vault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vaultmaster.com.vault.model.User;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private User user;
    private String token;
}
