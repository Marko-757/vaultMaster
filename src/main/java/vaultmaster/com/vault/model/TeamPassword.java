package vaultmaster.com.vault.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPassword {
    private int teamPasswordId;
    private UUID teamId;
    private int entryId;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private UUID modifiedBy;
    private LocalDateTime modifiedAt;
    private UUID folderId;

    private String plaintextPassword;

    @JsonIgnore
    private String encryptedPassword;
}
