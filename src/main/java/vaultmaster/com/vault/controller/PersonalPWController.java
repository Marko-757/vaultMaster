package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.model.PasswordEntry;  // Add this import
import vaultmaster.com.vault.service.PersonalPWService;
import vaultmaster.com.vault.service.PasswordEntryService;  // Add this import

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/passwords/personal")
public class PersonalPWController {

    private final PersonalPWService service;
    private final PasswordEntryService passwordEntryService;

    public PersonalPWController(PersonalPWService service, PasswordEntryService passwordEntryService) {
        this.service = service;
        this.passwordEntryService = passwordEntryService;
    }

    @PostMapping
    public ResponseEntity<?> addPassword(@RequestBody PersonalPWEntry entry, Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID userId = UUID.fromString(auth.getName());
        entry.setUserId(userId);
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());

        String missing = validateEntry(entry);
        if (!missing.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing fields: " + missing);
        }

        try {
            return ResponseEntity.ok(service.addPassword(entry));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving password.");
        }
    }

    @GetMapping("/me/passwords")
    public ResponseEntity<?> getUserPasswords(Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID userId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(service.getUserPasswords(userId));
    }

    @GetMapping("/entry/{entryId}")
    public ResponseEntity<?> getPasswordById(@PathVariable Long entryId) {
        try {
            return ResponseEntity.ok(service.getPasswordById(entryId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping("/entry/{entryId}")
    public ResponseEntity<String> updatePassword(@PathVariable Long entryId, @RequestBody PersonalPWEntry entry, Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");

        try {
            entry.setUserId(UUID.fromString(auth.getName()));
            entry.setEntryId(entryId);
            service.updatePassword(entry);
            return ResponseEntity.ok("Password updated successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update error: " + e.getMessage());
        }
    }

    @PutMapping("/entry/{entryId}/move-folder")
    public ResponseEntity<?> movePasswordToFolder(@PathVariable Long entryId,
                                                  @RequestParam(required = false) UUID newFolderId,
                                                  Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        UUID userId = UUID.fromString(auth.getName());

        try {
            service.movePasswordToFolder(entryId, userId, newFolderId);
            return ResponseEntity.ok("Folder updated successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to move folder.");
        }
    }


    @GetMapping("/entry/{entryId}/decrypt")
    public ResponseEntity<String> decryptPassword(@PathVariable Long entryId, Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");

        try {
            UUID userId = UUID.fromString(auth.getName());
            return ResponseEntity.ok(service.decryptPasswordById(entryId, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Decryption error: " + e.getMessage());
        }
    }

    @DeleteMapping("/entry/{entryId}")
    public ResponseEntity<String> deletePassword(@PathVariable Long entryId) {
        try {
            service.deletePassword(entryId);
            return ResponseEntity.ok("Password deleted successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getReason());
        }
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<?> getPasswordsByFolder(@PathVariable UUID folderId) {
        try {
            return ResponseEntity.ok(service.getPasswordsByFolder(folderId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    private String validateEntry(PersonalPWEntry entry) {
        List<String> missing = new ArrayList<>();
        if (entry.getAccountName() == null || entry.getAccountName().isBlank()) missing.add("accountName");
        if (entry.getUsername() == null || entry.getUsername().isBlank()) missing.add("username");
        if (entry.getPasswordHash() == null || entry.getPasswordHash().isBlank()) missing.add("passwordHash");
        return String.join(", ", missing);
    }

    private PasswordEntry convertToPasswordEntry(PersonalPWEntry entry) {
        PasswordEntry passwordEntry = new PasswordEntry();
        passwordEntry.setUserId(entry.getUserId());
        passwordEntry.setAccountName(entry.getAccountName());
        passwordEntry.setUsername(entry.getUsername());
        passwordEntry.setPasswordHash(entry.getPasswordHash());
        passwordEntry.setWebsite(entry.getWebsite());
        passwordEntry.setFolderId(entry.getFolderId());
        passwordEntry.setCreatedAt(entry.getCreatedAt());
        passwordEntry.setUpdatedAt(entry.getUpdatedAt());
        return passwordEntry;
    }
}
