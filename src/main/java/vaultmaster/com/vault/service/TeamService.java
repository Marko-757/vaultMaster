package vaultmaster.com.vault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.Team;
import vaultmaster.com.vault.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // Create a team using a Team object
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    // Create a team using individual arguments
    public Team createTeam(String teamName, UUID createdBy) {
        Team team = new Team();
        team.setTeamName(teamName);
        team.setCreatedAt(LocalDateTime.now());
        team.setCreatedBy(createdBy);

        return teamRepository.save(team);
    }

    // Get all teams
    public List<Team> getAllTeams() {
        return (List<Team>) teamRepository.findAll();
    }

    // Get a team by its ID
    public Optional<Team> getTeamById(UUID teamId) {
        return teamRepository.findById(teamId);
    }

    // Delete a team by its ID
    public void deleteTeam(UUID teamId) {
        teamRepository.deleteById(teamId);
    }
}
