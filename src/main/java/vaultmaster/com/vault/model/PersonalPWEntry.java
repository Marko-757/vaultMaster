package vaultmaster.com.vault.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalPWEntry {
    private Long entryId;
    private UUID userId;
    private String accountName;
    private String username;
    private String passwordHash;
    private String website;
    private UUID folderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setTimestamps() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}



