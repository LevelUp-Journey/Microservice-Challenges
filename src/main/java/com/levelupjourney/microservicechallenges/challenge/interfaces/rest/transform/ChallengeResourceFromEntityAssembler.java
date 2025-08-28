package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Challenge;
import com.levelupjourney.microservicechallenges.challenge.domain.model.aggregates.Test;
import com.levelupjourney.microservicechallenges.challenge.domain.model.entities.ChallengeVersion;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.*;

import java.util.stream.Collectors;

/**
 * Assembler to transform domain models into REST resources
 */
public class ChallengeResourceFromEntityAssembler {

    public static ChallengeResource toResourceFromEntity(Challenge challenge) {
        return new ChallengeResource(
                challenge.getId().id().toString(),
                challenge.getTeacherId().id().toString(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getState().name(),
                challenge.getStars().stream()
                        .map(star -> new StarResource(
                                star.userId().id().toString(),
                                star.challengeId().id().toString()
                        ))
                        .collect(Collectors.toList()),
                challenge.getVersions().stream()
                        .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList()),
                challenge.getStarsCount()
        );
    }

    public static ChallengeVersionResource toResourceFromEntity(ChallengeVersion version) {
        return new ChallengeVersionResource(
                version.getId(),
                version.getVersion().name(),
                version.getDefaultStudentCode(),
                version.getTests().stream()
                        .map(ChallengeResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList())
        );
    }

    public static TestResource toResourceFromEntity(Test test) {
        return new TestResource(
                test.getId().id().toString(),
                test.getTitle(),
                test.getHint(),
                test.getOnErrorHint(),
                test.getTestCode(),
                test.getInput(),
                test.getExpectedOutput()
        );
    }
}
