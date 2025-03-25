package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.PersonalFileFolder;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.service.PersonalFileFolderService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files/folders")
@RequiredArgsConstructor
public class PersonalFileFolderController {

    private final PersonalFileFolderService folderService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createFolder(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String folderName = requestBody.get("folderName");
        UUID userId = UUID.fromString(jwtService.getAuthenticatedUserId(request));
        folderService.createFolder(userId, folderName);
        return ResponseEntity.ok(Map.of("message", "Folder created successfully"));
    }

    @GetMapping
    public ResponseEntity<List<PersonalFileFolder>> getFoldersForUser(HttpServletRequest request) {
        UUID userId = UUID.fromString(jwtService.getAuthenticatedUserId(request));
        List<PersonalFileFolder> folders = folderService.getFoldersByUser(userId);
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<PersonalFileFolder> getFolderById(@PathVariable UUID folderId) {
        return ResponseEntity.ok(folderService.getFolderById(folderId));
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<Map<String, String>> renameFolder(@PathVariable UUID folderId, @RequestBody Map<String, String> requestBody) {
        String newName = requestBody.get("folderName");
        folderService.updateFolderName(folderId, newName);
        return ResponseEntity.ok(Map.of("message", "Folder renamed successfully"));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Map<String, String>> deleteFolder(@PathVariable UUID folderId) {
        folderService.deleteFolder(folderId);
        return ResponseEntity.ok(Map.of("message", "Folder deleted successfully"));
    }
}
