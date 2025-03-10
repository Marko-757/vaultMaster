package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWFolder;
import vaultmaster.com.vault.service.PersonalPWFolderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/passwords/personal/folder")
public class PersonalPWFolderController {
    private final PersonalPWFolderService service;

    public PersonalPWFolderController(PersonalPWFolderService service) {
        this.service = service;
    }

    @PostMapping
    public String createFolder(@RequestBody PersonalPWFolder folder) {
        if (folder.getFolderId() == null) {
            folder.setFolderId(UUID.randomUUID());
        }
        service.createFolder(folder);
        return "Folder created successfully!";
    }

    @GetMapping("/user/{userId}/folders")
    public List<PersonalPWFolder> getUserFolders(@PathVariable UUID userId) {
        return service.getUserFolders(userId);
    }

    @PutMapping("/{folderId}")
    public String updateFolder(@PathVariable UUID folderId, @RequestBody String newName) {
        int rowsAffected = service.updateFolder(folderId, newName);
        return rowsAffected > 0 ? "Folder updated successfully!" : "Error updating folder.";
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
