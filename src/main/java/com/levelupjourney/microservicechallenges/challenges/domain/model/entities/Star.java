package com.levelupjourney.microservicechallenges.challenges.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Star {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "starred_at", nullable = false)
    private LocalDateTime starredAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Star star = (Star) o;
        return Objects.equals(userId, star.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}