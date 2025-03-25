package vaultmaster.com.vault.rowmapper;

import vaultmaster.com.vault.model.PersonalPWFolder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PersonalPWFolderRowMapper implements RowMapper<PersonalPWFolder> {
    @Override
    public PersonalPWFolder mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PersonalPWFolder(
                UUID.fromString(rs.getString("folder_id")),
                UUID.fromString(rs.getString("user_id")),
                rs.getString("folder_name")
        );
    }
}
