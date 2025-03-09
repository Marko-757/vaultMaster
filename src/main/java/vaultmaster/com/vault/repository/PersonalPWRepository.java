package vaultmaster.com.vault.repository;

import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.util.AESUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.repository.PersonalPWRowMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


@Repository
public class PersonalPWRepository {
    private final JdbcTemplate jdbcTemplate;

    public PersonalPWRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Save new personal password (with encryption)
    public int savePassword(PersonalPWEntry entry) throws Exception {
        String sql = "INSERT INTO password_entries " +
                "(user_id, account_name, username, password_hash, url, folder_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                UUID.fromString(entry.getUserId().toString()),
                entry.getAccountName(),
                entry.getUsername(),
                AESUtil.encrypt(entry.getPasswordHash()), // Encrypt password
                (entry.getWebsite() != null ? entry.getWebsite() : null),
                (entry.getFolderId() != null ? UUID.fromString(entry.getFolderId().toString()) : null),
                Timestamp.valueOf(entry.getCreatedAt()),
                Timestamp.valueOf(entry.getUpdatedAt())
        );
    }

    // ✅ Delete password entry
    public int deletePassword(Long entryId) {
        String sql = "DELETE FROM password_entries WHERE entry_id = ?";
        return jdbcTemplate.update(sql, entryId);
    }

    // ✅ Get all passwords for a user
    public List<PersonalPWEntry> getPasswordsByUser(UUID userId) {
        String sql = "SELECT * FROM password_entries WHERE user_id = ?";
        return jdbcTemplate.query(sql, new PersonalPWRowMapper(), userId);
    }

    public PersonalPWEntry getPasswordById(Long entryId) {
        String sql = "SELECT * FROM password_entries WHERE entry_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new PersonalPWRowMapper(), entryId);
        } catch (Exception e) {
            return null; // Return null if no entry found
        }
    }


}



