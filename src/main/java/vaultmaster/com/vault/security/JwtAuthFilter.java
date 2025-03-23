package vaultmaster.com.vault.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.List;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/2fa/")) {
            filterChain.doFilter(request, response);
            return;  // Skip JWT validation for 2FA and authentication routes
        }

        // 🔒 Continue with token validation for protected routes
        String token = jwtService.extractTokenFromRequest(request);

        if (token != null && jwtService.isTokenValid(token)) {
            String userId = jwtService.extractUserId(token);
            boolean otpVerified = jwtService.extractOtpVerified(token);

            if (otpVerified) {
                Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
                if (optionalUser.isPresent()) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userId, null, List.of());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("OTP verification required.");
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token.");
            return;
        }

        filterChain.doFilter(request, response);
    }

}
