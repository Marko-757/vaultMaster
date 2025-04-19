package vaultmaster.com.vault.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamFileFolder {
    private UUID folderId;
    private UUID teamId;
    private String folderName;
    private LocalDateTime createdAt;
}
