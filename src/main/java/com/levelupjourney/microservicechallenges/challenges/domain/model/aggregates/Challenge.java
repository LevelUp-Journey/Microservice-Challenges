package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenges.domain.model.entities.ChallengeTag;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeStatus;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class Challenge extends AuditableAbstractAggregateRoot<Challenge> {
    
    @Id
    private UUID id;

    @Embedded
    private TeacherId teacherId;

    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private Integer experiencePoints;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    @OneToMany(mappedBy = "challengeId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CodeVersion> versions = new ArrayList<>();
    
    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeTag> tags = new ArrayList<>();

    public Challenge(CreateChallengeCommand command) {
        this.id = UUID.randomUUID();
        this.teacherId = command.teacherId();
        this.name = command.name();
        this.description = command.description();
        this.experiencePoints = command.experiencePoints();
        this.status = ChallengeStatus.DRAFT;
    }
    
    // Business methods
    public void publish() {
        // TODO: Add validation logic if needed
        this.status = ChallengeStatus.PUBLISHED;
    }
    
    public void validateCanStart() {
        if (this.status != ChallengeStatus.PUBLISHED) {
            throw new IllegalStateException("Challenge must be published before starting");
        }
    }
    
    public void updateDetails(String name, String description, Integer experiencePoints, List<ChallengeTag> tags) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (experiencePoints != null && experiencePoints >= 0) {
            this.experiencePoints = experiencePoints;
        }
        if (tags != null) {
            // Clear existing tags
            this.tags.clear();
            // Add new tags ensuring proper relationship
            tags.forEach(this::addTag);
        }
    }

    // Helper method to maintain bidirectional relationship consistency
    public void addTag(ChallengeTag tag) {
        if (tag != null) {
            this.tags.add(tag);
            tag.setChallenge(this);
        }
    }

    // Helper method to remove a tag while maintaining consistency
    public void removeTag(ChallengeTag tag) {
        if (tag != null) {
            this.tags.remove(tag);
        }
    }
}
