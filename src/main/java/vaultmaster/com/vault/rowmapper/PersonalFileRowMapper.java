package vaultmaster.com.vault.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import vaultmaster.com.vault.model.PersonalFile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class PersonalFileRowMapper implements RowMapper<PersonalFile> {

    @Override
    public PersonalFile mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PersonalFile.builder()
                .fileId(UUID.fromString(rs.getString("file_id")))
                .userId(UUID.fromString(rs.getString("user_id")))
                .folderId(rs.getString("folder_id") != null ? UUID.fromString(rs.getString("folder_id")) : null)
                .fileKey(rs.getString("file_key"))
                .originalFilename(rs.getString("original_filename"))
                .fileSize(rs.getLong("file_size"))
                .uploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime())
                .build();
    }
}
