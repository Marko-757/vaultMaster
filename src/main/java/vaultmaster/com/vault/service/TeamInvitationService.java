package vaultmaster.com.vault.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamInvitation;
import vaultmaster.com.vault.repository.TeamInvitationRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamInvitationService {

    private static final Logger logger = LoggerFactory.getLogger(TeamInvitationService.class);

    private final TeamInvitationRepository invitationRepository;
    private final EmailService emailService;

    public TeamInvitationService(TeamInvitationRepository invitationRepository, EmailService emailService) {
        this.invitationRepository = invitationRepository;
        this.emailService = emailService;
    }

    public TeamInvitation createInvitation(UUID teamId, String email) {
        TeamInvitation invitation = new TeamInvitation();
        invitation.setInvitationId(UUID.randomUUID());
        invitation.setTeamId(teamId);
        invitation.setInviteeEmail(email);
        invitation.setCode(generate6DigitCode());
        invitation.setSentAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        invitation.setAccepted(false);

        invitationRepository.save(invitation);
        logger.info("Created team invitation for email={} on team={}", email, teamId);
        return invitation;
    }

    public Optional<TeamInvitation> verifyInvitationCode(String email, String code) {
        logger.debug("Verifying code={} for email={}", code, email);
        Optional<TeamInvitation> invitation = invitationRepository.findByEmailAndCode(email, code);

        if (invitation.isEmpty()) {
            logger.warn("Invitation not found for email={} and code={}", email, code);
            throw new IllegalArgumentException("No invitation found for this email and code.");
        }

        if (invitation.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("Invitation expired for email={} with code={}", email, code);
            throw new IllegalArgumentException("Invitation has expired.");
        }

        return invitation;
    }

    public TeamInvitation validateInvitation(String email, String code) {
        return verifyInvitationCode(email, code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired invitation code"));
    }

    public void markInvitationAsAccepted(UUID invitationId) {
        invitationRepository.markAsAccepted(invitationId);
        logger.info("Marked invitation {} as accepted", invitationId);
    }

    public void markAsUsed(UUID invitationId) {
        markInvitationAsAccepted(invitationId); // alias
    }

    public void cleanupExpiredInvitations() {
        invitationRepository.deleteExpiredInvitations();
        logger.info("Manually cleaned up expired invitations.");
    }

    private String generate6DigitCode() {
        int code = 100000 + (int)(Math.random() * 900000);
        return String.valueOf(code);
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void cleanupExpiredInvitationsJob() {
        invitationRepository.deleteExpiredInvitations();
        logger.info("[⏰] Scheduled cleanup of expired team invitations completed.");
    }
}
