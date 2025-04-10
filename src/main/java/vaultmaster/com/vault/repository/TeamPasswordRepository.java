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
            INSERT INTO team_passwords
            (team_id, entry_id, folder_id, created_by, created_at, modified_by, modified_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)
            RETURNING team_password_id
        """;

        int id = jdbc.queryForObject(sql, Integer.class,
                pw.getTeamId(),
                pw.getEntryId(),
                pw.getFolderId(),
                pw.getCreatedBy(),
                pw.getModifiedBy(),
                Timestamp.valueOf(pw.getModifiedAt())
        );

        pw.setTeamPasswordId(id);
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

    public void updateModifiedAt(int teamPasswordId) {
        String sql = "UPDATE team_passwords SET modified_at = CURRENT_TIMESTAMP WHERE team_password_id = ?";
        jdbc.update(sql, teamPasswordId);
    }

    public void delete(int id) {
        jdbc.update("DELETE FROM team_passwords WHERE team_password_id = ?", id);
    }

    public int updateFolder(UUID folderId, int teamPasswordId) {
        String sql = "UPDATE team_passwords SET folder_id = ?, modified_at = CURRENT_TIMESTAMP WHERE team_password_id = ?";
        return jdbc.update(sql, folderId, teamPasswordId);
    }

    public List<TeamPassword> findByFolderId(UUID folderId) {
        String sql = """
        SELECT tp.team_password_id, tp.team_id, tp.entry_id, tp.folder_id,
               tp.created_by, tp.created_at, tp.modified_at,
               pe.account_name, pe.username, pe.url, pe.password_hash
        FROM team_passwords tp
        JOIN password_entries pe ON tp.entry_id = pe.entry_id
        WHERE tp.folder_id = ?
        ORDER BY pe.account_name
    """;

        return jdbc.query(sql, (rs, rowNum) -> {
            TeamPassword pw = new TeamPassword();
            pw.setTeamPasswordId(rs.getInt("team_password_id"));
            pw.setTeamId(UUID.fromString(rs.getString("team_id")));
            pw.setEntryId(rs.getInt("entry_id"));
            pw.setFolderId(folderId);
            pw.setCreatedBy(UUID.fromString(rs.getString("created_by")));
            pw.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            pw.setModifiedAt(rs.getTimestamp("modified_at") != null
                    ? rs.getTimestamp("modified_at").toLocalDateTime()
                    : null);
            pw.setAccountName(rs.getString("account_name"));
            pw.setUsername(rs.getString("username"));
            pw.setWebsite(rs.getString("url"));
            pw.setEncryptedPassword(rs.getString("password_hash"));
            return pw;
        }, folderId);
    }

    public Optional<UUID> findTeamIdByEntryId(int entryId) {
        String sql = "SELECT team_id FROM team_passwords WHERE entry_id = ?";
        try {
            UUID teamId = jdbc.queryForObject(sql, UUID.class, entryId);
            return Optional.of(teamId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }



}
