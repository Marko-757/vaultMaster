package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Permission;

import java.util.List;
import java.util.UUID;

@Repository
public class RolePermissionRepository {

    private final JdbcTemplate jdbc;

    public RolePermissionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 🔹 Assign permission to role (global)
    public void assignPermissionToRole(UUID roleId, UUID permissionId) {
        String sql = """
            INSERT INTO role_permissions (role_id, permission_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
        """;
        jdbc.update(sql, roleId, permissionId);
    }

    // 🔹 Get all permissions for a role
    public List<Permission> findPermissionsByRoleId(UUID roleId) {
        String sql = """
            SELECT p.permission_id, p.permission_name
            FROM role_permissions rp
            JOIN permissions p ON rp.permission_id = p.permission_id
            WHERE rp.role_id = ?
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            Permission p = new Permission();
            p.setId(UUID.fromString(rs.getString("permission_id")));
            p.setName(rs.getString("permission_name"));
            return p;
        }, roleId);
    }

    public void assignPermissionToItem(UUID roleId, UUID itemId, String itemType, String permissionName) {
        String sql = """
        INSERT INTO item_permissions (role_id, item_id, item_type, permission_name)
        VALUES (?, ?, ?, ?)
        ON CONFLICT DO NOTHING
    """;
        jdbc.update(sql, roleId, itemId, itemType, permissionName);
    }

    public List<Permission> findPermissionsByItem(UUID roleId, UUID itemId, String itemType) {
        String sql = """
        SELECT p.permission_id, p.permission_name
        FROM item_permissions ip
        JOIN permissions p ON ip.permission_name = p.permission_name
        WHERE ip.role_id = ? AND ip.item_id = ? AND ip.item_type = ?
    """;

        return jdbc.query(sql, (rs, rowNum) -> {
            Permission p = new Permission();
            p.setId(UUID.fromString(rs.getString("permission_id")));
            p.setName(rs.getString("permission_name"));
            return p;
        }, roleId, itemId, itemType);
    }


    public void removePermissionFromItem(UUID roleId, UUID itemId, String itemType, String permissionName) {
        String sql = """
        DELETE FROM item_permissions
        WHERE role_id = ? AND item_id = ? AND item_type = ? AND permission_name = ?
    """;
        jdbc.update(sql, roleId, itemId, itemType, permissionName);
    }


    public void assignPermissionToFolder(UUID roleId, UUID folderId, String folderType, String permissionName) {
        String table = folderType.equalsIgnoreCase("file") ? "file_folder_permissions" : "password_folder_permissions";

        String sql = String.format("""
        INSERT INTO %s (role_id, folder_id, permission_name)
        VALUES (?, ?, ?)
        ON CONFLICT DO NOTHING
    """, table);

        jdbc.update(sql, roleId, folderId, permissionName);
    }

    public void removePermissionFromFolder(UUID roleId, UUID folderId, String folderType, String permissionName) {
        String table = folderType.equalsIgnoreCase("file") ? "file_folder_permissions" : "password_folder_permissions";

        String deleteSql = String.format("""
        DELETE FROM %s
        WHERE role_id = ? AND folder_id = ? AND permission_name = ?
    """, table);

        jdbc.update(deleteSql, roleId, folderId, permissionName);
    }

    // Check if role has global permission
    public boolean roleHasPermission(UUID roleId, String permissionName) {
        String sql = """
            SELECT COUNT(*) FROM role_permissions rp
            JOIN permissions p ON rp.permission_id = p.permission_id
            WHERE rp.role_id = ? AND p.permission_name = ?
        """;

        Integer count = jdbc.queryForObject(sql, Integer.class, roleId, permissionName);
        return count != null && count > 0;
    }

    public void removePermissionFromRole(UUID roleId, String permissionName) {
        String sql = """
        DELETE FROM role_permissions
        WHERE role_id = ? AND permission_id = (
            SELECT permission_id FROM permissions WHERE permission_name = ?
        )
    """;
        jdbc.update(sql, roleId, permissionName);
    }

    public List<Permission> findPermissionsByFolder(UUID roleId, UUID folderId, String folderType) {
        String table = folderType.equalsIgnoreCase("file") ? "file_folder_permissions" : "password_folder_permissions";

        String sql = String.format("""
        SELECT p.permission_id, p.permission_name
        FROM %s fp
        JOIN permissions p ON p.permission_name = fp.permission_name
        WHERE fp.role_id = ? AND fp.folder_id = ?
    """, table);

        return jdbc.query(sql, (rs, rowNum) -> {
            Permission p = new Permission();
            p.setId(UUID.fromString(rs.getString("permission_id")));
            p.setName(rs.getString("permission_name"));
            return p;
        }, roleId, folderId);
    }

}
