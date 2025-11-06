package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersion;
import com.levelupjourney.microservicechallenges.challenges.domain.model.aggregates.CodeVersionTest;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionResource;
import com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource.CodeVersionTestResource;

import java.util.List;
import java.util.stream.Collectors;

public class CodeVersionResourceFromEntityAssembler {

    /**
     * Convert CodeVersion entity to resource for teachers/admins (shows all test details)
     */
    public static CodeVersionResource toResourceFromEntity(CodeVersion entity) {
        return toResourceFromEntity(entity, false);
    }

    /**
     * Convert CodeVersion entity to resource with role-based test filtering
     * 
     * @param entity The CodeVersion entity
     * @param isStudent true if the user is a student (will hide secret test details)
     * @return CodeVersionResource with filtered tests
     */
    public static CodeVersionResource toResourceFromEntity(CodeVersion entity, boolean isStudent) {
        List<CodeVersionTestResource> tests = entity.getTests().stream()
                .map(test -> toTestResource(test, isStudent))
                .collect(Collectors.toList());
        
        return new CodeVersionResource(
            entity.getId().id().toString(),
            entity.getChallengeId().id().toString(),
            entity.getLanguage().name(),
            entity.getInitialCode(),
            entity.getFunctionName(),
            tests
        );
    }

    /**
     * Convert CodeVersionTest to resource, hiding details if test is secret and user is a student
     */
    private static CodeVersionTestResource toTestResource(CodeVersionTest test, boolean isStudent) {
        // If test is secret AND user is a student, return empty strings for sensitive fields
        if (test.getIsSecret() && isStudent) {
            return new CodeVersionTestResource(
                test.getId().id().toString(),
                test.getCodeVersionId().id().toString(),
                "", // empty input
                "", // empty expectedOutput
                "", // empty customValidationCode
                "", // empty failureMessage
                test.getIsSecret()
            );
        }
        
        // Otherwise, return full test details (for public tests or teacher/admin users)
        return new CodeVersionTestResource(
            test.getId().id().toString(),
            test.getCodeVersionId().id().toString(),
            test.getInput(),
            test.getExpectedOutput(),
            test.getCustomValidationCode(),
            test.getFailureMessage(),
            test.getIsSecret()
        );
    }
}