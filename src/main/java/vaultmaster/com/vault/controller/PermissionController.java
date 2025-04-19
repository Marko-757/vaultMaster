package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.ItemPermissionRequest;
import vaultmaster.com.vault.dto.FolderPermissionRequest;
import vaultmaster.com.vault.model.Permission;
import vaultmaster.com.vault.repository.PermissionRepository;
import vaultmaster.com.vault.repository.RoleRepository;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.security.PermissionChecker;
import vaultmaster.com.vault.service.PermissionService;
import vaultmaster.com.vault.service.RolePermissionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;
    private final JwtService jwtService;
    private final PermissionChecker permissionChecker;

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionRepository.findAll());
    }

    @PostMapping("/item")
    public ResponseEntity<?> assignItemPermissions(@RequestBody ItemPermissionRequest request, HttpServletRequest httpRequest) {
        UUID userId = jwtService.getAuthenticatedUserIdAsUUID(httpRequest);
        UUID roleTeamId = roleRepository.findTeamIdByRoleId(request.getRoleId());

        if (!roleTeamId.equals(request.getTeamId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Role does not belong to the specified team.");
        }

        if (!permissionChecker.userHasPermission(userId, request.getTeamId(), "MANAGE_TEAM_ROLES")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            for (String permission : request.getPermissions()) {
                rolePermissionService.assignPermissionToItem(
                        request.getRoleId(),
                        request.getItemId(),
                        request.getItemType(),
                        permission
                );
            }
            return ResponseEntity.ok("Item-level permissions assigned.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/folder")
    public ResponseEntity<?> assignFolderPermissions(@RequestBody FolderPermissionRequest request, HttpServletRequest httpRequest) {
        UUID userId = jwtService.getAuthenticatedUserIdAsUUID(httpRequest);
        UUID roleTeamId = roleRepository.findTeamIdByRoleId(request.getRoleId());

        if (!roleTeamId.equals(request.getTeamId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Role does not belong to the specified team.");
        }

        if (!permissionChecker.userHasPermission(userId, request.getTeamId(), "MANAGE_TEAM_ROLES")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            permissionService.assignFolderPermissions(request);
            return ResponseEntity.ok("Folder-level permissions assigned.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/item")
    public ResponseEntity<?> removeItemPermission(@RequestBody ItemPermissionRequest request, HttpServletRequest httpRequest) {
        UUID userId = jwtService.getAuthenticatedUserIdAsUUID(httpRequest);
        UUID roleTeamId = roleRepository.findTeamIdByRoleId(request.getRoleId());

        if (!roleTeamId.equals(request.getTeamId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Role does not belong to the specified team.");
        }

        if (!permissionChecker.userHasPermission(userId, request.getTeamId(), "MANAGE_TEAM_ROLES")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            for (String permission : request.getPermissions()) {
                rolePermissionService.removePermissionFromItem(
                        request.getRoleId(),
                        request.getItemId(),
                        request.getItemType(),
                        permission
                );
            }
            return ResponseEntity.ok("Item permissions removed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/folder")
    public ResponseEntity<?> removeFolderPermission(@RequestBody FolderPermissionRequest request, HttpServletRequest httpRequest) {
        UUID userId = jwtService.getAuthenticatedUserIdAsUUID(httpRequest);
        UUID roleTeamId = roleRepository.findTeamIdByRoleId(request.getRoleId());

        if (!roleTeamId.equals(request.getTeamId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Role does not belong to the specified team.");
        }

        if (!permissionChecker.userHasPermission(userId, request.getTeamId(), "MANAGE_TEAM_ROLES")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        try {
            for (String permission : request.getPermissions()) {
                rolePermissionService.removePermissionFromFolder(
                        request.getRoleId(),
                        request.getFolderId(),
                        request.getFolderType(),
                        permission
                );
            }
            return ResponseEntity.ok("Folder permissions removed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
