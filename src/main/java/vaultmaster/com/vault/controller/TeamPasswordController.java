package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.PasswordEntry;
import vaultmaster.com.vault.model.TeamPassword;
import vaultmaster.com.vault.security.PermissionChecker;
import vaultmaster.com.vault.service.PasswordEntryService;
import vaultmaster.com.vault.service.TeamPasswordService;
import vaultmaster.com.vault.util.AESUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/team/passwords")
public class TeamPasswordController {

    private final TeamPasswordService passwordService;
    private final PermissionChecker permissionChecker;
    private final PasswordEntryService passwordEntryService;  // Add this

    public TeamPasswordController(TeamPasswordService passwordService, PermissionChecker permissionChecker, PasswordEntryService passwordEntryService) {
        this.passwordService = passwordService;
        this.permissionChecker = permissionChecker;
        this.passwordEntryService = passwordEntryService;  // Inject PasswordEntryService
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTeamPassword(@PathVariable int id) {
        UUID userId = permissionChecker.getCurrentUserId();
        TeamPassword password = passwordService.getPasswordById(id);

        if (!permissionChecker.userHasPermission(userId, password.getTeamId(), "PASSWORD_VIEW")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        return ResponseEntity.ok(password);
    }

    @PostMapping
    public ResponseEntity<?> createPassword(@RequestBody TeamPassword pw, HttpServletRequest request) {
        UUID userId = permissionChecker.getCurrentUserId();

        if (!permissionChecker.userHasPermission(userId, pw.getTeamId(), "MANAGE_TEAM_PASSWORDS")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            // Step 1: Create the PasswordEntry
            PasswordEntry entry = new PasswordEntry();
            entry.setUserId(pw.getCreatedBy());
            entry.setAccountName(pw.getAccountName());
            entry.setUsername(pw.getUsername());
            entry.setPasswordHash(pw.getPlaintextPassword());
            entry.setWebsite(pw.getWebsite());
            entry.setFolderId(pw.getFolderId());

            // Encrypt the password hash before saving
            String encryptedPassword = AESUtil.encrypt(entry.getPasswordHash());
            entry.setPasswordHash(encryptedPassword);

            // Save the password entry first
            PasswordEntry createdEntry = passwordEntryService.createPasswordEntry(pw.getCreatedBy(), entry);

            // Step 2: Create TeamPassword and link to created entry
            pw.setEntryId(createdEntry.getEntryId());  // Set the entryId from PasswordEntry
            pw.setCreatedBy(userId);
            pw.setModifiedBy(userId);
            TeamPassword created = passwordService.createPassword(pw);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create password.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassword(@PathVariable int id) {
        UUID userId = permissionChecker.getCurrentUserId();
        TeamPassword password = passwordService.getPasswordById(id);

        if (!permissionChecker.userHasPermission(userId, password.getTeamId(), "MANAGE_TEAM_PASSWORDS")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            // Delete associated password entry
            passwordEntryService.deletePasswordEntry(password.getEntryId()); // You will call this method in PasswordEntryService

            // Delete team password
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
}
