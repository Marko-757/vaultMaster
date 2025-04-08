package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.TeamPassword;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TeamPasswordRepository {

    private final JdbcTemplate jdbc;

    public TeamPasswordRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public TeamPassword save(TeamPassword pw) {
        String sql = """
            INSERT INTO team_passwords (team_id, entry_id, folder_id, created_by, created_at, modified_by, modified_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)
        """;

        jdbc.update(sql,
                pw.getTeamId(),
                pw.getEntryId(),
                pw.getFolderId(),
                pw.getCreatedBy(),
                pw.getModifiedBy(),
                Timestamp.valueOf(pw.getModifiedAt())
        );
        return pw;
    }

    public Optional<TeamPassword> findById(int id) {
        String sql = """
            SELECT tp.team_password_id, tp.team_id, tp.entry_id, tp.folder_id,
                   tp.created_by, tp.created_at, tp.modified_by, tp.modified_at,
                   pe.password_hash
            FROM team_passwords tp
            JOIN password_entries pe ON tp.entry_id = pe.entry_id
            WHERE tp.team_password_id = ?
        """;

        return jdbc.query(sql, rs -> {
            if (rs.next()) {
                TeamPassword pw = new TeamPassword();
                pw.setTeamPasswordId(rs.getInt("team_password_id"));
                pw.setTeamId(UUID.fromString(rs.getString("team_id")));
                pw.setEntryId(rs.getInt("entry_id"));
                pw.setFolderId(rs.getObject("folder_id", UUID.class));
                pw.setCreatedBy(UUID.fromString(rs.getString("created_by")));
                pw.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                pw.setModifiedBy(rs.getObject("modified_by", UUID.class));
                pw.setModifiedAt(rs.getTimestamp("modified_at") != null
                        ? rs.getTimestamp("modified_at").toLocalDateTime()
                        : null);
                pw.setEncryptedPassword(rs.getString("password_hash"));
                return Optional.of(pw);
            } else {
                return Optional.empty();
            }
        }, id);
    }

    public List<TeamPassword> findByTeamId(UUID teamId) {
        String sql = """
            SELECT tp.team_password_id, tp.team_id, tp.entry_id, tp.folder_id,
                   tp.created_by, tp.created_at, tp.modified_by, tp.modified_at,
                   pe.password_hash
            FROM team_passwords tp
            JOIN password_entries pe ON tp.entry_id = pe.entry_id
            WHERE tp.team_id = ?
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            TeamPassword pw = new TeamPassword();
            pw.setTeamPasswordId(rs.getInt("team_password_id"));
            pw.setTeamId(UUID.fromString(rs.getString("team_id")));
            pw.setEntryId(rs.getInt("entry_id"));
            pw.setFolderId(rs.getObject("folder_id", UUID.class));
            pw.setCreatedBy(UUID.fromString(rs.getString("created_by")));
            pw.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            pw.setModifiedBy(rs.getObject("modified_by", UUID.class));
            pw.setModifiedAt(rs.getTimestamp("modified_at") != null
                    ? rs.getTimestamp("modified_at").toLocalDateTime()
                    : null);
            pw.setEncryptedPassword(rs.getString("password_hash"));
            return pw;
        }, teamId);
    }

    public void delete(int id) {
        jdbc.update("DELETE FROM team_passwords WHERE team_password_id = ?", id);
    }
}
