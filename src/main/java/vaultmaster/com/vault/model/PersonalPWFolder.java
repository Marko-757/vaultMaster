package vaultmaster.com.vault.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor // ✅ Generates a no-args constructor
@AllArgsConstructor // ✅ Generates a constructor with all fields
public class PersonalPWFolder {
    private UUID folderId;
    private UUID userId;
    private String folderName;

    // ✅ Ensure folderId is always assigned
    public PersonalPWFolder(UUID userId, String folderName) {
        this.folderId = UUID.randomUUID(); // Generate a UUID for new folders
        this.userId = userId;
        this.folderName = folderName;
    }
}
