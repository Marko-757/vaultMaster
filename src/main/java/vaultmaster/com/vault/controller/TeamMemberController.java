package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.TeamMember;
import vaultmaster.com.vault.service.TeamMemberService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams/members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    /**
     * Add a new team member.
     *
     * @param teamMember The team member object.
     * @return ResponseEntity with the created TeamMember.
     */
    @PostMapping
    public ResponseEntity<?> addTeamMember(@RequestBody TeamMember teamMember) {
        try {
            // Pass the full TeamMember object to the service
            TeamMember createdMember = teamMemberService.addTeamMember(teamMember);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
}
