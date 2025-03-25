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
public class PersonalFileFolder {
    private UUID folderId;
    private UUID userId;
    private String folderName;
    private LocalDateTime createdAt;
}
