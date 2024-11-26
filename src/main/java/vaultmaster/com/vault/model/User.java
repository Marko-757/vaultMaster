package vaultmaster.com.vault.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")  // Specify the name of the table in the database
public class User {

    public boolean isPresent() {
        return this.email != null && !this.email.isEmpty();
    }

    @Id
    private Long userId;

    private String fullName;
    private String email;
    private String passwordHash;
    private String salt;
    private String phoneNumber;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime modifiedDate;
    private String modifiedBy;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getter for userID
    public Long getUserId() {
        return userId;
    }

    // Setter for userID
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter for passwordHash
    public String getPasswordHash() {
        return passwordHash;
    }

    // Setter for passwordHash
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Getter for salt
    public String getSalt() {
        return salt;
    }

    // Setter for salt
    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
