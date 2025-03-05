package vaultmaster.com.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class VaultApplication {

	private final Map<String, String> databaseSecrets;

	public VaultApplication(Map<String, String> databaseSecrets) {
		this.databaseSecrets = databaseSecrets;

		// ✅ Debugging: Print secrets to verify they are loaded
		System.out.println("DB_HOST: " + databaseSecrets.get("host"));
		System.out.println("DB_PORT: " + databaseSecrets.get("port"));
		System.out.println("DB_NAME: " + databaseSecrets.get("dbname"));
		System.out.println("DB_USER: " + databaseSecrets.get("username"));
	}

	public static void main(String[] args) {
		SpringApplication.run(VaultApplication.class, args);
	}
}
