package vaultmaster.com.vault.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamPWFolder;
import vaultmaster.com.vault.repository.*;
import vaultmaster.com.vault.security.PermissionChecker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TeamPWFolderService {

    private final TeamPWFolderRepository teamPWFolderRepository;
    private final TeamPasswordRepository teamPasswordRepository;
    private final PermissionCheckerRepository permissionCheckerRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionChecker permissionChecker;

    public TeamPWFolderService(
            TeamPWFolderRepository teamPWFolderRepository,
            TeamPasswordRepository teamPasswordRepository,
            PermissionCheckerRepository permissionCheckerRepository,
            RolePermissionRepository rolePermissionRepository,
            PermissionChecker permissionChecker
    ) {
        this.teamPWFolderRepository = teamPWFolderRepository;
        this.teamPasswordRepository = teamPasswordRepository;
        this.permissionCheckerRepository = permissionCheckerRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionChecker = permissionChecker;
    }

    public void createFolder(UUID teamId, String name, UUID createdBy) {
        TeamPWFolder folder = TeamPWFolder.builder()
                .folderId(UUID.randomUUID())
                .teamId(teamId)
                .folderName(name)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .modifiedBy(createdBy)
                .modifiedAt(LocalDateTime.now())
                .build();

        teamPWFolderRepository.createFolder(folder);
    }

    public List<TeamPWFolder> getFoldersForTeam(UUID teamId) {
        return teamPWFolderRepository.getFoldersByTeamId(teamId);
    }

    public boolean renameFolder(UUID folderId, String newName, UUID userId) {
        return teamPWFolderRepository.renameFolder(folderId, newName, userId) > 0;
    }

    public UUID getTeamIdByFolderId(UUID folderId) {
        return teamPWFolderRepository.getTeamIdByFolderId(folderId);
    }

    public boolean deleteFolder(UUID folderId, UUID userId, boolean deleteItems) {
        UUID teamId = getTeamIdByFolderId(folderId);

        if (!permissionChecker.hasEffectivePermission(userId, teamId, null, folderId, "password", "MANAGE_PASSWORD_FOLDERS")) {
            throw new AccessDeniedException("Access denied.");
        }

        if (deleteItems) {
            teamPasswordRepository.deleteByFolderId(folderId);
        } else {
            List<Integer> entryIds = teamPasswordRepository.findEntryIdsByFolder(folderId);
            List<UUID> roleIds = permissionCheckerRepository.findRolesWithFolderPermission(folderId, "MANAGE_PASSWORD_FOLDERS", "password");

            for (UUID roleId : roleIds) {
                List<String> perms = permissionCheckerRepository.findAllPermissionsForRoleOnFolder(roleId, folderId, "password");
                for (Integer entryId : entryIds) {
                    for (String perm : perms) {
                        rolePermissionRepository.assignPermissionToItem(roleId, UUID.fromString(entryId.toString()), "password", perm);
                    }
                }
            }

            teamPasswordRepository.nullifyFolderId(folderId);
        }

        return teamPWFolderRepository.deleteFolder(folderId) > 0;
    }
}
