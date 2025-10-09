package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionTestId;

import java.util.Optional;

public record UpdateCodeVersionTestCommand(CodeVersionTestId codeVersionTestId, Optional<String> input, Optional<String> expectedOutput, Optional<String> customValidationCode, Optional<String> failureMessage) {
}
