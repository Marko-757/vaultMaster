package vaultmaster.com.vault.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecretManagerUtil {

    private static final String SECRET_NAME = "VaultMasterDBCredentials"; // Your AWS Secret Name
    private static final Region REGION = Region.US_EAST_2; // Your AWS Region

    public static String getSecret(String key) {
        try {
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.US_EAST_2)
                    .build();

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId("VaultMasterDBCredentials")
                    .build();
            GetSecretValueResponse response = client.getSecretValue(request);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode secretJson = objectMapper.readTree(response.secretString());

            // Debugging: Print secrets to verify values
            System.out.println("Fetched Secret from AWS: " + secretJson.toString());

            return secretJson.get(key).asText();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving secret from AWS Secrets Manager", e);
        }
    }

}
