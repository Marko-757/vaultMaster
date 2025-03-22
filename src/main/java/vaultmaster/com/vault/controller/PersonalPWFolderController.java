package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWFolder;
import vaultmaster.com.vault.service.PersonalPWFolderService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/passwords/personal/folder")
public class PersonalPWFolderController {

    private final PersonalPWFolderService folderService;

    public PersonalPWFolderController(PersonalPWFolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public ResponseEntity<PersonalPWFolder> createFolder(@RequestBody PersonalPWFolder folder, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        folder.setUserId(userId);

        folderService.createFolder(folder);
        return ResponseEntity.ok(folder);
    }

    @GetMapping("/me/folders")
    public ResponseEntity<List<PersonalPWFolder>> getUserFolders(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        List<PersonalPWFolder> folders = folderService.getUserFolders(userId);
        return ResponseEntity.ok(folders);
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<String> updateFolder(
            @PathVariable UUID folderId,
            @RequestBody Map<String, String> payload
    ) {
        String newName = payload.get("folderName");
        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Folder name cannot be empty");
        }

        int rowsAffected = folderService.updateFolder(folderId, newName.trim());
        return rowsAffected > 0
                ? ResponseEntity.ok("Folder updated successfully!")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found.");
    }


    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable UUID folderId) {
        try {
            folderService.deleteFolder(folderId);
            return ResponseEntity.ok("Folder deleted successfully!");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
