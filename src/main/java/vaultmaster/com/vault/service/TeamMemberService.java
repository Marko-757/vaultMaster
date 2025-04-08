package vaultmaster.com.vault.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamMember;
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
        String currentUser = getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        teamMember.setCreatedDate(now);
        teamMember.setModifiedDate(now);
        teamMember.setCreatedBy(currentUser);
        teamMember.setModifiedBy(currentUser);

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
        return teamMemberRepository.findByTeamIdAndUserId(teamId, userId);
    }

    public void deleteTeamMember(UUID teamId, UUID userId) {
        teamMemberRepository.deleteByTeamIdAndUserId(teamId, userId);
    }

    public void updateRole(UUID teamId, UUID userId, UUID roleId) {
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new IllegalArgumentException("Team member not found.");
        }

        teamMemberRepository.assignRole(teamId, userId, roleId);

        // Update modified metadata
        String currentUser = getCurrentUsername();
        teamMemberRepository.updateModifiedInfo(teamId, userId, currentUser, LocalDateTime.now());
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system"; // fallback if something weird happens
    }
}
