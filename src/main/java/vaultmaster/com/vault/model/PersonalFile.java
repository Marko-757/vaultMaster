package vaultmaster.com.vault.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalFile {
    private UUID fileId;
    private UUID userId;
    private UUID folderId; // can be null
    private String fileKey;
    private String originalFilename;
    private String mimeType;
    private long fileSize;
    private LocalDateTime uploadedAt;
}
