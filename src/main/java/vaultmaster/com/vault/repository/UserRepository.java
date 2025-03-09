package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public Optional<User> findByVerificationToken(String token) {
        String sql = "SELECT * FROM users WHERE verification_token = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{token}, (rs, rowNum) -> {
                User u = new User();
                u.setUserId(UUID.fromString(rs.getString("user_id")));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setEmail(rs.getString("email"));
                u.setCreatedDate(rs.getTimestamp("created_date"));
                u.setModifiedDate(rs.getTimestamp("modified_date"));
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

    /**
     * Updates the user's verification status.
     */
    public void updateUserVerificationStatus(UUID userId, boolean verified) {
        String sql = "UPDATE users SET verified = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, verified, userId);
    }


    public Optional<User> findById(UUID userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{userId}, (rs, rowNum) -> {
                User u = new User();
                u.setUserId(UUID.fromString(rs.getString("user_id")));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setEmail(rs.getString("email"));
                u.setCreatedDate(rs.getTimestamp("created_date"));
                u.setModifiedDate(rs.getTimestamp("modified_date"));
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

    public void save(User user) {
        String sql = "INSERT INTO users (user_id, password_hash, full_name, phone_number, email, created_date, modified_date, created_by, modified_by, verified) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                user.getUserId(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                new Timestamp(user.getCreatedDate().getTime()),
                new Timestamp(user.getModifiedDate().getTime()),
                user.getCreatedBy(),
                user.getModifiedBy(),
                user.isVerified());
    }

    public void delete(UUID userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUserId(UUID.fromString(rs.getString("user_id")));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phone_number"));
            return user;
        });
    }
}
