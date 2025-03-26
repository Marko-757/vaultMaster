package vaultmaster.com.vault.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.PersonalFile;
import vaultmaster.com.vault.rowmapper.PersonalFileRowMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PersonalFileRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PersonalFileRowMapper rowMapper;

    public void save(PersonalFile file) {
        String sql = """
        INSERT INTO personal_file_uploads
        (file_id, user_id, folder_id, file_key, original_filename, file_size, mime_type, uploaded_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        jdbcTemplate.update(sql,
                file.getFileId(),
                file.getUserId(),
                file.getFolderId(),
                file.getFileKey(),
                file.getOriginalFilename(),
                file.getFileSize(),
                file.getMimeType(),
                Timestamp.valueOf(file.getUploadedAt())
        );
    }


    public Optional<PersonalFile> findById(UUID fileId) {
        String sql = "SELECT * FROM personal_file_uploads WHERE file_id = ?";
        return jdbcTemplate.query(sql, rowMapper, fileId)
                .stream()
                .findFirst();
    }

    public List<PersonalFile> findByUserId(UUID userId) {
        String sql = "SELECT * FROM personal_file_uploads WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<PersonalFile> findByFolderId(UUID folderId) {
        String sql = "SELECT * FROM personal_file_uploads WHERE folder_id = ?";
        return jdbcTemplate.query(sql, rowMapper, folderId);
    }

    public void deleteById(UUID fileId) {
        String sql = "DELETE FROM personal_file_uploads WHERE file_id = ?";
        jdbcTemplate.update(sql, fileId);
    }
}
