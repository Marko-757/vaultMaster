package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.Permission;
import vaultmaster.com.vault.model.Role;
import vaultmaster.com.vault.repository.PermissionRepository;
import vaultmaster.com.vault.repository.RolePermissionRepository;
import vaultmaster.com.vault.repository.RoleRepository;
import vaultmaster.com.vault.repository.TeamMemberRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final RolePermissionRepository rolePermissionRepo;
    private final TeamMemberRepository teamMemberRepo;

    public RoleService(RoleRepository roleRepo, PermissionRepository permissionRepo,
                       RolePermissionRepository rolePermissionRepo, TeamMemberRepository teamMemberRepo) {
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.rolePermissionRepo = rolePermissionRepo;
        this.teamMemberRepo = teamMemberRepo;
    }

    public Role createRole(UUID teamId, String roleName, UUID creatorId) {
        Role role = new Role();
        role.setRoleId(UUID.randomUUID());
        role.setTeamId(teamId);
        role.setRoleName(roleName);
        role.setCreatedBy(creatorId);
        role.setCreatedAt(new Date());
        roleRepo.save(role);
        return role;
    }

    public void assignPermission(UUID roleId, String permissionName) {
        Permission permission = permissionRepo.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName));
        rolePermissionRepo.assignPermissionToRole(roleId, permission.getId());
    }

    public void assignRoleToUser(UUID teamId, UUID userId, UUID roleId) {
        teamMemberRepo.assignRole(teamId, userId, roleId);
    }

    public List<Role> getRolesForTeam(UUID teamId) {
        return roleRepo.findByTeamId(teamId);
    }
}
