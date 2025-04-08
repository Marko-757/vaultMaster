package vaultmaster.com.vault.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamFile {
    private UUID fileId;
    private UUID teamId;
    private UUID folderId;
    private String fileKey;
    private String originalFilename;
    private long fileSize;
    private String mimeType;
    private LocalDateTime createdAt;
    private UUID createdBy;
}
