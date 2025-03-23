package vaultmaster.com.vault.repository;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;
import vaultmaster.com.vault.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUserId(UUID.fromString(rs.getString("user_id")));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEmail(rs.getString("email"));

        Timestamp created = rs.getTimestamp("created_date");
        Timestamp modified = rs.getTimestamp("modified_date");
        user.setCreatedDate(created != null ? new Date(created.getTime()) : null);
        user.setModifiedDate(modified != null ? new Date(modified.getTime()) : null);

        String createdByStr = rs.getString("created_by");
        String modifiedByStr = rs.getString("modified_by");
        user.setCreatedBy(createdByStr != null ? safeParseUUID(createdByStr) : null);
        user.setModifiedBy(modifiedByStr != null ? safeParseUUID(modifiedByStr) : null);

        user.setVerified(rs.getBoolean("verified"));
        return user;
    }

    /**
     * Safely parses a UUID from a string, returning null if the string is not a valid UUID.
     */
    private UUID safeParseUUID(String uuidStr) {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
