package vaultmaster.com.vault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.Team;
import vaultmaster.com.vault.model.Role;
import vaultmaster.com.vault.model.TeamMember;
import vaultmaster.com.vault.repository.TeamMemberRepository;
import vaultmaster.com.vault.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final RoleService roleService;
    private final TeamMemberService teamMemberService;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public TeamService(
            TeamRepository teamRepository,
            RoleService roleService,
            TeamMemberService teamMemberService,
            TeamMemberRepository teamMemberRepository
    ) {
        this.teamRepository = teamRepository;
        this.roleService = roleService;
        this.teamMemberService = teamMemberService;
        this.teamMemberRepository = teamMemberRepository;
    }

    public Team createTeam(String teamName, UUID createdBy) {
        Team team = new Team();
        team.setTeamName(teamName);
        team.setCreatedBy(createdBy);
        team.setCreatedAt(LocalDateTime.now());
        Team createdTeam = teamRepository.save(team);

        Role adminRole = roleService.getOrCreateAdminRoleForTeam(createdTeam.getTeamId(), createdBy);

        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(createdTeam.getTeamId());
        teamMember.setUserId(createdBy);
        teamMember.setRoleId(adminRole.getRoleId());
        teamMember.setCreatedBy(createdBy.toString());
        teamMember.setModifiedBy(createdBy.toString());
        teamMember.setCreatedDate(LocalDateTime.now());
        teamMember.setModifiedDate(LocalDateTime.now());

        teamMemberService.addTeamMember(teamMember);

        return createdTeam;
    }

    public List<Team> getTeamsByUser(UUID userId) {
        return teamRepository.findByCreatedBy(userId);
    }

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return (List<Team>) teamRepository.findAll();
    }

    public Optional<Team> getTeamById(UUID teamId) {
        return teamRepository.findById(teamId);
    }

    public void deleteTeam(UUID teamId) {
        teamRepository.deleteById(teamId);
    }

    public void removeUserFromTeam(UUID teamId, UUID userId) {
        teamMemberRepository.removeUserFromTeam(teamId, userId);
    }
}
