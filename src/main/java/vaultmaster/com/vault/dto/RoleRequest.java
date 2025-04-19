package vaultmaster.com.vault.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class RoleRequest {

    // Getters and Setters
    @NotNull
    private UUID teamId;  // Team ID should be a UUID

    @NotNull
    @Size(min = 3, max = 255)
    private String roleName;  // Role Name should be a String

}
