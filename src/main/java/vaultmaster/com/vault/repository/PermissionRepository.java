package vaultmaster.com.vault.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Permission;
import java.util.*;
import java.sql.*;


@Repository
public class PermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PermissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to convert rows into Permission objects
    private final RowMapper<Permission> permissionRowMapper = new RowMapper<>() {
        @Override
        public Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
            Permission permission = new Permission();
            permission.setPermissionId(UUID.fromString(rs.getString("permission_id")));
            permission.setPermissionName(rs.getString("permission_name"));
            return permission;
        }
    };

    /**
     * Find a permission by its ID.
     *
     * @param permissionId The permission's UUID.
     * @return An Optional containing the Permission if found, or empty if not.
     */
    public Optional<Permission> findById(UUID permissionId) {
        String sql = "SELECT * FROM permissions WHERE permission_id = ?";
        try {
            Permission permission = jdbcTemplate.queryForObject(sql, new Object[]{permissionId.toString()}, permissionRowMapper);
            return Optional.ofNullable(permission);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieve all permissions.
     *
     * @return A list of Permission objects.
     */
    public List<Permission> findAll() {
        String sql = "SELECT * FROM permissions";
        return jdbcTemplate.query(sql, permissionRowMapper);
    }

    /**
     * Save a new permission to the database.
     *
     * @param permission The Permission object to be saved.
     */
    public void save(Permission permission) {
        String sql = "INSERT INTO permissions (permission_id, permission_name) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                permission.getPermissionId().toString(),
                permission.getPermissionName());
    }

    /**
     * Update an existing permission.
     *
     * @param permission The Permission object with updated values.
     */
    public void update(Permission permission) {
        String sql = "UPDATE permissions SET permission_name = ? WHERE permission_id = ?";
        jdbcTemplate.update(sql,
                permission.getPermissionName(),
                permission.getPermissionId().toString());
    }

    /**
     * Delete a permission by its ID.
     *
     * @param permissionId The UUID of the permission to delete.
     * @return The number of rows affected.
     */
    public int deleteById(UUID permissionId) {
        String sql = "DELETE FROM permissions WHERE permission_id = ?";
        return jdbcTemplate.update(sql, permissionId.toString());
    }
}