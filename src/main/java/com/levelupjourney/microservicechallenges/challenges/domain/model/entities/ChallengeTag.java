package com.levelupjourney.microservicechallenges.challenges.domain.model.entities;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.Challenge;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "challenge_tags")
public class ChallengeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String color;

    private String iconUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    public ChallengeTag(String name, String color, String iconUrl) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.color = color;
        this.iconUrl = iconUrl;
    }

    // Helper method to set the challenge relationship
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeTag that = (ChallengeTag) o;
        return Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(iconUrl, that.iconUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, iconUrl);
    }
}
