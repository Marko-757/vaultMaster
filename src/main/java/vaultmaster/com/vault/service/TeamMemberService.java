package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamMember;
import vaultmaster.com.vault.model.TeamMemberId;
import vaultmaster.com.vault.repository.TeamMemberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    public TeamMember addTeamMember(TeamMember teamMember) {
        teamMember.setCreatedDate(LocalDateTime.now());
        teamMember.setModifiedDate(LocalDateTime.now());
        teamMember.setModifiedBy(teamMember.getCreatedBy());

        // Use the custom insert method
        teamMemberRepository.insert(teamMember);
        return teamMember;
    }

    public List<TeamMember> getTeamMembersByTeamId(UUID teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    public List<TeamMember> getTeamMembersByUserId(UUID userId) {
        return teamMemberRepository.findByUserId(userId);
    }

    public Optional<TeamMember> getTeamMember(UUID teamId, UUID userId) {
        TeamMemberId id = new TeamMemberId(teamId, userId);
        return teamMemberRepository.findById(id);
    }

    public void deleteTeamMember(UUID teamId, UUID userId) {
        TeamMemberId id = new TeamMemberId(teamId, userId);
        teamMemberRepository.deleteById(id);
    }
}
