package vaultmaster.com.vault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberProfile {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String roleName;
}
