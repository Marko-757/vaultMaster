package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.repository.PermissionRepository;
import vaultmaster.com.vault.repository.RolePermissionRepository;
import vaultmaster.com.vault.repository.RoleRepository;
import vaultmaster.com.vault.model.Permission;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public void assignPermissionToRole(UUID roleId, String permissionName) {
        if (!roleRepository.findById(roleId).isPresent()) {
            throw new IllegalArgumentException("Role not found: " + roleId);
        }

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionName));

        rolePermissionRepository.assignPermissionToRole(roleId, permission.getId());
    }

    public List<Permission> getPermissionsForRole(UUID roleId) {
        return rolePermissionRepository.findPermissionsByRoleId(roleId);
    }

}
