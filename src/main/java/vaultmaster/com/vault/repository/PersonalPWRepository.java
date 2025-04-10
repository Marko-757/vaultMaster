package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.rowmapper.PersonalPWRowMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class PersonalPWRepository {
    private final JdbcTemplate jdbcTemplate;

    public PersonalPWRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PersonalPWEntry insertPassword(PersonalPWEntry entry) {
        String sql = """
        INSERT INTO password_entries 
        (user_id, account_name, username, password_hash, url, folder_id, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING entry_id
    """;

        // Set timestamps if not already set
        if (entry.getCreatedAt() == null) entry.setCreatedAt(LocalDateTime.now());
        if (entry.getUpdatedAt() == null) entry.setUpdatedAt(LocalDateTime.now());

        Long generatedId = jdbcTemplate.queryForObject(sql, new Object[]{
                entry.getUserId(),
                entry.getAccountName(),
                entry.getUsername(),
                entry.getPasswordHash(),
                entry.getWebsite(),
                entry.getFolderId(),
                Timestamp.valueOf(entry.getCreatedAt()),
                Timestamp.valueOf(entry.getUpdatedAt())
        }, Long.class);

        entry.setEntryId(generatedId);
        return entry;
    }


    public int deletePassword(Long entryId) {
        String sql = "DELETE FROM password_entries WHERE entry_id = ?";
        return jdbcTemplate.update(sql, entryId);
    }

    public int updatePassword(PersonalPWEntry entry) {
        String sql = """
            UPDATE password_entries
            SET account_name = ?, username = ?, password_hash = ?, url = ?, folder_id = ?, updated_at = NOW()
            WHERE entry_id = ? AND user_id = ?
        """;
        return jdbcTemplate.update(sql,
                entry.getAccountName(),
                entry.getUsername(),
                entry.getPasswordHash(),
                entry.getWebsite(),
                entry.getFolderId(),
                entry.getEntryId(),
                entry.getUserId()
        );
    }

    public PersonalPWEntry getPasswordById(Long entryId) {
        String sql = "SELECT * FROM password_entries WHERE entry_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new PersonalPWRowMapper(), entryId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<PersonalPWEntry> getPasswordsByUser(UUID userId) {
        String sql = """
        SELECT * FROM password_entries 
        WHERE user_id = ? 
        AND entry_id NOT IN (SELECT entry_id FROM team_passwords)
    """;
        return jdbcTemplate.query(sql, new PersonalPWRowMapper(), userId);
    }


    public List<PersonalPWEntry> getPasswordsByFolder(UUID folderId) {
        String sql = "SELECT * FROM password_entries WHERE folder_id = ?";
        return jdbcTemplate.query(sql, new PersonalPWRowMapper(), folderId);
    }

    public boolean userExists(UUID userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    public List<UUID> getUserFolderIds(UUID userId) {
        String sql = """
            SELECT DISTINCT folder_id 
            FROM password_entries 
            WHERE user_id = ? AND folder_id IS NOT NULL
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getObject("folder_id", UUID.class), userId);
    }
}
