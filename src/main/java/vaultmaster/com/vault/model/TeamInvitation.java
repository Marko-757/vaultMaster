package vaultmaster.com.vault.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TeamInvitation {
    private UUID invitationId;
    private UUID teamId;
    private String inviteeEmail;
    private String code;
    private boolean accepted;
    private LocalDateTime sentAt;
    private LocalDateTime expiresAt;
}
