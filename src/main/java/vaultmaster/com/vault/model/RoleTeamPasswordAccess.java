package vaultmaster.com.vault.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RoleTeamPasswordAccess {
    private UUID roleId;
    private int teamPasswordId;
}
