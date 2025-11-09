package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.challenges.infrastructure.persistence.jpa.repositories.ChallengeRepository;
import com.levelupjourney.microservicechallenges.shared.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-testing-purposes-minimum-256-bits-required-here-for-hmac512"
})
class ChallengeGuidesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String teacherToken;
    private String teacherId;
    private Challenge testChallenge;
    private UUID guideId1;
    private UUID guideId2;

    @BeforeEach
    void setUp() {
        // Clear repository
        challengeRepository.deleteAll();

        // Setup teacher
        teacherId = UUID.randomUUID().toString();
        teacherToken = jwtUtil.generateToken(teacherId, List.of("ROLE_TEACHER"));

        // Create guide IDs for testing
        guideId1 = UUID.randomUUID();
        guideId2 = UUID.randomUUID();

        // Create a test challenge with guides
        testChallenge = new Challenge(
                new com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand(
                        new TeacherId(UUID.fromString(teacherId)),
                        "Test Challenge with Guides",
                        "A challenge to test guides functionality",
                        100,
                        Difficulty.EASY,
                        List.of("#test", "#guides"),
                        List.of(guideId1),
                        3
                )
        );
        testChallenge = challengeRepository.save(testChallenge);
    }

    @Test
    void shouldCreateChallengeWithGuidesAndMaxAttempts() throws Exception {
        // Arrange
        UUID guideId = UUID.randomUUID();
        String requestBody = """
                {
                    "name": "Challenge with Guides",
                    "description": "Testing guides creation",
                    "experiencePoints": 150,
                    "difficulty": "MEDIUM",
                    "tags": ["#test"],
                    "guides": ["%s"],
                    "maxAttemptsBeforeGuides": 5
                }
                """.formatted(guideId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Challenge with Guides"))
                .andExpect(jsonPath("$.guides", hasSize(1)))
                .andExpect(jsonPath("$.guides[0]").value(guideId.toString()))
                .andExpect(jsonPath("$.maxAttemptsBeforeGuides").value(5));
    }

    @Test
    void shouldCreateChallengeWithoutGuides() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "name": "Challenge without Guides",
                    "description": "Testing without guides",
                    "experiencePoints": 100,
                    "difficulty": "EASY",
                    "tags": ["#test"]
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guides", hasSize(0)))
                .andExpect(jsonPath("$.maxAttemptsBeforeGuides").value(nullValue()));
    }

    @Test

    void shouldAddGuideToChallenge() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId2)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(2)))
                .andExpect(jsonPath("$.guides", hasItem(guideId1.toString())))
                .andExpect(jsonPath("$.guides", hasItem(guideId2.toString())));
    }

    @Test

    void shouldNotAddDuplicateGuide() throws Exception {
        // Act - Try to add the same guide twice
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId1)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(1))); // Should still have only 1
    }

    @Test

    void shouldRemoveGuideFromChallenge() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId1)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(0)));
    }

    @Test

    void shouldReturn404WhenAddingGuideToNonExistentChallenge() throws Exception {
        // Arrange
        UUID nonExistentChallengeId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        nonExistentChallengeId, guideId2)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Challenge not found")));
    }

    @Test

    void shouldReturn403WhenNonOwnerTriesToAddGuide() throws Exception {
        // Arrange - Create a different teacher token
        String otherTeacherId = UUID.randomUUID().toString();
        String otherTeacherToken = jwtUtil.generateToken(otherTeacherId, List.of("ROLE_TEACHER"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId2)
                        .header("Authorization", "Bearer " + otherTeacherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(containsString("not authorized")));
    }

    @Test

    void shouldReturn403WhenNonOwnerTriesToRemoveGuide() throws Exception {
        // Arrange - Create a different teacher token
        String otherTeacherId = UUID.randomUUID().toString();
        String otherTeacherToken = jwtUtil.generateToken(otherTeacherId, List.of("ROLE_TEACHER"));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId1)
                        .header("Authorization", "Bearer " + otherTeacherToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(containsString("not authorized")));
    }

    @Test

    void shouldGetChallengeWithGuides() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/challenges/{challengeId}", testChallenge.getId().id())
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(1)))
                .andExpect(jsonPath("$.guides[0]").value(guideId1.toString()))
                .andExpect(jsonPath("$.maxAttemptsBeforeGuides").value(3));
    }

    @Test

    void shouldAddMultipleGuidesSequentially() throws Exception {
        // Add second guide
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId2)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(2)));

        // Add third guide
        UUID guideId3 = UUID.randomUUID();
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId3)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(3)));
    }

    @Test

    void shouldRemoveSpecificGuideWhileKeepingOthers() throws Exception {
        // First add a second guide
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId2)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(2)));

        // Remove the first guide
        mockMvc.perform(delete("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId1)
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guides", hasSize(1)))
                .andExpect(jsonPath("$.guides[0]").value(guideId2.toString()));
    }

    @Test

    void shouldReturn403WhenStudentTriesToAddGuide() throws Exception {
        // Arrange
        String studentId = UUID.randomUUID().toString();
        String studentToken = jwtUtil.generateToken(studentId, List.of("ROLE_STUDENT"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId2)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test

    void shouldReturn403WhenStudentTriesToRemoveGuide() throws Exception {
        // Arrange
        String studentId = UUID.randomUUID().toString();
        String studentToken = jwtUtil.generateToken(studentId, List.of("ROLE_STUDENT"));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), guideId1)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test

    void shouldHandleInvalidGuideIdFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges/{challengeId}/guides/{guideId}",
                        testChallenge.getId().id(), "invalid-uuid")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isBadRequest());
    }

    @Test

    void shouldCreateChallengeWithMaxAttemptsButNoGuides() throws Exception {
        // Arrange - This tests edge case where teacher sets max attempts but doesn't add guides yet
        String requestBody = """
                {
                    "name": "Challenge with Max Attempts Only",
                    "description": "Testing max attempts without guides",
                    "experiencePoints": 100,
                    "difficulty": "EASY",
                    "tags": ["#test"],
                    "guides": [],
                    "maxAttemptsBeforeGuides": 5
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/challenges")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guides", hasSize(0)))
                .andExpect(jsonPath("$.maxAttemptsBeforeGuides").value(5));
    }
}

