package vaultmaster.com.vault.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Permission {
    private UUID id;
    private String name;
    private String displayName;
    private String description;
}
