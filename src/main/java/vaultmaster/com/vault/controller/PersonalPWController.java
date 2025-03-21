package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.service.PersonalPWService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passwords/personal")
public class PersonalPWController {
    private final PersonalPWService service;

    public PersonalPWController(PersonalPWService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> addPassword(@RequestBody PersonalPWEntry entry, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        UUID userId = UUID.fromString(authentication.getName()); // Get userId from JWT
        entry.setUserId(userId);

        // Validate required fields
        String missingFields = validateEntry(entry);
        if (!missingFields.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing required fields: " + missingFields);
        }

        try {
            service.addPassword(entry);
            return ResponseEntity.ok("Password saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private String validateEntry(PersonalPWEntry entry) {
        List<String> missingFields = new ArrayList<>();

        if (entry.getAccountName() == null || entry.getAccountName().trim().isEmpty()) missingFields.add("accountName");
        if (entry.getUsername() == null || entry.getUsername().trim().isEmpty()) missingFields.add("username");
        if (entry.getPasswordHash() == null || entry.getPasswordHash().trim().isEmpty()) missingFields.add("passwordHash");

        return String.join(", ", missingFields);
    }

    @GetMapping
    public ResponseEntity<?> getUserPasswords(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        UUID userId = UUID.fromString(authentication.getName());
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
    public ResponseEntity<String> updatePassword(
            @PathVariable Long entryId,
            @RequestBody PersonalPWEntry updatedEntry,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        UUID userId = UUID.fromString(authentication.getName());
        updatedEntry.setUserId(userId);
        updatedEntry.setEntryId(entryId); // Ensure the correct entry is targeted

        try {
            service.updatePassword(updatedEntry);
            return ResponseEntity.ok("Password updated successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
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

    // ✅ Get passwords by folder
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<?> getPasswordsByFolder(@PathVariable UUID folderId) {
        try {
            return ResponseEntity.ok(service.getPasswordsByFolder(folderId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
