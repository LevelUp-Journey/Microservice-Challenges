package com.levelupjourney.microservicechallenges.challenges.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class ChallengeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String color;

    private String iconUrl;

    public ChallengeTag(String name, String color, String iconUrl) {
        this.name = name;
        this.color = color;
        this.iconUrl = iconUrl;
    }
}
