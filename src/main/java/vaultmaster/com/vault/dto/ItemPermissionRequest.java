package vaultmaster.com.vault.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ItemPermissionRequest {
    private UUID roleId;
    private UUID teamId;
    private UUID itemId;
    private String itemType;
    private List<String> permissions;
}
