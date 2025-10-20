package com.levelupjourney.microservicechallenges.shared.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

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
}
