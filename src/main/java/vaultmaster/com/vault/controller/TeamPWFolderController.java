package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.security.PermissionChecker;
import vaultmaster.com.vault.service.TeamPWFolderService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/team/folders")
public class TeamPWFolderController {

    private final TeamPWFolderService service;
    private final PermissionChecker permissionChecker;

    public TeamPWFolderController(TeamPWFolderService service, PermissionChecker permissionChecker) {
        this.service = service;
        this.permissionChecker = permissionChecker;
    }

    @PostMapping
    public ResponseEntity<?> createFolder(@RequestBody Map<String, String> body) {
        UUID userId = permissionChecker.getCurrentUserId();
        UUID teamId = UUID.fromString(body.get("teamId"));
        String name = body.get("folderName");

        if (!permissionChecker.userHasPermission(userId, teamId, "MANAGE_TEAM_PASSWORDS")) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        service.createFolder(teamId, name, userId);
        return ResponseEntity.ok("Folder created successfully.");
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamFolders(@PathVariable UUID teamId) {
        UUID userId = permissionChecker.getCurrentUserId();

        if (!permissionChecker.userHasPermission(userId, teamId, "MANAGE_TEAM_PASSWORDS")) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        return ResponseEntity.ok(service.getFoldersForTeam(teamId));
    }

    @PutMapping("/{folderId}/rename")
    public ResponseEntity<?> renameFolder(@PathVariable UUID folderId, @RequestBody Map<String, String> body) {
        UUID userId = permissionChecker.getCurrentUserId();
        String newName = body.get("newName");

        if (newName == null || newName.isBlank()) {
            return ResponseEntity.badRequest().body("New folder name is required.");
        }

        boolean renamed = service.renameFolder(folderId, newName, userId);
        return renamed ? ResponseEntity.ok("Folder renamed.") : ResponseEntity.status(404).body("Folder not found.");
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<?> deleteFolder(@PathVariable UUID folderId) {
        UUID userId = permissionChecker.getCurrentUserId();

        // Optional: check if user has access to folder via team_id (requires extra query)
        boolean deleted = service.deleteFolder(folderId);
        return deleted ? ResponseEntity.ok("Folder deleted.") : ResponseEntity.status(404).body("Folder not found.");
    }

}
