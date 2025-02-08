package vaultmaster.com.vault;

import vaultmaster.com.vault.config.SecretManagerUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VaultApplication {
	public static void main(String[] args) {
		// Fetch secrets from AWS and set as environment variables
		System.setProperty("DB_HOST", SecretManagerUtil.getSecret("host"));
		System.setProperty("DB_PORT", SecretManagerUtil.getSecret("port"));
		System.setProperty("DB_NAME", SecretManagerUtil.getSecret("dbname"));
		System.setProperty("DB_USER", SecretManagerUtil.getSecret("username"));
		System.setProperty("DB_PASSWORD", SecretManagerUtil.getSecret("password"));

		// Verify if the values are correctly set
		System.out.println("DB_HOST: " + System.getProperty("DB_HOST"));
		System.out.println("DB_PORT: " + System.getProperty("DB_PORT"));
		System.out.println("DB_NAME: " + System.getProperty("DB_NAME"));
		System.out.println("DB_USER: " + System.getProperty("DB_USER"));

		SpringApplication.run(VaultApplication.class, args);
	}
}
