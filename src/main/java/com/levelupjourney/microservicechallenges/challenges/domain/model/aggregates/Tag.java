package com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;
import com.levelupjourney.microservicechallenges.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag extends AuditableAbstractAggregateRoot<Tag> {

    @EmbeddedId
    private TagId id;

    @Column(nullable = false, unique = true)
    private String name;

    private String color;

    private String iconUrl;

    public Tag(String name, String color, String iconUrl) {
        this.id = new TagId(UUID.randomUUID());
        this.name = name;
        this.color = color;
        this.iconUrl = iconUrl;
    }

    public void updateDetails(String name, String color, String iconUrl) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
        if (iconUrl != null) {
            this.iconUrl = iconUrl;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}