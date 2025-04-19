package vaultmaster.com.vault.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamFileFolder;
import vaultmaster.com.vault.repository.*;
import vaultmaster.com.vault.security.PermissionChecker;
import java.util.List;
import java.util.UUID;

@Service
public class TeamFileFolderService {

    private final TeamFileFolderRepository teamFileFolderRepository;
    private final TeamFileRepository teamFileRepository;
    private final PermissionChecker permissionChecker;
    private final PermissionCheckerRepository permissionCheckerRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public TeamFileFolderService(
            TeamFileFolderRepository teamFileFolderRepository,
            TeamFileRepository teamFileRepository,
            PermissionChecker permissionChecker,
            PermissionCheckerRepository permissionCheckerRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        this.teamFileFolderRepository = teamFileFolderRepository;
        this.teamFileRepository = teamFileRepository;
        this.permissionChecker = permissionChecker;
        this.permissionCheckerRepository = permissionCheckerRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public TeamFileFolder createFolder(TeamFileFolder folder) {
        return teamFileFolderRepository.createFolder(folder);
    }

    public List<TeamFileFolder> getFoldersByTeam(UUID teamId) {
        return teamFileFolderRepository.getFoldersByTeamId(teamId);
    }

    public boolean renameFolder(UUID folderId, String newName) {
        return teamFileFolderRepository.renameFolder(folderId, newName) > 0;
    }

    public boolean deleteFolder(UUID folderId) {
        return teamFileFolderRepository.deleteFolder(folderId) > 0;
    }

    public UUID getTeamIdByFolderId(UUID folderId) {
        return teamFileFolderRepository.getTeamIdByFolderId(folderId);
    }

    public boolean deleteFolder(UUID folderId, UUID userId, boolean deleteItems) {
        UUID teamId = getTeamIdByFolderId(folderId);

        boolean allowed = permissionChecker.hasEffectivePermission(
                userId, teamId, null, folderId, "file", "MANAGE_FILE_FOLDERS"
        );

        if (!allowed) {
            throw new AccessDeniedException("Access denied.");
        }

        if (deleteItems) {
            teamFileRepository.deleteByFolderId(folderId);
        } else {
            List<UUID> fileIds = teamFileRepository.findFileIdsByFolder(folderId);
            List<UUID> roleIds = permissionCheckerRepository.findRolesWithFolderPermission(folderId, "MANAGE_FILE_FOLDERS", "file");

            for (UUID roleId : roleIds) {
                List<String> perms = permissionCheckerRepository.findAllPermissionsForRoleOnFolder(roleId, folderId, "file");
                for (UUID fileId : fileIds) {
                    for (String perm : perms) {
                        rolePermissionRepository.assignPermissionToItem(roleId, fileId, "file", perm);
                    }
                }
            }

            teamFileRepository.nullifyFolderId(folderId);
        }

        return teamFileFolderRepository.deleteFolder(folderId) > 0;
    }
}
