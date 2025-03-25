package vaultmaster.com.vault.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.PersonalFileFolder;
import vaultmaster.com.vault.rowmapper.PersonalFileFolderRowMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PersonalFileFolderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PersonalFileFolderRowMapper rowMapper;

    public void save(PersonalFileFolder folder) {
        String sql = """
            INSERT INTO personal_file_folders (folder_id, user_id, folder_name, created_at)
            VALUES (?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                folder.getFolderId(),
                folder.getUserId(),
                folder.getFolderName(),
                Timestamp.valueOf(folder.getCreatedAt())
        );
    }

    public List<PersonalFileFolder> findByUserId(UUID userId) {
        String sql = "SELECT * FROM personal_file_folders WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public Optional<PersonalFileFolder> findById(UUID folderId) {
        String sql = "SELECT * FROM personal_file_folders WHERE folder_id = ?";
        return jdbcTemplate.query(sql, rowMapper, folderId)
                .stream()
                .findFirst();
    }

    public void updateFolderName(UUID folderId, String newName) {
        String sql = "UPDATE personal_file_folders SET folder_name = ? WHERE folder_id = ?";
        jdbcTemplate.update(sql, newName, folderId);
    }

    public void deleteById(UUID folderId) {
        String sql = "DELETE FROM personal_file_folders WHERE folder_id = ?";
        jdbcTemplate.update(sql, folderId);
    }
}
