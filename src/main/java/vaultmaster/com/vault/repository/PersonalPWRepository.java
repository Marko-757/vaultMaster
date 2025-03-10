package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.repository.PersonalPWRowMapper;
import vaultmaster.com.vault.util.AESUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class PersonalPWRepository {
    private final JdbcTemplate jdbcTemplate;

    public PersonalPWRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Save a new password
    public int savePassword(PersonalPWEntry entry) throws Exception {
        String sql = "INSERT INTO password_entries " +
                "(user_id, account_name, username, password_hash, url, folder_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                entry.getUserId(),
                entry.getAccountName(),
                entry.getUsername(),
                AESUtil.encrypt(entry.getPasswordHash()), // ✅ Encrypt password before storing
                entry.getWebsite(),
                entry.getFolderId(),
                Timestamp.valueOf(entry.getCreatedAt()),
                Timestamp.valueOf(entry.getUpdatedAt())
        );
    }

    // ✅ Delete a password entry
    public int deletePassword(Long entryId) {
        String sql = "DELETE FROM password_entries WHERE entry_id = ?";
        return jdbcTemplate.update(sql, entryId);
    }

    // ✅ Get a password entry by ID
    public PersonalPWEntry getPasswordById(Long entryId) {
        String sql = "SELECT * FROM password_entries WHERE entry_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new PersonalPWRowMapper(), entryId);
        } catch (Exception e) {
            return null; // Return null if no entry found
        }
    }

    // ✅ Check if a user exists in the database
    public boolean userExists(UUID userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    // ✅ Get all passwords for a user
    public List<PersonalPWEntry> getPasswordsByUser(UUID userId) {
        String sql = "SELECT * FROM password_entries WHERE user_id = ?";
        return jdbcTemplate.query(sql, new PersonalPWRowMapper(), userId);
    }

    // ✅ Get all passwords associated with a folder
    public List<PersonalPWEntry> getPasswordsByFolder(UUID folderId) {
        String sql = "SELECT * FROM password_entries WHERE folder_id = ?";
        return jdbcTemplate.query(sql, new PersonalPWRowMapper(), folderId);
    }
}
