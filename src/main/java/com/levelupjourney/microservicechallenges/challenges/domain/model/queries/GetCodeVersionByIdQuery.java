package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;

public record GetCodeVersionByIdQuery(
        CodeVersionId codeVersionId
) {
}
