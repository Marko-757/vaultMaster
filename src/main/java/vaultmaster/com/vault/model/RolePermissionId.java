package vaultmaster.com.vault.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data  // Generates getters, setters, toString(), equals(), and hashCode()
@NoArgsConstructor  // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class RolePermissionId implements Serializable {
    private UUID roleId;
    private UUID permissionId;
}


