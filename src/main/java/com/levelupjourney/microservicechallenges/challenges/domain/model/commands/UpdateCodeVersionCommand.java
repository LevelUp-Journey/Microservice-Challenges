package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;

import java.util.Optional;

public record UpdateCodeVersionCommand(CodeVersionId codeVersionId, Optional<String> code, Optional<String> functionName) {
}
