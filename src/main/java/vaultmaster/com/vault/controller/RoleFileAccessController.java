package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.service.RoleTeamFileAccessService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles/access/files")
public class RoleFileAccessController {

    private final RoleTeamFileAccessService fileAccessService;

    public RoleFileAccessController(RoleTeamFileAccessService fileAccessService) {
        this.fileAccessService = fileAccessService;
    }

    // Grant access
    @PostMapping("/grant")
    public ResponseEntity<String> grantFileAccess(@RequestParam UUID roleId, @RequestParam UUID teamFileId) {
        fileAccessService.grantAccess(roleId, teamFileId);
        return ResponseEntity.ok("Access granted to file.");
    }

    // Revoke access
    @DeleteMapping("/revoke")
    public ResponseEntity<String> revokeFileAccess(@RequestParam UUID roleId, @RequestParam UUID teamFileId) {
        fileAccessService.revokeAccess(roleId, teamFileId);
        return ResponseEntity.ok("Access revoked from file.");
    }

    // Check access
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFileAccess(@RequestParam UUID roleId, @RequestParam UUID teamFileId) {
        boolean hasAccess = fileAccessService.canAccess(roleId, teamFileId);
        return ResponseEntity.ok(hasAccess);
    }

    // List all file IDs accessible by role
    @GetMapping("/{roleId}/files")
    public ResponseEntity<List<UUID>> getFilesByRole(@PathVariable UUID roleId) {
        List<UUID> files = fileAccessService.getAccessibleFileIds(roleId);
        return ResponseEntity.ok(files);
    }
}
