package vaultmaster.com.vault.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWFolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class PersonalPWFolderRepository {
    private final JdbcTemplate jdbcTemplate;

    public PersonalPWFolderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Create a new folder
    public int createFolder(PersonalPWFolder folder) {
        String sql = "INSERT INTO personal_pw_folders (folder_id, user_id, folder_name) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
                folder.getFolderId(),
                folder.getUserId(),
                folder.getFolderName());
    }

    public boolean doesUserExist(UUID userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }


    public List<PersonalPWFolder> getUserFolders(UUID userId) {
        String sql = "SELECT * FROM personal_pw_folders WHERE user_id = ?";
        return jdbcTemplate.query(sql, folderRowMapper, userId);
    }


    public int updateFolder(UUID folderId, String newName) {
        String sql = "UPDATE personal_pw_folders SET folder_name = ? WHERE folder_id = ?";
        return jdbcTemplate.update(sql, newName, folderId);
    }

    public int deleteFolder(UUID folderId) {
        String sql = "DELETE FROM personal_pw_folders WHERE folder_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, folderId);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found.");
        }
        return rowsAffected;
    }

    private static final RowMapper<PersonalPWFolder> folderRowMapper = (rs, rowNum) ->
            new PersonalPWFolder(
                    UUID.fromString(rs.getString("folder_id")),
                    UUID.fromString(rs.getString("user_id")),
                    rs.getString("folder_name")
            );
}
