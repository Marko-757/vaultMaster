package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ItemPermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemPermissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Grant permission for a specific item and role
    public void grantPermission(UUID itemId, String itemType, UUID roleId, String permissionName) {
        String sql = """
            INSERT INTO item_permissions (item_id, item_type, role_id, permission_name)
            VALUES (?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql, itemId, itemType, roleId, permissionName);
    }

    // Get all permissions for an item and role
    public List<String> getPermissions(UUID itemId, UUID roleId) {
        String sql = """
            SELECT permission_name FROM item_permissions
            WHERE item_id = ? AND role_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("permission_name"), itemId, roleId);
    }

    // Revoke a permission
    public void revokePermission(UUID itemId, String itemType, UUID roleId, String permissionName) {
        String sql = """
            DELETE FROM item_permissions
            WHERE item_id = ? AND item_type = ? AND role_id = ? AND permission_name = ?
        """;
        jdbcTemplate.update(sql, itemId, itemType, roleId, permissionName);
    }

    // Check if a role has a specific permission for an item
    public boolean hasPermission(UUID itemId, UUID roleId, String permissionName) {
        String sql = """
            SELECT COUNT(*) FROM item_permissions
            WHERE item_id = ? AND role_id = ? AND permission_name = ?
        """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, itemId, roleId, permissionName);
        return count != null && count > 0;
    }
}

