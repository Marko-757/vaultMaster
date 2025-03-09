package vaultmaster.com.vault.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.service.PersonalPWService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<String> addPassword(@RequestBody PersonalPWEntry entry) {
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

        if (entry.getUserId() == null) missingFields.add("userId");
        if (entry.getAccountName() == null || entry.getAccountName().trim().isEmpty()) missingFields.add("accountName");
        if (entry.getUsername() == null || entry.getUsername().trim().isEmpty()) missingFields.add("username"); // ✅ Fix: Check for username
        if (entry.getPasswordHash() == null || entry.getPasswordHash().trim().isEmpty()) missingFields.add("passwordHash");

        return String.join(", ", missingFields); // Return all missing fields as a comma-separated string
    }





    @GetMapping("/user/{userId}")
    public List<PersonalPWEntry> getUserPasswords(@PathVariable UUID userId) {
        return service.getUserPasswords(userId);
    }

    @GetMapping("/entry/{entryId}")
    public PersonalPWEntry getPasswordById(@PathVariable Long entryId) {
        return service.getPasswordById(entryId);
    }

    @DeleteMapping("/entry/{entryId}")
    public ResponseEntity<String> deletePassword(@PathVariable Long entryId) {
        try {
            service.deletePassword(entryId);
            return ResponseEntity.ok("Password deleted successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getReason());
        }
    }

}
