package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PermissionCheckerRepository {

    private final JdbcTemplate jdbcTemplate;

    public PermissionCheckerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean roleHasFolderPermission(UUID roleId, UUID folderId, String permissionName, String folderType) {
        String tableName = folderType.equalsIgnoreCase("file")
                ? "file_folder_permissions"
                : "password_folder_permissions";

        String sql = String.format("""
            SELECT COUNT(*) FROM %s
            WHERE role_id = ? AND folder_id = ? AND permission_name = ?
        """, tableName);

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId, folderId, permissionName);
        return count != null && count > 0;
    }

    public boolean roleHasItemPermission(UUID roleId, int itemId, String itemType, String permissionName) {
        String sql = """
        SELECT COUNT(*) FROM item_permissions
        WHERE role_id = ? AND item_id = ? AND item_type = ? AND permission_name = ?
    """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId, itemId, itemType, permissionName);
        return count != null && count > 0;
    }

    public boolean roleHasItemPermission(UUID roleId, UUID itemId, String itemType, String permissionName) {
        String sql = """
        SELECT COUNT(*) FROM item_permissions
        WHERE role_id = ? AND item_id = ? AND item_type = ? AND permission_name = ?
    """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId, itemId.toString(), itemType, permissionName);
        return count != null && count > 0;
    }


    public List<String> getFolderPermissions(UUID roleId, UUID folderId, String folderType) {
        String tableName = folderType.equalsIgnoreCase("file")
                ? "file_folder_permissions"
                : "password_folder_permissions";

        String sql = String.format("""
            SELECT permission_name FROM %s
            WHERE role_id = ? AND folder_id = ?
        """, tableName);

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("permission_name"), roleId, folderId);
    }

    public List<String> getItemPermissions(UUID roleId, UUID itemId, String itemType) {
        String sql = """
            SELECT permission_name FROM item_permissions
            WHERE role_id = ? AND item_id = ? AND item_type = ?
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("permission_name"), roleId, itemId, itemType);
    }

    public List<UUID> findRolesWithFolderPermission(UUID folderId, String permissionName, String folderType) {
        String table = folderType.equalsIgnoreCase("file") ? "file_folder_permissions" : "password_folder_permissions";

        String sql = String.format("""
        SELECT role_id
        FROM %s ffp
        JOIN permissions p ON ffp.permission_id = p.permission_id
        WHERE ffp.folder_id = ? AND p.permission_name = ?
    """, table);

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                UUID.fromString(rs.getString("role_id")), folderId, permissionName);
    }

    public List<String> findAllPermissionsForRoleOnFolder(UUID roleId, UUID folderId, String folderType) {
        String table = folderType.equalsIgnoreCase("file") ? "file_folder_permissions" : "password_folder_permissions";

        String sql = String.format("""
        SELECT p.permission_name
        FROM %s ffp
        JOIN permissions p ON ffp.permission_id = p.permission_id
        WHERE ffp.folder_id = ? AND ffp.role_id = ?
    """, table);

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("permission_name"), folderId, roleId);
    }


}
