package vaultmaster.com.vault.config;
/*import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecretConfig {

    @Bean
    public DatabaseSecret databaseSecret() throws Exception {
        SecretsManagerClient client = SecretsManagerClient.create();
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId("VaultMasterDBCredentials") // Replace with your secret name
                .build();
        GetSecretValueResponse response = client.getSecretValue(request);
        String secretString = response.secretString();

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(secretString, DatabaseSecret.class);
    }

    public static class DatabaseSecret {
        private String username;
        private String password;
        private String engine;
        private String host;
        private int port;
        private String dbname;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEngine() {
            return engine;
        }

        public void setEngine(String engine) {
            this.engine = engine;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getDbname() {
            return dbname;
        }

        public void setDbname(String dbname) {
            this.dbname = dbname;
        }
    }
}
*/
