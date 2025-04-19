package vaultmaster.com.vault.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPermission {
    private UUID folderId;
    private UUID roleId;
    private String permissionName;
}

