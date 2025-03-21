package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWFolder;
import vaultmaster.com.vault.service.PersonalPWFolderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passwords/personal/folder")
public class PersonalPWFolderController {
    private final PersonalPWFolderService service;

    public PersonalPWFolderController(PersonalPWFolderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> createFolder(@RequestBody PersonalPWFolder folder, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        UUID userId = UUID.fromString(authentication.getName());
        folder.setUserId(userId);

        if (folder.getFolderId() == null) {
            folder.setFolderId(UUID.randomUUID());
        }

        service.createFolder(folder);
        return ResponseEntity.ok("Folder created successfully!");
    }

    @GetMapping
    public ResponseEntity<List<PersonalPWFolder>> getUserFolders(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        List<PersonalPWFolder> folders = service.getUserFolders(userId);
        return ResponseEntity.ok(folders);
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<String> updateFolder(@PathVariable UUID folderId, @RequestBody String newName) {
        int rowsAffected = service.updateFolder(folderId, newName);
        return rowsAffected > 0
                ? ResponseEntity.ok("Folder updated successfully!")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found.");
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable UUID folderId) {
        try {
            service.deleteFolder(folderId);
            return ResponseEntity.ok("Folder deleted successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
