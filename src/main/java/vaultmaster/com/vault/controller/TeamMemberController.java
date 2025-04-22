package vaultmaster.com.vault.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.TeamMemberProfile;
import vaultmaster.com.vault.model.TeamMember;
import vaultmaster.com.vault.service.TeamMemberService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams/members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @PostMapping
    public ResponseEntity<TeamMember> addTeamMember(@Valid @RequestBody TeamMember teamMember) {
        try {
            TeamMember createdMember = teamMemberService.addTeamMember(teamMember);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all team members by team ID.
     *
     * @param teamId The UUID of the team.
     * @return ResponseEntity with a list of TeamMember objects.
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMember>> getTeamMembersByTeamId(@PathVariable UUID teamId) {
        try {
            List<TeamMember> teamMembers = teamMemberService.getTeamMembersByTeamId(teamId);
            return ResponseEntity.ok(teamMembers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all team members by user ID.
     *
     * @param userId The UUID of the user.
     * @return ResponseEntity with a list of TeamMember objects.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TeamMember>> getTeamMembersByUserId(@PathVariable UUID userId) {
        try {
            List<TeamMember> teamMembers = teamMemberService.getTeamMembersByUserId(userId);
            return ResponseEntity.ok(teamMembers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a single team member by team ID and user ID.
     *
     * @param teamId The UUID of the team.
     * @param userId The UUID of the user.
     * @return ResponseEntity with the found TeamMember.
     */
    @GetMapping("/{teamId}/{userId}")
    public ResponseEntity<TeamMember> getTeamMember(@PathVariable UUID teamId, @PathVariable UUID userId) {
        try {
            Optional<TeamMember> member = teamMemberService.getTeamMember(teamId, userId);
            return member.map(ResponseEntity::ok).orElseGet(() ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update a team member's role.
     *
     * @param teamId The UUID of the team.
     * @param userId The UUID of the user.
     * @param roleId The UUID of the new role.
     * @return ResponseEntity with success or error message.
     */
    @PutMapping("/{teamId}/{userId}/role")
    public ResponseEntity<String> updateRole(@PathVariable UUID teamId,
                                             @PathVariable UUID userId,
                                             @RequestParam UUID roleId) {
        try {
            teamMemberService.updateRole(teamId, userId, roleId);
            return ResponseEntity.ok("Role updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Delete a team member using team ID and user ID.
     *
     * @param teamId The UUID of the team.
     * @param userId The UUID of the user.
     * @return ResponseEntity with a success or error message.
     */
    @DeleteMapping("/{teamId}/{userId}")
    public ResponseEntity<String> deleteTeamMember(@PathVariable UUID teamId, @PathVariable UUID userId) {
        try {
            teamMemberService.deleteTeamMember(teamId, userId);
            return ResponseEntity.ok("Team member removed successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/user/{userId}/roles")
    public ResponseEntity<List<Map<String, Object>>> getUserRoles(@PathVariable UUID userId) {
        try {
            List<Map<String, Object>> roles = teamMemberService.getRolesByUserId(userId);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/team/{teamId}/profiles")
    public ResponseEntity<List<TeamMemberProfile>> getTeamMemberProfiles(@PathVariable UUID teamId) {
        try {
            List<TeamMemberProfile> profiles = teamMemberService.getTeamMemberProfiles(teamId);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
