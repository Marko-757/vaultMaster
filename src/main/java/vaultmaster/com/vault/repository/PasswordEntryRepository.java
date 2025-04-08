package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.PasswordEntry;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordEntryRepository {

    private final JdbcTemplate jdbcTemplate;

    public PasswordEntryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PasswordEntry> rowMapper = (rs, rowNum) -> {
        PasswordEntry entry = new PasswordEntry();
        entry.setEntryId(rs.getInt("entry_id"));
        entry.setUserId(UUID.fromString(rs.getString("user_id")));
        entry.setAccountName(rs.getString("account_name"));
        entry.setUsername(rs.getString("username"));
        entry.setPasswordHash(rs.getString("password_hash"));
        entry.setWebsite(rs.getString("url"));
        entry.setFolderId(rs.getObject("folder_id") != null ? UUID.fromString(rs.getString("folder_id")) : null);
        entry.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        entry.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return entry;
    };

    public PasswordEntry save(PasswordEntry entry) {
        String sql = """
        INSERT INTO password_entries
        (user_id, account_name, username, password_hash, url, folder_id, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING entry_id
    """;

        int entryId = jdbcTemplate.queryForObject(sql, new Object[]{
                entry.getUserId(),
                entry.getAccountName(),
                entry.getUsername(),
                entry.getPasswordHash(),
                entry.getWebsite(),
                entry.getFolderId(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        }, Integer.class);

        entry.setEntryId(entryId);
        return entry;
    }

    public Optional<PasswordEntry> findById(int entryId) {
        String sql = "SELECT * FROM password_entries WHERE entry_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, entryId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<PasswordEntry> findByUserId(UUID userId) {
        String sql = "SELECT * FROM password_entries WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<PasswordEntry> findByTeamId(UUID teamId) {
        String sql = """
            SELECT pe.* FROM password_entries pe
            JOIN team_passwords tp ON tp.entry_id = pe.entry_id
            WHERE tp.team_id = ?
        """;
        return jdbcTemplate.query(sql, rowMapper, teamId);
    }

    public void update(int entryId, PasswordEntry entry) {
        String sql = """
            UPDATE password_entries
            SET account_name = ?, username = ?, password_hash = ?, url = ?, folder_id = ?, updated_at = ?
            WHERE entry_id = ?
        """;

        jdbcTemplate.update(sql,
                entry.getAccountName(),
                entry.getUsername(),
                entry.getPasswordHash(),
                entry.getWebsite(),
                entry.getFolderId(),
                Timestamp.valueOf(entry.getUpdatedAt()),
                entryId
        );
    }

    public void delete(int entryId) {
        jdbcTemplate.update("DELETE FROM password_entries WHERE entry_id = ?", entryId);
    }
}
