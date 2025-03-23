package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.repository.UserRowMapper;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserRepository(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findById(UUID userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, userId); // ✅ UUID directly
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByVerificationToken(String token) {
        String sql = "SELECT * FROM users WHERE verification_token = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, token);
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
                user.getCreatedDate() != null ? new Timestamp(user.getCreatedDate().getTime()) : null,
                user.getModifiedDate() != null ? new Timestamp(user.getModifiedDate().getTime()) : null,
                user.getCreatedBy(),
                user.getModifiedBy(),
                user.isVerified());
    }

    public void updateUserVerificationStatus(UUID userId, boolean verified) {
        String sql = "UPDATE users SET verified = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, verified, userId);
    }

    public void delete(UUID userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }
}
