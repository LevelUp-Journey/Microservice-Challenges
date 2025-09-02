package com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.Star;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import jakarta.persistence.*;
import jakarta.persistence.AttributeOverride;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Challenge extends AuditableAbstractAggregateRoot<Challenge> {

    @EmbeddedId
    private ChallengeId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "teacher_id"))
    private TeacherId teacherId;

    private String title;

    private String description;

    @ElementCollection
    @CollectionTable(
        name = "challenge_stars",
        joinColumns = @JoinColumn(name = "owner_challenge_id")
    )
    private List<Star> stars;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeVersion> versions;

    @Enumerated(EnumType.STRING)
    private ChallengeState state;

    // Default constructor for JPA
    protected Challenge() {}

    // Constructor for creating new challenges
    public Challenge(TeacherId teacherId, String title, String description) {
        if (teacherId == null) throw new IllegalArgumentException("Teacher ID cannot be null");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be null or empty");
        if (description == null || description.trim().isEmpty()) throw new IllegalArgumentException("Description cannot be null or empty");
        
        this.id = new ChallengeId(UUID.randomUUID());
        this.teacherId = teacherId;
        this.title = title.trim();
        this.description = description.trim();
        this.state = ChallengeState.DRAFT;
        this.stars = new ArrayList<>();
        this.versions = new ArrayList<>();
    }

    // Business methods
    public void updateTitle(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (this.state == ChallengeState.PUBLISHED) {
            throw new IllegalStateException("Cannot update title of a published challenge");
        }
        this.title = newTitle.trim();
    }

    public void updateDescription(String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (this.state == ChallengeState.PUBLISHED) {
            throw new IllegalStateException("Cannot update description of a published challenge");
        }
        this.description = newDescription.trim();
    }

    public void publish() {
        if (this.state != ChallengeState.DRAFT) {
            throw new IllegalStateException("Only draft challenges can be published");
        }
        if (this.versions.isEmpty()) {
            throw new IllegalStateException("Challenge must have at least one version to be published");
        }
        this.state = ChallengeState.PUBLISHED;
    }

    public void starByStudent(StudentId studentId) {
        if (studentId == null) throw new IllegalArgumentException("Student ID cannot be null");
        if (this.state != ChallengeState.PUBLISHED) {
            throw new IllegalStateException("Only published challenges can be starred");
        }
        
        Star newStar = new Star(studentId, this.id);
        if (!this.stars.contains(newStar)) {
            this.stars.add(newStar);
        }
    }

    public void unstarByStudent(StudentId studentId) {
        if (studentId == null) throw new IllegalArgumentException("Student ID cannot be null");
        
        Star starToRemove = new Star(studentId, this.id);
        this.stars.remove(starToRemove);
    }

    public void addVersion(ChallengeVersion version) {
        if (version == null) throw new IllegalArgumentException("Version cannot be null");
        if (this.state == ChallengeState.PUBLISHED) {
            throw new IllegalStateException("Cannot add version to published challenge");
        }
        this.versions.add(version);
        version.setChallenge(this);
    }

    public int getStarsCount() {
        return this.stars.size();
    }

    public boolean isStarredByStudent(StudentId studentId) {
        Star star = new Star(studentId, this.id);
        return this.stars.contains(star);
    }
}
