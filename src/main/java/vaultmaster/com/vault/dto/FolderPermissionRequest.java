package vaultmaster.com.vault.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FolderPermissionRequest {
    private UUID teamId;
    private UUID roleId;
    private UUID folderId;
    private String folderType;
    private List<String> permissions;
}
