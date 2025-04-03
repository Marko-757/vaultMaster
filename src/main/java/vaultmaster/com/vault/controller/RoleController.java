package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.Role;
import vaultmaster.com.vault.service.RoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<Role> createRole(
            @RequestParam UUID teamId,
            @RequestParam String name,
            @RequestParam UUID createdBy
    ) {
        Role role = roleService.createRole(teamId, name, createdBy);
        return ResponseEntity.ok(role);
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<String> addPermission(@PathVariable UUID roleId, @RequestParam String permission) {
        roleService.assignPermission(roleId, permission);
        return ResponseEntity.ok("Permission assigned");
    }

    @PutMapping("/assign")
    public ResponseEntity<String> assignRoleToUser(
            @RequestParam UUID teamId,
            @RequestParam UUID userId,
            @RequestParam UUID roleId
    ) {
        roleService.assignRoleToUser(teamId, userId, roleId);
        return ResponseEntity.ok("Role assigned to user");
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Role>> getRolesForTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(roleService.getRolesForTeam(teamId));
    }
}
