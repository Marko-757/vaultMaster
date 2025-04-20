package vaultmaster.com.vault.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import vaultmaster.com.vault.model.TeamInvitation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.time.LocalDateTime;

public class TeamInvitationRowMapper implements RowMapper<TeamInvitation> {
    @Override
    public TeamInvitation mapRow(ResultSet rs, int rowNum) throws SQLException {
        TeamInvitation invite = new TeamInvitation();
        invite.setInvitationId(UUID.fromString(rs.getString("invitation_id")));
        invite.setTeamId(UUID.fromString(rs.getString("team_id")));
        invite.setInviteeEmail(rs.getString("invitee_email"));
        invite.setCode(rs.getString("code"));
        invite.setAccepted(rs.getBoolean("accepted"));
        invite.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime());
        invite.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        return invite;
    }
}
