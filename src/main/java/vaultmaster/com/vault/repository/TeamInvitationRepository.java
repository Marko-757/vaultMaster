package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.TeamInvitation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TeamInvitationRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamInvitationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(TeamInvitation invitation) {
        String sql = """
            INSERT INTO team_invitations (invitation_id, team_id, email, code, created_at, expires_at, used)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                invitation.getInvitationId(),
                invitation.getTeamId(),
                invitation.getInviteeEmail(),
                invitation.getCode(),
                invitation.getSentAt(),
                invitation.getExpiresAt(),
                invitation.isAccepted()
        );
    }

    public Optional<TeamInvitation> findByEmailAndCode(String email, String code) {
        String sql = """
            SELECT * FROM team_invitations
            WHERE email = ? AND code = ? AND used = false
        """;

        try {
            TeamInvitation invitation = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), email, code);
            return Optional.ofNullable(invitation);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void markAsAccepted(UUID invitationId) {
        String sql = """
            UPDATE team_invitations
            SET used = true
            WHERE invitation_id = ?
        """;

        jdbcTemplate.update(sql, invitationId);
    }

    public void deleteExpiredInvitations() {
        String sql = """
            DELETE FROM team_invitations
            WHERE expires_at < NOW()
        """;

        jdbcTemplate.update(sql);
    }

    private TeamInvitation mapRow(ResultSet rs) throws SQLException {
        TeamInvitation invitation = new TeamInvitation();
        invitation.setInvitationId(UUID.fromString(rs.getString("invitation_id")));
        invitation.setTeamId(UUID.fromString(rs.getString("team_id")));
        invitation.setInviteeEmail(rs.getString("email"));
        invitation.setCode(rs.getString("code"));
        invitation.setSentAt(rs.getTimestamp("created_at").toLocalDateTime());
        invitation.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        invitation.setAccepted(rs.getBoolean("used"));
        return invitation;
    }
}
