package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.Permission;
import vaultmaster.com.vault.model.Role;
import vaultmaster.com.vault.model.TeamMember;
import vaultmaster.com.vault.repository.PermissionRepository;
import vaultmaster.com.vault.repository.RolePermissionRepository;
import vaultmaster.com.vault.repository.RoleRepository;
import vaultmaster.com.vault.repository.TeamMemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final TeamMemberRepository teamMemberRepository;

    public RoleService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            TeamMemberRepository teamMemberRepository
    ) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public Role getOrCreateAdminRoleForTeam(UUID teamId, UUID createdBy) {
        String adminRoleName = "Admin";
        return roleRepository.findByNameAndTeamId(adminRoleName, teamId)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleId(UUID.randomUUID());
                    role.setTeamId(teamId);
                    role.setRoleName(adminRoleName);
                    role.setCreatedBy(createdBy);
                    role.setCreatedAt(LocalDateTime.now());

                    roleRepository.save(role);

                    List<Permission> allPermissions = permissionRepository.findAll();
                    System.out.println("🛠 Total permissions found: " + allPermissions.size());
                    for (Permission permission : allPermissions) {
                        System.out.println("→ Assigning: " + permission.getName() + " to admin role");
                        rolePermissionRepository.assignPermissionToRole(role.getRoleId(), permission.getId());
                    }

                    return role;
                });
    }

    public Role createPendingRoleForTeam(UUID teamId, UUID createdBy) {
        String pendingRoleName = "Pending";
        return roleRepository.findByNameAndTeamId(pendingRoleName, teamId)
                .orElseGet(() -> {
                    Role pendingRole = new Role();
                    pendingRole.setRoleId(UUID.randomUUID());
                    pendingRole.setTeamId(teamId);
                    pendingRole.setRoleName(pendingRoleName);
                    pendingRole.setCreatedBy(createdBy);
                    pendingRole.setCreatedAt(LocalDateTime.now());

                    return roleRepository.save(pendingRole);
                });
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> getRolesByTeamId(UUID teamId) {
        return roleRepository.findByTeamId(teamId);
    }

    public Optional<Role> getRoleById(UUID roleId) {
        return roleRepository.findById(roleId);
    }

    public void deleteRole(UUID roleId) {
        roleRepository.delete(roleId);
    }

    public void assignPermissionsToRole(UUID roleId, List<String> permissionNames) {
        UUID teamId = roleRepository.findTeamIdByRoleId(roleId);
        if (!roleRepository.existsByIdAndTeamId(roleId, teamId)) {
            throw new IllegalArgumentException("Role does not belong to the specified team.");
        }

        for (String name : permissionNames) {
            permissionRepository.findByName(name).ifPresent(permission ->
                    rolePermissionRepository.assignPermissionToRole(roleId, permission.getId())
            );
        }
    }

    public void assignRoleToUser(UUID teamId, UUID userId, UUID roleId) {
        UUID roleTeamId = roleRepository.findTeamIdByRoleId(roleId);
        if (!roleTeamId.equals(teamId)) {
            throw new IllegalArgumentException("Role does not belong to the specified team.");
        }

        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of the team"));

        member.setRoleId(roleId);
        teamMemberRepository.assignRoleWithAudit(teamId, userId, roleId, userId);
    }


    // 🔹 New Methods

    public boolean roleExists(UUID roleId) {
        return roleRepository.findById(roleId).isPresent();
    }

    public Optional<Role> findByNameAndTeamId(String roleName, UUID teamId) {
        return roleRepository.findByNameAndTeamId(roleName, teamId);
    }

    public boolean renameRole(UUID roleId, String newName) {
        roleRepository.renameRole(roleId, newName);
        return true;
    }

    public void removeRoleFromUser(UUID teamId, UUID userId) {
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of the team"));

        member.setRoleId(null);
        teamMemberRepository.assignRoleWithAudit(teamId, userId, null, userId);
    }

    public List<Role> getRolesWithPermissionsByTeamId(UUID teamId) {
        List<Role> roles = roleRepository.findByTeamId(teamId);
        for (Role role : roles) {
            List<Permission> permissions = rolePermissionRepository.findPermissionsByRoleId(role.getRoleId());
            role.setPermissions(permissions);
        }
        return roles;
    }

    public Optional<Role> findRoleForUserInTeam(UUID userId, UUID teamId) {
        return roleRepository.findRoleForUserInTeam(userId, teamId);
    }

}
