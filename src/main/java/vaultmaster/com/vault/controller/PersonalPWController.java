package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.service.PersonalPWService;
import vaultmaster.com.vault.util.AESUtil;

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
    public ResponseEntity<PersonalPWEntry> addPassword(@RequestBody PersonalPWEntry entry, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        entry.setUserId(userId);

        String missingFields = validateEntry(entry);
        if (!missingFields.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Encrypt password before storing
            String encrypted = AESUtil.encrypt(entry.getPasswordHash());
            entry.setPasswordHash(encrypted);

            service.addPassword(entry);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private String validateEntry(PersonalPWEntry entry) {
        List<String> missingFields = new ArrayList<>();
        if (entry.getAccountName() == null || entry.getAccountName().trim().isEmpty()) missingFields.add("accountName");
        if (entry.getUsername() == null || entry.getUsername().trim().isEmpty()) missingFields.add("username");
        if (entry.getPasswordHash() == null || entry.getPasswordHash().trim().isEmpty()) missingFields.add("passwordHash");
        return String.join(", ", missingFields);
    }

    @GetMapping("/me/passwords")
    public ResponseEntity<List<PersonalPWEntry>> getAllUserPasswords(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        List<PersonalPWEntry> entries = service.getUserPasswords(userId);
        return ResponseEntity.ok(entries);
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
    public ResponseEntity<String> updatePassword(@PathVariable Long entryId, @RequestBody PersonalPWEntry entry, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        try {
            UUID userId = UUID.fromString(authentication.getName());
            entry.setUserId(userId);

            String encrypted = AESUtil.encrypt(entry.getPasswordHash());
            entry.setPasswordHash(encrypted);
            entry.setEntryId(entryId);

            service.updatePassword(entry);
            return ResponseEntity.ok("Password updated successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Error: " + e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Encryption error: " + e.getMessage());
        }
    }

    @GetMapping("/entry/{entryId}/decrypt")
    public ResponseEntity<String> getDecryptedPassword(@PathVariable Long entryId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        try {
            UUID userId = UUID.fromString(authentication.getName());
            String decrypted = service.decryptPasswordById(entryId, userId);
            return ResponseEntity.ok(decrypted);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error decrypting password: " + e.getMessage());
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
}
