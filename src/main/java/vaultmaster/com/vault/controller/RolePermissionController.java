package vaultmaster.com.vault.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.service.RolePermissionService;

import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<String> assignPermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId) {
        rolePermissionService.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok("Permission assigned to role successfully");
    }
}

