package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.CodeVersionId;

public record AddCodeVersionTestCommand(CodeVersionId codeVersionId, String input, String expectedOutput, String customValidationCode, String failureMessage, Boolean isSecret) {
}
