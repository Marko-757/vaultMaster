package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    /**
     * Fetches a user by email.
     * @param email The user's email.
     * @return Optional<User> if found.
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{email}, userRowMapper()).stream().findFirst();
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updatePassword(UUID userId, String newHashedPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, newHashedPassword, userId);
    }


    /**
     * Fetches a user by ID.
     * @param userId The user's UUID.
     * @return Optional<User> if found.
     */
    public Optional<User> findById(UUID userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{userId}, userRowMapper()).stream().findFirst();
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Saves a new user to the database.
     * @param user The user object.
     */
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
                user.getCreatedBy() != null ? user.getCreatedBy() : null,
                user.getModifiedBy() != null ? user.getModifiedBy() : null,
                user.isVerified());
    }

    /**
     * Deletes a user from the database by userId.
     * @param userId The user's UUID.
     */
    public void delete(UUID userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    /**
     * Fetches all users from the database.
     * @return List<User>
     */
    public List<User> findAll() {
        String sql = "SELECT user_id, full_name, email, phone_number FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUserId(UUID.fromString(rs.getString("user_id")));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phone_number"));
            return user;
        });
    }

    /**
     * Maps database columns to a User object.
     * @return RowMapper<User>
     */
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserId(UUID.fromString(rs.getString("user_id")));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setFullName(rs.getString("full_name"));
            user.setPhoneNumber(rs.getString("phone_number"));
            user.setEmail(rs.getString("email"));
            user.setCreatedDate(rs.getTimestamp("created_date"));
            user.setModifiedDate(rs.getTimestamp("modified_date"));
            user.setCreatedBy(rs.getString("created_by") != null ? UUID.fromString(rs.getString("created_by")) : null);
            user.setModifiedBy(rs.getString("modified_by") != null ? UUID.fromString(rs.getString("modified_by")) : null);
            user.setVerified(rs.getBoolean("verified"));
            return user;
        };
    }
}
