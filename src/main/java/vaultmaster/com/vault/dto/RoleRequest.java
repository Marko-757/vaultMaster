package vaultmaster.com.vault.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class RoleRequest {

    @NotNull
    private UUID teamId;  // Team ID should be a UUID

    @NotNull
    @Size(min = 3, max = 255)
    private String roleName;  // Role Name should be a String

    // Getters and Setters
    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
