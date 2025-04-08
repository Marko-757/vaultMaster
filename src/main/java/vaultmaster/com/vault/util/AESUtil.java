package vaultmaster.com.vault.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;


public class AESUtil {
    private static final String ALGORITHM = "AES";
    private static final String AES_SECRET_KEY = System.getenv("AES_SECRET_KEY");

    public static SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(AES_SECRET_KEY);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }

    public static boolean isValidEncryptedFormat(String encryptedData) {
        try {
            // Decode the base64 string to get the byte array
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);

            // Check if the byte array length is a multiple of AES block size (16 bytes)
            if (decodedData.length % 16 != 0) {
                return false;  // Invalid format, as AES requires data in block sizes of 16 bytes
            }

            return true;  // The data is in a valid format
        } catch (IllegalArgumentException e) {
            // If the base64 decoding fails, the format is invalid
            return false;
        }
    }
}


