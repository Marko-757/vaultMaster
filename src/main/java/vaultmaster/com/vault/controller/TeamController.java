package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.TeamRequest;
import vaultmaster.com.vault.model.Team;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.TeamService;
import vaultmaster.com.vault.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    public TeamController(TeamService teamService, JwtService jwtService) {
        this.teamService = teamService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamRequest teamRequest, HttpServletRequest request) {
        logger.info("Creating team: {}", teamRequest.getTeamName());
        UUID createdBy = UUID.fromString(jwtService.getAuthenticatedUserId(request));
        Team team = teamService.createTeam(teamRequest.getTeamName(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Team>> getAllTeamsForUser(HttpServletRequest request) {
        try {
            UUID userId = jwtService.getAuthenticatedUserIdAsUUID(request);
            List<Team> teams = teamService.getTeamsByUser(userId);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            logger.error("Failed to fetch teams for user", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<String> removeUserFromTeam(
            @PathVariable UUID teamId,
            @PathVariable UUID userId,
            HttpServletRequest request) {

        UUID actingUserId = jwtService.getAuthenticatedUserIdAsUUID(request);
        Team team = teamService.getTeamById(teamId).orElse(null);

        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }

        if (!team.getCreatedBy().equals(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the team creator can remove users.");
        }

        if (userId.equals(team.getCreatedBy())) {
            return ResponseEntity.badRequest().body("Team creator cannot remove themselves.");
        }

        try {
            teamService.removeUserFromTeam(teamId, userId);
            logger.info("Removed user {} from team {}", userId, teamId);
            return ResponseEntity.ok("User removed from team.");
        } catch (Exception e) {
            logger.error("Failed to remove user from team", e);
            return ResponseEntity.internalServerError().body("Failed to remove user.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable UUID id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/memberships")
    public ResponseEntity<List<Team>> getMemberships(HttpServletRequest request) {
        try {
            UUID userId = jwtService.getAuthenticatedUserIdAsUUID(request);
            List<Team> memberships = teamService.getMembershipsForUser(userId);
            return ResponseEntity.ok(memberships);
        } catch (Exception e) {
            logger.error("Failed to fetch team memberships", e);
            return ResponseEntity.internalServerError().body(List.of()); // better for frontend handling
        }
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Team> updateTeamName(@PathVariable UUID teamId, @RequestBody TeamRequest teamRequest, HttpServletRequest request) {
        UUID userId = jwtService.getAuthenticatedUserIdAsUUID(request);
        Team team = teamService.getTeamById(teamId).orElse(null);

        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!team.getCreatedBy().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        team.setTeamName(teamRequest.getTeamName());
        teamService.updateTeam(team);
        logger.info("Team {} renamed by {}", teamId, userId);
        return ResponseEntity.ok(team);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable UUID teamId, HttpServletRequest request) {
        UUID userId = jwtService.getAuthenticatedUserIdAsUUID(request);
        Team team = teamService.getTeamById(teamId).orElse(null);

        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
        }

        if (!team.getCreatedBy().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this team.");
        }

        teamService.deleteTeam(teamId);
        logger.info("Team {} deleted by {}", teamId, userId);
        return ResponseEntity.ok("Team deleted successfully.");
    }
}
