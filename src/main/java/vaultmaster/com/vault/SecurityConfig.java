package vaultmaster.com.vault;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for testing
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll() // Allow unrestricted access to all endpoints
                )
                .httpBasic(httpBasic -> {}); // Enable HTTP Basic (no-op for now)
        return http.build();
    }
}
