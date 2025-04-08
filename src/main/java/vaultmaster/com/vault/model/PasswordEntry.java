package vaultmaster.com.vault.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntry {
    private int entryId;
    private UUID userId;
    private String accountName;
    private String username;
    private String passwordHash;
    private String website;
    private UUID folderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID createdBy;
    private UUID modifiedBy;
}
