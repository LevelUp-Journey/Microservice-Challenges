package com.levelupjourney.microservicechallenges.challenge.domain.model.entities;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import jakarta.persistence.*;
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
}
