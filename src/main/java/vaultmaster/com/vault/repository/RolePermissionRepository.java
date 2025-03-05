package vaultmaster.com.vault.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.RolePermission;
import vaultmaster.com.vault.model.RolePermissionId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RolePermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public RolePermissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to map a row from the result set to a RolePermission object.
    private final RowMapper<RolePermission> rolePermissionRowMapper = new RowMapper<RolePermission>() {
        @Override
        public RolePermission mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Assuming the role_permissions table has columns "role_id" and "permission_id".
            UUID roleId = UUID.fromString(rs.getString("role_id"));
            UUID permissionId = UUID.fromString(rs.getString("permission_id"));
            RolePermissionId id = new RolePermissionId(roleId, permissionId);

            // Create a RolePermission instance and set the composite key.
            RolePermission rolePermission = new RolePermission();
            rolePermission.setId(id);
            // If RolePermission has additional fields, map them here.
            return rolePermission;
        }
    };

    /**
     * Inserts a new role-permission relationship into the database.
     * @param rolePermission The RolePermission object to save.
     */
    public void save(RolePermission rolePermission) {
        String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                rolePermission.getRole().getRoleId().toString(),
                rolePermission.getPermission().getPermissionId().toString());
    }

    /**
     * Finds a RolePermission by roleId and permissionId.
     * @param roleId The role ID.
     * @param permissionId The permission ID.
     * @return An Optional containing the RolePermission if found.
     */
    public Optional<RolePermission> findByRoleIdAndPermissionId(UUID roleId, UUID permissionId) {
        String sql = "SELECT * FROM role_permissions WHERE role_id = ? AND permission_id = ?";
        try {
            RolePermission rolePermission = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{roleId.toString(), permissionId.toString()},
                    rolePermissionRowMapper
            );
            return Optional.ofNullable(rolePermission);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    /**
     * Deletes a role-permission relationship based on roleId and permissionId.
     * @param roleId The role ID.
     * @param permissionId The permission ID.
     * @return The number of rows affected.
     */
    public int deleteByRoleIdAndPermissionId(UUID roleId, UUID permissionId) {
        String sql = "DELETE FROM role_permissions WHERE role_id = ? AND permission_id = ?";
        return jdbcTemplate.update(sql, roleId.toString(), permissionId.toString());
    }
}
