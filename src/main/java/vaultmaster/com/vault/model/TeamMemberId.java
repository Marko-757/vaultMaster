package vaultmaster.com.vault.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TeamMemberId implements Serializable {
    private UUID teamId;
    private UUID userId;

    // Default constructor
    public TeamMemberId() {}

    // Constructor
    public TeamMemberId(UUID teamId, UUID userId) {
        this.teamId = teamId;
        this.userId = userId;
    }

    // Getters and Setters
    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMemberId that = (TeamMemberId) o;
        return Objects.equals(teamId, that.teamId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, userId);
    }
}
