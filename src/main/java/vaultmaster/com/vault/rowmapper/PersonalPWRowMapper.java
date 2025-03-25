package vaultmaster.com.vault.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import vaultmaster.com.vault.model.PersonalPWEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PersonalPWRowMapper implements RowMapper<PersonalPWEntry> {
    @Override
    public PersonalPWEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            return PersonalPWEntry.builder()
                    .entryId(rs.getLong("entry_id"))
                    .userId(UUID.fromString(rs.getString("user_id")))
                    .accountName(rs.getString("account_name"))
                    .username(rs.getString("username"))
                    .passwordHash(rs.getString("password_hash"))
                    .website(rs.getString("url"))
                    .folderId(rs.getString("folder_id") != null ? UUID.fromString(rs.getString("folder_id")) : null)
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                    .build();
        }
    }


