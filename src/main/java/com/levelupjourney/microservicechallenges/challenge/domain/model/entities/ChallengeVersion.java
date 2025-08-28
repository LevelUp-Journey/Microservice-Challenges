package com.levelupjourney.microservicechallenges.challenge.domain.model.entities;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChallengeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    private Language version;

    private String defaultStudentCode;

    @OneToMany(mappedBy = "challengeVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests;

    // Default constructor for JPA
    protected ChallengeVersion() {}

    // Constructor for creating new challenge versions
    public ChallengeVersion(Language version, String defaultStudentCode) {
        if (version == null) throw new IllegalArgumentException("Version language cannot be null");
        if (defaultStudentCode == null || defaultStudentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Default student code cannot be null or empty");
        }
        
        this.version = version;
        this.defaultStudentCode = defaultStudentCode.trim();
        this.tests = new ArrayList<>();
    }

    // Business methods
    public void addTest(Test test) {
        if (test == null) throw new IllegalArgumentException("Test cannot be null");
        this.tests.add(test);
        test.setChallengeVersion(this);
    }

    public void removeTest(Test test) {
        if (test == null) throw new IllegalArgumentException("Test cannot be null");
        this.tests.remove(test);
        test.setChallengeVersion(null);
    }

    public void updateDefaultCode(String newDefaultCode) {
        if (newDefaultCode == null || newDefaultCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Default code cannot be null or empty");
        }
        this.defaultStudentCode = newDefaultCode.trim();
    }

    public int getTestsCount() {
        return this.tests.size();
    }

    // Setters for relationships
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    // Getters
    public Long getId() { return id; }
    public Challenge getChallenge() { return challenge; }
    public Language getVersion() { return version; }
    public String getDefaultStudentCode() { return defaultStudentCode; }
    public List<Test> getTests() { return new ArrayList<>(tests); }
}
