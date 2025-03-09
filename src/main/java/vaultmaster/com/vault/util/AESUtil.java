package vaultmaster.com.vault.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {
    private static final String SECRET_KEY = "12345678901234567890123456789012"; // 32 bytes for AES-256
    private static final byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Ensure the key is exactly 32 bytes (AES-256)
        SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(keyBytes, 32), "AES");

        byte[] iv = new byte[16]; // AES IV must be 16 bytes
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        // Store IV with encrypted data, separated by ":"
        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedBytes);
    }


    public static String decrypt(String encryptedData) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted data format");
        }

        byte[] iv = Base64.getDecoder().decode(parts[0]); // Extract IV
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]); // Extract encrypted data

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Ensure the key is exactly 32 bytes (AES-256)
        SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(keyBytes, 32), "AES");

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        return new String(cipher.doFinal(encryptedBytes));
    }



    public static String generateIV() {
        byte[] iv = new byte[16]; // AES requires exactly 16 bytes
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv); // Encode for storage
    }

}

