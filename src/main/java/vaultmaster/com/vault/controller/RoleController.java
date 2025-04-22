package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.RoleRequest;
import vaultmaster.com.vault.model.Permission;
import vaultmaster.com.vault.model.Role;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.service.RolePermissionService;
import vaultmaster.com.vault.service.RoleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    public RoleController(RoleService roleService, RolePermissionService rolePermissionService, JwtService jwtService) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        try {
            UUID createdBy = UUID.fromString(jwtService.getAuthenticatedUserId(request));

            // Check for duplicates
            Optional<Role> existing = roleService.findByNameAndTeamId(roleRequest.getRoleName(), roleRequest.getTeamId());
            if (existing.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }

            Role role = new Role();
            role.setRoleId(UUID.randomUUID());
            role.setTeamId(roleRequest.getTeamId());
            role.setRoleName(roleRequest.getRoleName());
            role.setCreatedBy(createdBy);
            role.setCreatedAt(LocalDateTime.now());

            Role saved = roleService.createRole(role);
            logger.info("Role {} created in team {}", role.getRoleName(), role.getTeamId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            logger.error("Error creating role:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<String> assignPermissionsToRole(
            @PathVariable UUID roleId,
            @RequestBody List<String> permissions) {
        try {
            if (permissions == null || permissions.isEmpty()) {
                return ResponseEntity.badRequest().body("No permissions provided.");
            }

            roleService.assignPermissionsToRole(roleId, permissions);
            logger.info("Permissions assigned to role {}", roleId);
            return ResponseEntity.ok("Permissions assigned to role successfully.");
        } catch (Exception e) {
            logger.error("Error assigning permissions to role:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error assigning permissions.");
        }
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<List<Permission>> getPermissionsForRole(@PathVariable UUID roleId) {
        try {
            return ResponseEntity.ok(rolePermissionService.getPermissionsForRole(roleId));
        } catch (Exception e) {
            logger.error("Error retrieving permissions for role:", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Role>> getRolesForTeam(@PathVariable UUID teamId) {
        try {
            List<Role> rolesWithPermissions = roleService.getRolesWithPermissionsByTeamId(teamId);
            return ResponseEntity.ok(rolesWithPermissions);
        } catch (Exception e) {
            logger.error("Error retrieving roles for team:", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/assign")
    public ResponseEntity<String> assignRoleToUser(
            @RequestParam UUID teamId,
            @RequestParam UUID userId,
            @RequestParam UUID roleId
    ) {
        try {
            roleService.assignRoleToUser(teamId, userId, roleId);
            logger.info("Assigned role {} to user {} in team {}", roleId, userId, teamId);
            return ResponseEntity.ok("Role assigned to user");
        } catch (Exception e) {
            logger.error("Error assigning role to user:", e);
            return ResponseEntity.internalServerError().body("Error assigning role to user");
        }
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID roleId) {
        return roleService.getRoleById(roleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/exists/{roleId}")
    public ResponseEntity<Boolean> roleExists(@PathVariable UUID roleId) {
        return ResponseEntity.ok(roleService.roleExists(roleId));
    }

    @GetMapping("/find")
    public ResponseEntity<Role> findByNameAndTeam(
            @RequestParam String roleName,
            @RequestParam UUID teamId
    ) {
        Optional<Role> role = roleService.findByNameAndTeamId(roleName, teamId);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{roleId}/rename")
    public ResponseEntity<?> renameRole(
            @PathVariable UUID roleId,
            @RequestParam String newName
    ) {
        try {
            roleService.renameRole(roleId, newName);
            logger.info("Renamed role {} to {}", roleId, newName);
            return ResponseEntity.ok("Role renamed successfully.");
        } catch (Exception e) {
            logger.error("Error renaming role:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to rename role.");
        }
    }

    @PutMapping("/remove")
    public ResponseEntity<String> removeRoleFromUser(
            @RequestParam UUID teamId,
            @RequestParam UUID userId
    ) {
        try {
            roleService.removeRoleFromUser(teamId, userId);
            logger.info("Removed role from user {} in team {}", userId, teamId);
            return ResponseEntity.ok("Role removed from user");
        } catch (Exception e) {
            logger.error("Error removing role from user:", e);
            return ResponseEntity.internalServerError().body("Error removing role from user");
        }
    }
}
