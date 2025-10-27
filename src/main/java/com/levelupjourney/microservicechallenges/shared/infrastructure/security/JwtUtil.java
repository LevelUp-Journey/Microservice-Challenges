package com.levelupjourney.microservicechallenges.shared.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class for JWT token operations.
 * Decodes and verifies JWT tokens using the configured secret.
 */
@Component
public class JwtUtil {

    private final Algorithm algorithm;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC512(jwtSecret);
    }

    /**
     * Extracts and verifies the userId from a JWT token.
     *
     * @param token The JWT token string (without "Bearer " prefix)
     * @return The userId from the token payload, or null if extraction/verification fails
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

            // Verify and decode JWT
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(cleanToken);

            // Extract userId from payload
            String userId = decodedJWT.getClaim("userId").asString();

            return userId;
        } catch (Exception e) {
            // Log error and return null if token is invalid
            System.err.println("Error verifying/decoding JWT token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts and verifies the subject (sub) from a JWT token.
     *
     * @param token The JWT token string
     * @return The subject from the token payload, or null if extraction/verification fails
     */
    public String extractSubject(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            String cleanToken = token.startsWith("Bearer ")
                ? token.substring(7)
                : token;

            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(cleanToken);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            System.err.println("Error verifying/decoding JWT token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts and verifies the email from a JWT token.
     *
     * @param token The JWT token string
     * @return The email from the token payload, or null if extraction/verification fails
     */
    public String extractEmail(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            String cleanToken = token.startsWith("Bearer ")
                ? token.substring(7)
                : token;

            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(cleanToken);
            return decodedJWT.getClaim("email").asString();
        } catch (Exception e) {
            System.err.println("Error verifying/decoding JWT token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts and verifies the roles from a JWT token.
     *
     * @param token The JWT token string
     * @return The list of roles from the token payload, or empty list if extraction/verification fails
     */
    public List<String> extractRoles(String token) {
        if (token == null || token.isBlank()) {
            return List.of();
        }

        try {
            String cleanToken = token.startsWith("Bearer ")
                ? token.substring(7)
                : token;

            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(cleanToken);
            return decodedJWT.getClaim("roles").asList(String.class);
        } catch (Exception e) {
            System.err.println("Error verifying/decoding JWT token roles: " + e.getMessage());
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
