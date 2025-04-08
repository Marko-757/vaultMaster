package vaultmaster.com.vault.security;

import org.springframework.stereotype.Component;
import vaultmaster.com.vault.repository.RolePermissionRepository;
import vaultmaster.com.vault.repository.TeamMemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Component
public class PermissionChecker {

    private final TeamMemberRepository teamMemberRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public PermissionChecker(TeamMemberRepository teamMemberRepository,
                             RolePermissionRepository rolePermissionRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }


    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return UUID.fromString(authentication.getName()); // assuming name is userId
    }

    public boolean userHasPermission(UUID userId, UUID teamId, String requiredPermission) {
        return hasPermission(teamId, userId, requiredPermission);
    }

    public boolean hasPermission(UUID teamId, UUID userId, String requiredPermission) {
        // Check if the user is part of the team
        var teamMemberOpt = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        if (teamMemberOpt.isEmpty()) {
            return false;
        }

        var roleId = teamMemberOpt.get().getRoleId();
        if (roleId == null) {
            return false;
        }

        // Check if the role has the required permission
        return rolePermissionRepository.roleHasPermission(roleId, requiredPermission);
    }
}
