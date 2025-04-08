package vaultmaster.com.vault.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RoleTeamFileAccess {
    private UUID roleId;
    private UUID teamFileId;
}
