package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;

public record UpdateCodeVersionCommand(CodeVersionId codeVersionId, String code) {
}
