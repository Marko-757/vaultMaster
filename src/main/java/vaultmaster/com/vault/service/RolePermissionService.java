package vaultmaster.com.vault.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.Permission;
import vaultmaster.com.vault.repository.PermissionRepository;
import vaultmaster.com.vault.repository.RolePermissionRepository;
import vaultmaster.com.vault.repository.RoleRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public void assignPermissionToRole(UUID roleId, String permissionName) {
        if (roleRepository.findById(roleId).isEmpty()) {
            throw new IllegalArgumentException("Role not found: " + roleId);
        }

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionName));

        rolePermissionRepository.assignPermissionToRole(roleId, permission.getId());
    }

    public void assignPermissionToItem(UUID roleId, UUID itemId, String itemType, String permissionName) {
        if (roleRepository.findById(roleId).isEmpty()) {
            throw new IllegalArgumentException("Role not found: " + roleId);
        }

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionName));

        rolePermissionRepository.assignPermissionToItem(roleId, itemId, itemType, permission.getName());
    }

    public List<Permission> getPermissionsForRole(UUID roleId) {
        return rolePermissionRepository.findPermissionsByRoleId(roleId);
    }

    public List<Permission> getItemPermissionsForRole(UUID roleId, UUID itemId, String itemType) {
        return rolePermissionRepository.findPermissionsByItem(roleId, itemId, itemType);
    }

    public void removePermissionFromFolder(UUID roleId, UUID folderId, String folderType, String permissionName) {
        rolePermissionRepository.removePermissionFromFolder(roleId, folderId, folderType, permissionName);
    }

    public void removePermissionFromItem(UUID roleId, UUID itemId, String itemType, String permissionName) {
        if ("global".equalsIgnoreCase(itemType)) {
            removeGlobalPermission(roleId, permissionName);
        } else {
            rolePermissionRepository.removePermissionFromItem(roleId, itemId, itemType, permissionName);
        }
    }

    public void removeGlobalPermission(UUID roleId, String permissionName) {
        rolePermissionRepository.removePermissionFromRole(roleId, permissionName);
    }

    private void assertRoleBelongsToTeam(UUID roleId, UUID teamId) {
        UUID actualTeamId = roleRepository.findTeamIdByRoleId(roleId);
        if (!actualTeamId.equals(teamId)) {
            throw new IllegalArgumentException("Role does not belong to the specified team.");
        }
    }

    public List<Permission> getFolderPermissionsForRole(UUID roleId, UUID folderId, String folderType) {
        return rolePermissionRepository.findPermissionsByFolder(roleId, folderId, folderType);
    }
}
