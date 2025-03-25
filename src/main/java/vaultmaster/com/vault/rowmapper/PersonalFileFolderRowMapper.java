package vaultmaster.com.vault.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import vaultmaster.com.vault.model.PersonalFileFolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class PersonalFileFolderRowMapper implements RowMapper<PersonalFileFolder> {

    @Override
    public PersonalFileFolder mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PersonalFileFolder.builder()
                .folderId(UUID.fromString(rs.getString("folder_id")))
                .userId(UUID.fromString(rs.getString("user_id")))
                .folderName(rs.getString("folder_name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
