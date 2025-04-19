package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.TeamFileFolder;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

@Repository
public class TeamFileFolderRepository {

    private final JdbcTemplate jdbc;

    public TeamFileFolderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public TeamFileFolder createFolder(TeamFileFolder folder) {
        String sql = """
            INSERT INTO team_file_folders (folder_id, team_id, folder_name, created_at)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
        """;

        UUID folderId = UUID.randomUUID();
        folder.setFolderId(folderId);

        jdbc.update(sql,
                folder.getFolderId(),
                folder.getTeamId(),
                folder.getFolderName()
        );

        return folder;
    }

    public List<TeamFileFolder> getFoldersByTeamId(UUID teamId) {
        String sql = "SELECT * FROM team_file_folders WHERE team_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> mapRow(rs), teamId);
    }

    public int renameFolder(UUID folderId, String newName) {
        String sql = "UPDATE team_file_folders SET folder_name = ? WHERE folder_id = ?";
        return jdbc.update(sql, newName, folderId);
    }

    public int deleteFolder(UUID folderId) {
        String sql = "DELETE FROM team_file_folders WHERE folder_id = ?";
        return jdbc.update(sql, folderId);
    }

    public UUID getTeamIdByFolderId(UUID folderId) {
        String sql = "SELECT team_id FROM team_file_folders WHERE folder_id = ?";
        return jdbc.queryForObject(sql, UUID.class, folderId);
    }

    private TeamFileFolder mapRow(ResultSet rs) throws java.sql.SQLException {
        TeamFileFolder folder = new TeamFileFolder();
        folder.setFolderId(UUID.fromString(rs.getString("folder_id")));
        folder.setTeamId(UUID.fromString(rs.getString("team_id")));
        folder.setFolderName(rs.getString("folder_name"));
        folder.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return folder;
    }
}
