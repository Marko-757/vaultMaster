package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.service.RoleTeamPasswordAccessService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles/access/passwords")
public class RolePasswordAccessController {

    private final RoleTeamPasswordAccessService passwordAccessService;

    public RolePasswordAccessController(RoleTeamPasswordAccessService passwordAccessService) {
        this.passwordAccessService = passwordAccessService;
    }

    // Grant access
    @PostMapping("/grant")
    public ResponseEntity<String> grantPasswordAccess(@RequestParam UUID roleId, @RequestParam int teamPasswordId) {
        passwordAccessService.grantAccess(roleId, teamPasswordId);
        return ResponseEntity.ok("Access granted to password.");
    }

    // Revoke access
    @DeleteMapping("/revoke")
    public ResponseEntity<String> revokePasswordAccess(@RequestParam UUID roleId, @RequestParam int teamPasswordId) {
        passwordAccessService.revokeAccess(roleId, teamPasswordId);
        return ResponseEntity.ok("Access revoked from password.");
    }

    // Check access
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkPasswordAccess(@RequestParam UUID roleId, @RequestParam int teamPasswordId) {
        boolean hasAccess = passwordAccessService.canAccess(roleId, teamPasswordId);
        return ResponseEntity.ok(hasAccess);
    }

    // List all password IDs accessible by role
    @GetMapping("/{roleId}/passwords")
    public ResponseEntity<List<Integer>> getPasswordsByRole(@PathVariable UUID roleId) {
        List<Integer> passwords = passwordAccessService.getAccessiblePasswordIds(roleId);
        return ResponseEntity.ok(passwords);
    }
}
