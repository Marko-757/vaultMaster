package vaultmaster.com.vault.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.FolderPermissionRequest;
import vaultmaster.com.vault.model.TeamFileFolder;
import vaultmaster.com.vault.security.PermissionChecker;
import vaultmaster.com.vault.service.PermissionService;
import vaultmaster.com.vault.service.TeamFileFolderService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/team-file-folders")
public class TeamFileFolderController {

    private final TeamFileFolderService service;
    private final PermissionChecker permissionChecker;
    private static final Logger logger = LoggerFactory.getLogger(TeamFileFolderController.class);
    private final PermissionService permissionService;

    public TeamFileFolderController(TeamFileFolderService service, PermissionChecker permissionChecker, PermissionService permissionService) {
        this.service = service;
        this.permissionChecker = permissionChecker;
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<?> createFolder(@RequestBody TeamFileFolder folder) {
        UUID userId = permissionChecker.getCurrentUserId();

        if (!permissionChecker.userHasPermission(userId, folder.getTeamId(), "MANAGE_TEAM_FILES")) {
            logger.warn("User {} denied access to create file folder in team {}", userId, folder.getTeamId());
            return ResponseEntity.status(403).body("Access denied.");
        }

        TeamFileFolder created = service.createFolder(folder);
        logger.info("User {} created file folder '{}' in team {}", userId, created.getFolderName(), created.getTeamId());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getFolders(@PathVariable UUID teamId) {
        UUID userId = permissionChecker.getCurrentUserId();

        if (!permissionChecker.userHasPermission(userId, teamId, "FILE_VIEW")) {
            logger.warn("User {} denied access to view file folders in team {}", userId, teamId);
            return ResponseEntity.status(403).body("Access denied.");
        }

        logger.info("User {} retrieved file folders in team {}", userId, teamId);
        return ResponseEntity.ok(service.getFoldersByTeam(teamId));
    }

    @PutMapping("/{folderId}/rename")
    public ResponseEntity<?> renameFolder(@PathVariable UUID folderId, @RequestBody Map<String, String> body) {
        UUID userId = permissionChecker.getCurrentUserId();
        UUID teamId = service.getTeamIdByFolderId(folderId);

        boolean allowed = permissionChecker.hasEffectivePermission(
                userId, teamId, null, folderId, "file", "MANAGE_FILE_FOLDERS"
        );

        if (!allowed) {
            logger.warn("User {} denied access to rename file folder {} in team {}", userId, folderId, teamId);
            return ResponseEntity.status(403).body("Access denied.");
        }

        boolean renamed = service.renameFolder(folderId, body.get("newName"));
        if (renamed) {
            logger.info("User {} renamed file folder {} to '{}' in team {}", userId, folderId, body.get("newName"), teamId);
            return ResponseEntity.ok("Folder renamed.");
        } else {
            return ResponseEntity.status(404).body("Folder not found.");
        }
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<?> deleteFolder(
            @PathVariable UUID folderId,
            @RequestParam(name = "deleteItems", defaultValue = "false") boolean deleteItems) {

        UUID userId = permissionChecker.getCurrentUserId();
        UUID teamId = service.getTeamIdByFolderId(folderId);

        boolean allowed = permissionChecker.hasEffectivePermission(
                userId, teamId, null, folderId, "file", "MANAGE_FILE_FOLDERS"
        );

        if (!allowed) {
            logger.warn("User {} denied access to delete file folder {} in team {}", userId, folderId, teamId);
            return ResponseEntity.status(403).body("Access denied.");
        }

        boolean deleted = service.deleteFolder(folderId, userId, deleteItems);
        return deleted
                ? ResponseEntity.ok("File folder deleted.")
                : ResponseEntity.status(404).body("Folder not found.");
    }


    @PostMapping("/{folderId}/permissions")
    public ResponseEntity<?> assignFolderPermissions(
            @PathVariable UUID folderId,
            @RequestBody FolderPermissionRequest request
    ) {
        UUID userId = permissionChecker.getCurrentUserId();
        UUID teamId = service.getTeamIdByFolderId(folderId);

        if (!permissionChecker.hasEffectivePermission(userId, teamId, null, folderId, request.getFolderType(), "MANAGE_" + request.getFolderType().toUpperCase() + "_FOLDERS")) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        permissionService.assignFolderPermissions(request);
        return ResponseEntity.ok("Folder permissions assigned.");
    }

}
