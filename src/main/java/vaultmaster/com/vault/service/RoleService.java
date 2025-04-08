package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.exception.DuplicateResourceException;
import vaultmaster.com.vault.exception.NotFoundException;
import vaultmaster.com.vault.model.Permission;
import vaultmaster.com.vault.model.Role;
import vaultmaster.com.vault.repository.PermissionRepository;
import vaultmaster.com.vault.repository.RolePermissionRepository;
import vaultmaster.com.vault.repository.RoleRepository;
import vaultmaster.com.vault.repository.TeamMemberRepository;
import vaultmaster.com.vault.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final RolePermissionRepository rolePermissionRepo;
    private final TeamMemberRepository teamMemberRepo;
    private final TeamRepository teamRepo;

    public RoleService(RoleRepository roleRepo, PermissionRepository permissionRepo,
                       RolePermissionRepository rolePermissionRepo,
                       TeamMemberRepository teamMemberRepo,
                       TeamRepository teamRepo) {
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.rolePermissionRepo = rolePermissionRepo;
        this.teamMemberRepo = teamMemberRepo;
        this.teamRepo = teamRepo;
    }


    public Role createRole(UUID teamId, String roleName, UUID creatorId) {
        if (!teamRepo.teamExists(teamId)) {
            throw new NotFoundException("Team with ID " + teamId + " not found.");
        }

        if (roleRepo.existsByTeamIdAndRoleName(teamId, roleName)) {
            throw new DuplicateResourceException("Role '" + roleName + "' already exists for this team.");
        }

        Role role = new Role();
        role.setRoleId(UUID.randomUUID());
        role.setTeamId(teamId);
        role.setRoleName(roleName);
        role.setCreatedBy(creatorId);
        role.setCreatedAt(LocalDateTime.now());
        roleRepo.save(role);
        return role;
    }

    public void assignPermissionsToRole(UUID roleId, List<String> permissionNames) {
        for (String permissionName : permissionNames) {
            Permission permission = permissionRepo.findByName(permissionName)
                    .orElseThrow(() -> new NotFoundException("Permission not found: " + permissionName));
            rolePermissionRepo.assignPermissionToRole(roleId, permission.getId());
        }
    }

    public void assignRoleToUser(UUID teamId, UUID userId, UUID roleId) {
        if (!teamRepo.teamExists(teamId)) {
            throw new NotFoundException("Team with ID " + teamId + " not found.");
        }
        teamMemberRepo.assignRole(teamId, userId, roleId);
    }

    public List<Role> getRolesForTeam(UUID teamId) {
        if (!teamRepo.teamExists(teamId)) {
            throw new NotFoundException("Team with ID " + teamId + " not found.");
        }
        return roleRepo.findByTeamId(teamId);
    }

    public java.util.Optional<Role> getRoleById(UUID roleId) {
        return roleRepo.findById(roleId);
    }
}
