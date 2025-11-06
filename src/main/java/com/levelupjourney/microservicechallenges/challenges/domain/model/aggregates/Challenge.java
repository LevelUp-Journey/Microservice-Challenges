package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.Star;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class Challenge extends AuditableAbstractAggregateRoot<Challenge> {
    
    @EmbeddedId
    private ChallengeId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "teacher_id"))
    private TeacherId teacherId;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer experiencePoints;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, columnDefinition = "varchar(255) default 'MEDIUM'")
    private Difficulty difficulty;

    @ElementCollection
    @CollectionTable(name = "challenge_stars", joinColumns = @JoinColumn(name = "challenge_id"))
    private List<Star> stars = new ArrayList<>();

    @OneToMany(mappedBy = "challengeId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CodeVersion> versions = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "challenge_tags", joinColumns = @JoinColumn(name = "challenge_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    public Challenge(CreateChallengeCommand command) {
        this.id = new ChallengeId(UUID.randomUUID());
        this.teacherId = command.teacherId();
        this.name = command.name();
        this.description = command.description();
        
        // Validate experience points against difficulty max score
        if (command.experiencePoints() > command.difficulty().getMaxScore()) {
            throw new IllegalArgumentException(
                String.format("Experience points (%d) cannot exceed maximum score for %s difficulty (%d)", 
                    command.experiencePoints(), command.difficulty(), command.difficulty().getMaxScore())
            );
        }
        
        this.experiencePoints = command.experiencePoints();
        this.difficulty = command.difficulty();
        this.status = ChallengeStatus.DRAFT;
        this.tags = command.tags() != null ? new ArrayList<>(command.tags()) : new ArrayList<>();
    }
    
    // Business methods
    public void publish() {
        // TODO: Add validation logic if needed
        this.status = ChallengeStatus.PUBLISHED;
    }
    
    public boolean canBePublished() {
        // A challenge can be published if it has at least one CodeVersion
        // and that CodeVersion has at least 3 tests
        if (this.versions == null || this.versions.isEmpty()) {
            return false;
        }
        
        // Check if at least one version has 3 or more tests
        return this.versions.stream()
            .anyMatch(version -> version.getTests() != null && version.getTests().size() >= 3);
    }
    
    public void validateCanStart() {
        if (this.status != ChallengeStatus.PUBLISHED) {
            throw new IllegalStateException("Challenge must be published before starting");
        }
    }
    
    public void updateDetails(String name, String description, Integer experiencePoints, List<String> tags) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (experiencePoints != null && experiencePoints >= 0) {
            // Validate experience points against current difficulty max score
            if (experiencePoints > this.difficulty.getMaxScore()) {
                throw new IllegalArgumentException(
                    String.format("Experience points (%d) cannot exceed maximum score for %s difficulty (%d)", 
                        experiencePoints, this.difficulty, this.difficulty.getMaxScore())
                );
            }
            this.experiencePoints = experiencePoints;
        }
        if (tags != null) {
            // Replace all tags
            this.tags.clear();
            this.tags.addAll(tags);
        }
    }
    
    public void updateDetails(String name, String description, Integer experiencePoints, Difficulty difficulty, List<String> tags) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (difficulty != null) {
            this.difficulty = difficulty;
        }
        if (experiencePoints != null && experiencePoints >= 0) {
            // Validate experience points against difficulty max score (use new difficulty if provided, otherwise current)
            Difficulty validationDifficulty = difficulty != null ? difficulty : this.difficulty;
            if (experiencePoints > validationDifficulty.getMaxScore()) {
                throw new IllegalArgumentException(
                    String.format("Experience points (%d) cannot exceed maximum score for %s difficulty (%d)", 
                        experiencePoints, validationDifficulty, validationDifficulty.getMaxScore())
                );
            }
            this.experiencePoints = experiencePoints;
        }
        if (tags != null) {
            // Replace all tags
            this.tags.clear();
            this.tags.addAll(tags);
        }
    }
    
    public void updateDetails(String name, String description, Integer experiencePoints, ChallengeStatus status, List<String> tags) {
        // Update basic details
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (experiencePoints != null && experiencePoints >= 0) {
            // Validate experience points against current difficulty max score
            if (experiencePoints > this.difficulty.getMaxScore()) {
                throw new IllegalArgumentException(
                    String.format("Experience points (%d) cannot exceed maximum score for %s difficulty (%d)", 
                        experiencePoints, this.difficulty, this.difficulty.getMaxScore())
                );
            }
            this.experiencePoints = experiencePoints;
        }
        if (tags != null) {
            // Replace all tags
            this.tags.clear();
            this.tags.addAll(tags);
        }
        
        // Handle status change with validation
        if (status != null && status != this.status) {
            if (status == ChallengeStatus.PUBLISHED) {
                if (!canBePublished()) {
                    throw new IllegalStateException(
                        "Cannot publish challenge: must have at least one code version with at least 3 tests");
                }
            }
            this.status = status;
        }
    }

    public void updateDetails(String name, String description, Integer experiencePoints, Difficulty difficulty, ChallengeStatus status, List<String> tags) {
        // Update basic details
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (difficulty != null) {
            this.difficulty = difficulty;
        }
        if (experiencePoints != null && experiencePoints >= 0) {
            // Validate experience points against difficulty max score (use new difficulty if provided, otherwise current)
            Difficulty validationDifficulty = difficulty != null ? difficulty : this.difficulty;
            if (experiencePoints > validationDifficulty.getMaxScore()) {
                throw new IllegalArgumentException(
                    String.format("Experience points (%d) cannot exceed maximum score for %s difficulty (%d)", 
                        experiencePoints, validationDifficulty, validationDifficulty.getMaxScore())
                );
            }
            this.experiencePoints = experiencePoints;
        }
        if (tags != null) {
            // Replace all tags
            this.tags.clear();
            this.tags.addAll(tags);
        }
        
        // Handle status change with validation
        if (status != null && status != this.status) {
            if (status == ChallengeStatus.PUBLISHED) {
                if (!canBePublished()) {
                    throw new IllegalStateException(
                        "Cannot publish challenge: must have at least one code version with at least 3 tests");
                }
            }
            this.status = status;
        }
    }

    // Helper method to add a tag to the challenge
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !this.tags.contains(tag.toLowerCase())) {
            this.tags.add(tag.toLowerCase());
        }
    }

    // Helper method to remove a tag from the challenge
    public void removeTag(String tag) {
        if (tag != null) {
            this.tags.remove(tag.toLowerCase());
        }
    }

    // Helper method to add a star (like) to the challenge
    public void addStar(String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            Star newStar = new Star(userId, LocalDateTime.now());
            // Remove existing star from same user if exists
            this.stars.removeIf(star -> star.getUserId().equals(userId));
            this.stars.add(newStar);
        }
    }

    // Helper method to remove a star from the challenge
    public void removeStar(String userId) {
        if (userId != null) {
            this.stars.removeIf(star -> star.getUserId().equals(userId));
        }
    }

    // Helper method to check if user has starred this challenge
    public boolean hasStarred(String userId) {
        if (userId == null) return false;
        return this.stars.stream().anyMatch(star -> star.getUserId().equals(userId));
    }
}
