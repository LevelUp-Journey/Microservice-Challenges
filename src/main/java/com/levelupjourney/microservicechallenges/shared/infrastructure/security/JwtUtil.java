package com.levelupjourney.microservicechallenges.shared.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class for JWT token operations.
 * Decodes JWT tokens to extract user information without signature verification.
 * 
 * Note: This implementation does NOT verify the JWT signature. 
 * Signature verification should be handled by API Gateway or authentication middleware.
 */
@Component
public class JwtUtil {

    /**
     * Extracts the userId from a JWT token.
     * 
     * @param token The JWT token string (without "Bearer " prefix)
     * @return The userId from the token payload, or null if extraction fails
     */
    public String extractUserId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            // Remove "Bearer " prefix if present
            String cleanToken = token.startsWith("Bearer ") 
                ? token.substring(7) 
                : token;

            // Decode JWT without verification (verification done at API Gateway)
            DecodedJWT decodedJWT = JWT.decode(cleanToken);
            
            // Extract userId from payload
            String userId = decodedJWT.getClaim("userId").asString();
            
            return userId;
        } catch (Exception e) {
            // Log error and return null if token is invalid
            System.err.println("Error decoding JWT token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the subject (sub) from a JWT token.
     * 
     * @param token The JWT token string
     * @return The subject from the token payload, or null if extraction fails
     */
    public String extractSubject(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            String cleanToken = token.startsWith("Bearer ") 
                ? token.substring(7) 
                : token;

            DecodedJWT decodedJWT = JWT.decode(cleanToken);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            System.err.println("Error decoding JWT token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the email from a JWT token.
     * 
     * @param token The JWT token string
     * @return The email from the token payload, or null if extraction fails
     */
    public String extractEmail(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            String cleanToken = token.startsWith("Bearer ") 
                ? token.substring(7) 
                : token;

            DecodedJWT decodedJWT = JWT.decode(cleanToken);
            return decodedJWT.getClaim("email").asString();
        } catch (Exception e) {
            System.err.println("Error decoding JWT token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the roles from a JWT token.
     * 
     * @param token The JWT token string
     * @return The list of roles from the token payload, or empty list if extraction fails
     */
    public List<String> extractRoles(String token) {
        if (token == null || token.isBlank()) {
            return List.of();
        }

        try {
            String cleanToken = token.startsWith("Bearer ") 
                ? token.substring(7) 
                : token;

            DecodedJWT decodedJWT = JWT.decode(cleanToken);
            return decodedJWT.getClaim("roles").asList(String.class);
        } catch (Exception e) {
            System.err.println("Error decoding JWT token roles: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Checks if the token contains a specific role.
     * 
     * @param token The JWT token string
     * @param role The role to check for
     * @return true if the token contains the role, false otherwise
     */
    public boolean hasRole(String token, String role) {
        List<String> roles = extractRoles(token);
        return roles.contains(role);
    }
}
