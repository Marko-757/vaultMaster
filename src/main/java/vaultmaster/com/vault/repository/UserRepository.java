package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.User;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Existing method to find by email
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{email}, (rs, rowNum) -> {
                User u = new User();
                u.setUserId(UUID.fromString(rs.getString("user_id")));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setEmail(rs.getString("email"));
                u.setCreatedDate(new java.util.Date(rs.getTimestamp("created_date").getTime()));
                u.setModifiedDate(new java.util.Date(rs.getTimestamp("modified_date").getTime()));
                u.setCreatedBy(rs.getString("created_by") != null ? UUID.fromString(rs.getString("created_by")) : null);
                u.setModifiedBy(rs.getString("modified_by") != null ? UUID.fromString(rs.getString("modified_by")) : null);
                u.setVerified(rs.getBoolean("verified"));
                return u;
            });
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // New method to save a user
    public void save(User user) {
        String sql = "INSERT INTO users (user_id, password_hash, full_name, phone_number, email, created_date, modified_date, created_by, modified_by, verified) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getUserId().toString(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                new Timestamp(user.getCreatedDate().getTime()),
                new Timestamp(user.getModifiedDate().getTime()),
                user.getCreatedBy() != null ? user.getCreatedBy().toString() : null,
                user.getModifiedBy() != null ? user.getModifiedBy().toString() : null,
                user.isVerified());
    }
}
