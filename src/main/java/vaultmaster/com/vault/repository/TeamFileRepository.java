package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.TeamFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TeamFileRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamFileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TeamFile> rowMapper = (rs, rowNum) -> {
        TeamFile file = new TeamFile();
        file.setFileId(UUID.fromString(rs.getString("file_id")));
        file.setTeamId(UUID.fromString(rs.getString("team_id")));
        file.setFolderId(rs.getObject("folder_id", UUID.class));
        file.setFileKey(rs.getString("file_key"));
        file.setOriginalFilename(rs.getString("original_filename"));
        file.setFileSize(rs.getLong("file_size"));
        file.setMimeType(rs.getString("mime_type"));
        file.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        file.setCreatedBy(UUID.fromString(rs.getString("created_by")));
        return file;
    };

    public void save(TeamFile file) {
        String sql = """
            INSERT INTO team_files (
                file_id, team_id, folder_id, file_key, original_filename,
                file_size, mime_type, created_at, created_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                file.getFileId(),
                file.getTeamId(),
                file.getFolderId(),
                file.getFileKey(),
                file.getOriginalFilename(),
                file.getFileSize(),
                file.getMimeType(),
                Timestamp.valueOf(file.getCreatedAt()),
                file.getCreatedBy()
        );
    }

    public Optional<TeamFile> findById(UUID fileId) {
        String sql = "SELECT * FROM team_files WHERE file_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, fileId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<TeamFile> findByTeamId(UUID teamId) {
        String sql = "SELECT * FROM team_files WHERE team_id = ?";
        return jdbcTemplate.query(sql, rowMapper, teamId);
    }

    public List<TeamFile> findByFolderId(UUID folderId) {
        String sql = "SELECT * FROM team_files WHERE folder_id = ?";
        return jdbcTemplate.query(sql, rowMapper, folderId);
    }

    public void deleteById(UUID fileId) {
        String sql = "DELETE FROM team_files WHERE file_id = ?";
        jdbcTemplate.update(sql, fileId);
    }
}
