package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.TeamPassword;
import vaultmaster.com.vault.repository.TeamPWFolderRepository;
import vaultmaster.com.vault.security.PermissionChecker;
import vaultmaster.com.vault.service.TeamPasswordService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/team/passwords")
public class TeamPasswordController {

    private final TeamPasswordService passwordService;
    private final PermissionChecker permissionChecker;
    private final TeamPWFolderRepository teamPWFolderRepository;

    public TeamPasswordController(
            TeamPasswordService passwordService,
            PermissionChecker permissionChecker,
            TeamPWFolderRepository teamPWFolderRepository
    ) {
        this.passwordService = passwordService;
        this.permissionChecker = permissionChecker;
        this.teamPWFolderRepository = teamPWFolderRepository;
    }

    @PostMapping
    public ResponseEntity<?> createPassword(@RequestBody TeamPassword pw, HttpServletRequest request) {
        UUID userId = permissionChecker.getCurrentUserId();

        if (!permissionChecker.userHasPermission(userId, pw.getTeamId(), "MANAGE_TEAM_PASSWORDS")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            pw.setCreatedBy(userId);
            pw.setModifiedBy(userId);

            TeamPassword created = passwordService.createPassword(pw);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create password.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePassword(
            @PathVariable UUID id,
            @RequestBody TeamPassword updated
    ) {
        UUID userId = permissionChecker.getCurrentUserId();
        TeamPassword existing = passwordService.getPasswordById(id);

        boolean allowed = permissionChecker.hasEffectivePermission(
                userId,
                existing.getTeamId(),
                existing.getEntryId(), // ✅ Correct: entryId is int
                existing.getFolderId(),
                "password",
                "MANAGE_TEAM_PASSWORDS"
        );

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            updated.setTeamPasswordId(id);
            updated.setModifiedBy(userId);
            passwordService.updatePassword(updated);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassword(@PathVariable UUID id) {
        UUID userId = permissionChecker.getCurrentUserId();
        TeamPassword password = passwordService.getPasswordById(id);

        boolean allowed = permissionChecker.hasEffectivePermission(
                userId,
                password.getTeamId(),
                password.getEntryId(), // ✅ Fixed
                password.getFolderId(),
                "password",
                "MANAGE_TEAM_PASSWORDS"
        );

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            passwordService.deletePassword(id);
            return ResponseEntity.ok("Password deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete password.");
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getPasswordsByTeam(@PathVariable UUID teamId) {
        UUID userId = permissionChecker.getCurrentUserId();

        if (!permissionChecker.userHasPermission(userId, teamId, "PASSWORD_VIEW")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            List<TeamPassword> passwords = passwordService.getPasswordsByTeam(teamId);
            return ResponseEntity.ok(passwords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch team passwords.");
        }
    }

    @PutMapping("/{teamPasswordId}/move")
    public ResponseEntity<?> movePassword(
            @PathVariable UUID teamPasswordId,
            @RequestBody Map<String, String> body
    ) {
        UUID userId = permissionChecker.getCurrentUserId();
        TeamPassword password = passwordService.getPasswordById(teamPasswordId);

        boolean allowed = permissionChecker.hasEffectivePermission(
                userId,
                password.getTeamId(),
                password.getEntryId(), // ✅ Fixed
                password.getFolderId(),
                "password",
                "MANAGE_TEAM_PASSWORDS"
        );

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        UUID folderId = UUID.fromString(body.get("folderId"));
        boolean moved = passwordService.movePasswordToFolder(teamPasswordId, folderId);
        return moved ? ResponseEntity.ok("Password moved.") : ResponseEntity.status(404).body("Password not found.");
    }

    @GetMapping("/{teamPasswordId}/decrypt")
    public ResponseEntity<?> decryptTeamPassword(@PathVariable UUID teamPasswordId, Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        TeamPassword password = passwordService.getPasswordById(teamPasswordId);

        boolean allowed = permissionChecker.hasEffectivePermission(
                userId,
                password.getTeamId(),
                password.getTeamPasswordId(),
                password.getFolderId(),
                "password",
                "PASSWORD_VIEW"
        );

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            String decrypted = passwordService.decryptPasswordByEntryId(password.getEntryId());
            return ResponseEntity.ok(Map.of("decryptedPassword", decrypted));
        } catch (Exception e) {
            e.printStackTrace(); // 🔍 Helps during local debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Decryption error: " + e.getMessage());
        }
    }


    @GetMapping("/folder/{folderId}")
    public ResponseEntity<?> getPasswordsInFolder(@PathVariable UUID folderId) {
        UUID userId = permissionChecker.getCurrentUserId();
        UUID teamId = teamPWFolderRepository.getTeamIdByFolderId(folderId);

        if (!permissionChecker.userHasFolderPermission(userId, teamId, folderId, "PASSWORD_VIEW", false)) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        List<TeamPassword> passwords = passwordService.getPasswordsByFolder(folderId);
        return ResponseEntity.ok(passwords);
    }
}
