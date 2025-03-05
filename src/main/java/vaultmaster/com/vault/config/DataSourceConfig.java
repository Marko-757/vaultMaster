package vaultmaster.com.vault.config;
/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(SecretConfig.DatabaseSecret secret) {
        String url = String.format("jdbc:postgresql://%s:%d/%s", secret.getHost(), secret.getPort(), secret.getDbname());
        return DataSourceBuilder.create()
                .url(url)
                .username(secret.getUsername())
                .password(secret.getPassword())
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
*/