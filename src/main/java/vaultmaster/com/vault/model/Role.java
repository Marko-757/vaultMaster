package vaultmaster.com.vault.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private UUID roleId;
    private String roleName;
    private UUID teamId;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private List<Permission> permissions;
}
