package vaultmaster.com.vault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vaultmaster.com.vault.model.User;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;

    public AuthResponse(User user, String token) {
        this.token = token;
        this.email = user.getEmail();
        this.fullName = user.getFullName();
    }

}
