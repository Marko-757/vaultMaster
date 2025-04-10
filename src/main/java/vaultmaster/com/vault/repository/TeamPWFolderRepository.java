package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.TeamPWFolder;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public class TeamPWFolderRepository {

    private final JdbcTemplate jdbc;

    public TeamPWFolderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void createFolder(TeamPWFolder folder) {
        String sql = """
            INSERT INTO team_pw_folders (folder_id, team_id, folder_name, created_by, created_at, modified_by, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
                folder.getFolderId(),
                folder.getTeamId(),
                folder.getFolderName(),
                folder.getCreatedBy(),
                Timestamp.valueOf(folder.getCreatedAt()),
                folder.getModifiedBy(),
                folder.getModifiedAt() != null ? Timestamp.valueOf(folder.getModifiedAt()) : null
        );
    }

    public List<TeamPWFolder> getFoldersByTeamId(UUID teamId) {
        String sql = """
        SELECT folder_id, team_id, folder_name, created_by, created_at, modified_by, modified_at
        FROM team_pw_folders
        WHERE team_id = ?
        ORDER BY folder_name
    """;

        return jdbc.query(sql, (rs, rowNum) -> TeamPWFolder.builder()
                .folderId(UUID.fromString(rs.getString("folder_id")))
                .teamId(UUID.fromString(rs.getString("team_id")))
                .folderName(rs.getString("folder_name"))
                .createdBy(UUID.fromString(rs.getString("created_by")))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .modifiedBy(rs.getObject("modified_by", UUID.class))
                .modifiedAt(rs.getTimestamp("modified_at") != null ? rs.getTimestamp("modified_at").toLocalDateTime() : null)
                .build(), teamId);
    }

    public int renameFolder(UUID folderId, String newName, UUID modifiedBy) {
        String sql = """
        UPDATE team_pw_folders
        SET folder_name = ?, modified_by = ?, modified_at = CURRENT_TIMESTAMP
        WHERE folder_id = ?
    """;
        return jdbc.update(sql, newName, modifiedBy, folderId);
    }

    public int deleteFolder(UUID folderId) {
        String sql = "DELETE FROM team_pw_folders WHERE folder_id = ?";
        return jdbc.update(sql, folderId);
    }

    public UUID getTeamIdByFolderId(UUID folderId) {
        String sql = "SELECT team_id FROM team_pw_folders WHERE folder_id = ?";
        return jdbc.queryForObject(sql, UUID.class, folderId);
    }


}

