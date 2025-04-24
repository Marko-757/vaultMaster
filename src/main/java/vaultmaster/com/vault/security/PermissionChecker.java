package vaultmaster.com.vault.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vaultmaster.com.vault.repository.PermissionCheckerRepository;
import vaultmaster.com.vault.repository.RolePermissionRepository;
import vaultmaster.com.vault.repository.TeamMemberRepository;

import java.util.UUID;

@Component
public class PermissionChecker {

    private final TeamMemberRepository teamMemberRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionCheckerRepository permissionCheckerRepository;

    private static final Logger logger = LoggerFactory.getLogger(PermissionChecker.class);

    public PermissionChecker(
            TeamMemberRepository teamMemberRepository,
            RolePermissionRepository rolePermissionRepository,
            PermissionCheckerRepository permissionCheckerRepository
    ) {
        this.teamMemberRepository = teamMemberRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionCheckerRepository = permissionCheckerRepository;
    }

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return UUID.fromString(authentication.getName());
    }

    public boolean userHasPermission(UUID userId, UUID teamId, String permission) {
        return hasEffectivePermission(userId, teamId, null, null, null, permission);
    }

    public boolean hasEffectivePermission(
            UUID userId,
            UUID teamId,
            Object itemId,
            UUID folderId,
            String itemType,
            String permission
    ) {
        var memberOpt = teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
        if (memberOpt.isEmpty()) {
            logger.warn("User {} is not a member of team {}", userId, teamId);
            return false;
        }

        UUID roleId = memberOpt.get().getRoleId();
        if (roleId == null) {
            logger.warn("User {} has no role in team {}", userId, teamId);
            return false;
        }

        // Item-level override
        if (itemId != null && itemType != null) {
            if (itemId instanceof UUID uuidId) {
                if (permissionCheckerRepository.roleHasItemPermission(roleId, uuidId, itemType, permission)) {
                    logger.debug("UUID item match for {} {}", itemType, itemId);
                    return true;
                }
            }
        }

        // Folder-level
        if (folderId != null && itemType != null) {
            String folderType = itemType.equalsIgnoreCase("file") ? "file" : "password";
            if (permissionCheckerRepository.roleHasFolderPermission(roleId, folderId, permission, folderType)) {
                return true;
            }
        }

        // Global
        return rolePermissionRepository.roleHasPermission(roleId, permission);
    }

    public boolean userHasItemPermission(UUID userId, UUID teamId, int itemId, String itemType, String permission) {
        return hasEffectivePermission(userId, teamId, itemId, null, itemType, permission);
    }

    // For item_id stored as UUID (e.g., files)
    public boolean userHasItemPermission(UUID userId, UUID teamId, UUID itemId, String itemType, String permission) {
        return hasEffectivePermission(userId, teamId, itemId, null, itemType, permission);
    }


    public boolean userHasFolderPermission(UUID userId, UUID teamId, UUID folderId, String permission, boolean isFileFolder) {
        String folderType = isFileFolder ? "file" : "password";
        return hasEffectivePermission(userId, teamId, null, folderId, folderType, permission);
    }
}
