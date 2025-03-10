package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.service.PersonalPWService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/passwords/personal")
public class PersonalPWController {
    private final PersonalPWService service;

    public PersonalPWController(PersonalPWService service) {
        this.service = service;
    }

    // ✅ Create a new password
    @PostMapping
    public ResponseEntity<String> addPassword(@RequestBody PersonalPWEntry entry) {
        // ✅ Validate required fields
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

    // ✅ Helper method to validate required fields
    private String validateEntry(PersonalPWEntry entry) {
        List<String> missingFields = new ArrayList<>();

        if (entry.getUserId() == null) missingFields.add("userId");
        if (entry.getAccountName() == null || entry.getAccountName().trim().isEmpty()) missingFields.add("accountName");
        if (entry.getUsername() == null || entry.getUsername().trim().isEmpty()) missingFields.add("username");
        if (entry.getPasswordHash() == null || entry.getPasswordHash().trim().isEmpty()) missingFields.add("passwordHash");

        return String.join(", ", missingFields);
    }

    // ✅ Get all passwords for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPasswords(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(service.getUserPasswords(userId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    // ✅ Get a password by its ID
    @GetMapping("/entry/{entryId}")
    public ResponseEntity<?> getPasswordById(@PathVariable Long entryId) {
        try {
            return ResponseEntity.ok(service.getPasswordById(entryId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    // ✅ Delete a password
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
