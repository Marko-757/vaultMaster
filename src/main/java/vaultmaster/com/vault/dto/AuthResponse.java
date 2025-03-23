package vaultmaster.com.vault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vaultmaster.com.vault.model.User;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private UUID userId;  // 👈 Add this

    public AuthResponse(User user, String token) {
        this.token = token;
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.userId = user.getUserId();  // 👈 Add this
    }
}
