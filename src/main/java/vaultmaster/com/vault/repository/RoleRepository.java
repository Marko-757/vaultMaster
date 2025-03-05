package vaultmaster.com.vault.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to convert rows into Role objects
    private final RowMapper<Role> roleRowMapper = new RowMapper<Role>() {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setRoleId(UUID.fromString(rs.getString("role_id")));
            role.setRoleName(rs.getString("role_name"));
            // Assuming 'created_by' is stored as text (UUID string) and 'created_at' as a TIMESTAMP
            String createdByStr = rs.getString("created_by");
            role.setCreatedBy(createdByStr != null ? UUID.fromString(createdByStr) : null);
            Timestamp createdAtTs = rs.getTimestamp("created_at");
            role.setCreatedAt(createdAtTs != null ? new Date(createdAtTs.getTime()) : null);
            return role;
        }
    };

    /**
     * Find a role by its ID.
     *
     * @param roleId The role's UUID.
     * @return An Optional containing the Role if found, or empty if not.
     */
    public Optional<Role> findById(UUID roleId) {
        String sql = "SELECT * FROM roles WHERE role_id = ?";
        try {
            Role role = jdbcTemplate.queryForObject(sql, new Object[]{roleId.toString()}, roleRowMapper);
            return Optional.ofNullable(role);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieve all roles.
     *
     * @return A list of Role objects.
     */
    public List<Role> findAll() {
        String sql = "SELECT * FROM roles";
        return jdbcTemplate.query(sql, roleRowMapper);
    }

    /**
     * Save a new role to the database.
     *
     * @param role The Role object to be saved.
     */
    public void save(Role role) {
        String sql = "INSERT INTO roles (role_id, role_name, created_by, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                role.getRoleId().toString(),
                role.getRoleName(),
                role.getCreatedBy() != null ? role.getCreatedBy().toString() : null,
                role.getCreatedAt() != null ? new Timestamp(role.getCreatedAt().getTime()) : null);
    }

    /**
     * Update an existing role.
     *
     * @param role The Role object with updated values.
     */
    public void update(Role role) {
        String sql = "UPDATE roles SET role_name = ?, created_by = ?, created_at = ? WHERE role_id = ?";
        jdbcTemplate.update(sql,
                role.getRoleName(),
                role.getCreatedBy() != null ? role.getCreatedBy().toString() : null,
                role.getCreatedAt() != null ? new Timestamp(role.getCreatedAt().getTime()) : null,
                role.getRoleId().toString());
    }

    /**
     * Delete a role by its ID.
     *
     * @param roleId The UUID of the role to delete.
     * @return The number of rows affected.
     */
    public int deleteById(UUID roleId) {
        String sql = "DELETE FROM roles WHERE role_id = ?";
        return jdbcTemplate.update(sql, roleId.toString());
    }
}
