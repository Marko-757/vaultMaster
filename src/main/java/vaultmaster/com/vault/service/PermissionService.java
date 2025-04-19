package vaultmaster.com.vault.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.dto.FolderPermissionRequest;
import vaultmaster.com.vault.model.Permission;
import vaultmaster.com.vault.repository.PermissionRepository;
import vaultmaster.com.vault.repository.RolePermissionRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public void assignFolderPermissions(FolderPermissionRequest request) {
        for (String permissionName : request.getPermissions()) {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionName));

            rolePermissionRepository.assignPermissionToFolder(
                    request.getRoleId(),
                    request.getFolderId(),
                    request.getFolderType(),
                    permission.getName()
            );
        }
    }
}

