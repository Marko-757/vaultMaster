// DuplicateResourceException.java
package vaultmaster.com.vault.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}