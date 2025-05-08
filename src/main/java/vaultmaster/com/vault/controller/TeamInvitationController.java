package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.*;
import vaultmaster.com.vault.repository.TeamRepository;
import vaultmaster.com.vault.security.*;
import vaultmaster.com.vault.service.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/team-invitations")
@RequiredArgsConstructor
public class TeamInvitationController {

    private static final Logger logger = LoggerFactory.getLogger(TeamInvitationController.class);

    private final TeamInvitationService invitationService;
    private final TeamMemberService teamMemberService;
    private final JwtService jwtService;
    private final UserService userService;
    private final PermissionChecker permissionChecker;
    private final TeamRepository teamRepository;
    private final EmailService emailService;
    private final RoleService roleService;

    @PostMapping("/send")
    public ResponseEntity<?> sendInvitation(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            UUID inviterId = jwtService.getAuthenticatedUserIdAsUUID(httpRequest);
            UUID teamId = UUID.fromString(request.get("teamId"));
            String email = request.get("email");

            if (!permissionChecker.userHasPermission(inviterId, teamId, "INVITE_TEAM_MEMBER")) {
                logger.warn("User {} lacks permission to invite to team {}", inviterId, teamId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to invite team members.");
            }

            TeamInvitation invitation = invitationService.createInvitation(teamId, email);
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Team not found"));

            String subject = "You're Invited to Join the VaultMaster Team: " + team.getTeamName();
            String body = String.format("""
                Hi there,

                You've been invited to join the team **%s** on VaultMaster.

                Invitation Details:
                • Team Name: %s
                • Verification Code: %s

                To accept the invitation, log into your VaultMaster account associated with this email and enter the code on the team invitation page.

                This code expires in 15 minutes.

                If you did not expect this invitation, you can ignore this email.

                VaultMaster Security Team
                """, team.getTeamName(), team.getTeamName(), invitation.getCode());

            emailService.sendEmail(email, subject, body);
            logger.info("Invitation sent by {} to {}", inviterId, email);
            return ResponseEntity.ok("Invitation sent to " + email);

        } catch (Exception e) {
            logger.error("Error sending invitation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send invitation.");
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvitation(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String email = jwtService.getAuthenticatedEmail(httpRequest);
        String code = request.get("code");

        try {
            TeamInvitation invitation = invitationService.validateInvitation(email, code);
            UUID userId = jwtService.getAuthenticatedUserIdAsUUID(httpRequest);
            UUID teamId = invitation.getTeamId();

            if (userService.getUserByEmail(email).isEmpty()) {
                logger.warn("Email not associated with any user: {}", email);
                return ResponseEntity.badRequest().body("Account not found for email: " + email);
            }

            // Add member first
            TeamMember newMember = new TeamMember();
            newMember.setTeamId(teamId);
            newMember.setUserId(userId);
            teamMemberService.addTeamMember(newMember);

            // Assign the "Pending" role
            Role pendingRole = roleService.findByNameAndTeamId("Pending", teamId)
                    .orElseThrow(() -> new RuntimeException("Pending role not found"));
            roleService.assignRoleToUser(teamId, userId, pendingRole.getRoleId());

            invitationService.markAsUsed(invitation.getInvitationId());

            logger.info("User {} successfully joined team {}", userId, teamId);
            return ResponseEntity.ok("You’ve successfully joined the team.");
        } catch (IllegalArgumentException e) {
            logger.warn("Invitation validation failed for {}: {}", email, e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error accepting invitation for {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to accept invitation.");
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyInvitation(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String email = jwtService.getAuthenticatedEmail(request);
        String code = body.get("code");

        try {
            TeamInvitation invitation = invitationService.validateInvitation(email, code);

            if (invitation.isAccepted()) {
                logger.warn("Attempted reuse of invitation: {}", invitation.getInvitationId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invitation already used.");
            }

            if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
                logger.warn("Expired invitation used for email={} code={}", email, code);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invitation has expired.");
            }

            UUID userId = jwtService.getAuthenticatedUserIdAsUUID(request);
            TeamMember newMember = new TeamMember();
            newMember.setTeamId(invitation.getTeamId());
            newMember.setUserId(userId);

            teamMemberService.addTeamMember(newMember);
            invitationService.markAsUsed(invitation.getInvitationId());

            logger.info("Verified and accepted invitation for user {} to team {}", userId, invitation.getTeamId());
            return ResponseEntity.ok("You have joined the team!");
        } catch (IllegalArgumentException e) {
            logger.warn("Invitation verification failed for {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
