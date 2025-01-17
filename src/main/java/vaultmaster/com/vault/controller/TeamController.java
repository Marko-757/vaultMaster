package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.TeamRequest;
import vaultmaster.com.vault.model.Team;
import vaultmaster.com.vault.service.TeamService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Create a new team using a JSON request body.
     *
     * @param teamRequest The team request object containing team details.
     * @return ResponseEntity with the created Team.
     */
    @PostMapping
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamRequest teamRequest) {
        try {
            // Create a new team using the request data
            Team team = teamService.createTeam(teamRequest.getTeamName(), teamRequest.getCreatedBy());
            return ResponseEntity.status(HttpStatus.CREATED).body(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Get all teams.
     *
     * @return ResponseEntity with a list of all teams.
     */
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        try {
            List<Team> teams = teamService.getAllTeams();
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get a team by its ID.
     *
     * @param id UUID of the team.
     * @return ResponseEntity with the requested Team, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeamById(@PathVariable UUID id) {
        try {
            Optional<Team> teamOptional = teamService.getTeamById(id);
            if (teamOptional.isPresent()) {
                return ResponseEntity.ok(teamOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Delete a team by its ID.
     *
     * @param id UUID of the team.
     * @return ResponseEntity with a success or error message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable UUID id) {
        try {
            if (teamService.getTeamById(id).isPresent()) {
                teamService.deleteTeam(id);
                return ResponseEntity.ok("Team deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
