package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.TeamRequest;
import vaultmaster.com.vault.model.Team;
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
            UUID userId = UUID.fromString(jwtService.getAuthenticatedUserId(request));
            List<Team> teams = teamService.getTeamsByUser(userId);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable UUID id) {
        Team team = teamService.getTeamById(id).orElse(null);
        if (team == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(team);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Team> updateTeamName(@PathVariable UUID teamId, @RequestBody TeamRequest teamRequest, HttpServletRequest request) {
        UUID userId = UUID.fromString(jwtService.getAuthenticatedUserId(request));
        Team team = teamService.getTeamById(teamId).orElse(null);
        if (team != null && team.getCreatedBy().equals(userId)) {
            team.setTeamName(teamRequest.getTeamName());
            teamService.updateTeam(team);
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable UUID teamId, HttpServletRequest request) {
        UUID userId = UUID.fromString(jwtService.getAuthenticatedUserId(request));
        Team team = teamService.getTeamById(teamId).orElse(null);
        if (team != null && team.getCreatedBy().equals(userId)) {
            teamService.deleteTeam(teamId);
            return ResponseEntity.ok("Team deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this team.");
        }
    }
}
