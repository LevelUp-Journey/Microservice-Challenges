package com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solution.domain.model.aggregates.Solution;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.SolutionResource;

import java.util.stream.Collectors;

/**
 * Assembler to transform domain models into REST resources
 */
public class SolutionResourceFromEntityAssembler {

    public static SolutionResource toResourceFromEntity(Solution solution) {
        return new SolutionResource(
                solution.getId().id().toString(),
                solution.getStudentId().id().toString(),
                solution.getChallengeId().id().toString(),
                solution.getLanguage().name(),
                solution.getCode(),
                solution.getPassedTests().getTestIds().stream()
                        .map(testId -> testId.id().toString())
                        .collect(Collectors.toList()),
                solution.getPassedTestsCount()
        );
    }
}
