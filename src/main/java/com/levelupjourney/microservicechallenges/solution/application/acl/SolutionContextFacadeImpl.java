package com.levelupjourney.microservicechallenges.solution.application.acl;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.Language;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.CreateSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.SubmitSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.model.commands.UpdateSolutionCommand;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionByStudentIdAndChallengeIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.queries.GetSolutionsByStudentIdQuery;
import com.levelupjourney.microservicechallenges.solution.domain.model.valueobjects.SolutionId;
import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solution.interfaces.acl.SolutionContextFacade;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of SolutionContextFacade
 * 
 * This service acts as an Anti-Corruption Layer for the Solution bounded context.
 * It provides simplified access to solution operations for other bounded contexts,
 * translating between external requests and internal domain operations.
 */
@Service
public class SolutionContextFacadeImpl implements SolutionContextFacade {
    
    private final SolutionCommandService solutionCommandService;
    private final SolutionQueryService solutionQueryService;

    public SolutionContextFacadeImpl(SolutionCommandService solutionCommandService,
                                   SolutionQueryService solutionQueryService) {
        this.solutionCommandService = solutionCommandService;
        this.solutionQueryService = solutionQueryService;
    }

    @Override
    public String createSolution(String challengeId, String studentId, String code, String language) {
        try {
            var command = new CreateSolutionCommand(
                new StudentId(UUID.fromString(studentId)),
                new ChallengeId(UUID.fromString(challengeId)),
                Language.valueOf(language.toUpperCase()),
                code
            );
            var solution = solutionCommandService.handle(command);
            return solution.isEmpty() ? null : solution.get().getId().id().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String submitSolution(String challengeId, String studentId, String code, String language) {
        try {
            var command = new SubmitSolutionCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new StudentId(UUID.fromString(studentId)),
                code,
                Language.valueOf(language.toUpperCase())
            );
            var solution = solutionCommandService.handle(command);
            return solution.isEmpty() ? null : solution.get().getId().id().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean updateSolution(String solutionId, String code, String language) {
        try {
            var command = new UpdateSolutionCommand(
                new SolutionId(UUID.fromString(solutionId)),
                code,
                Language.valueOf(language.toUpperCase())
            );
            var solution = solutionCommandService.handle(command);
            return solution.isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public SolutionBasicInfo fetchSolutionBasicInfoById(String solutionId) {
        try {
            var query = new GetSolutionByIdQuery(new SolutionId(UUID.fromString(solutionId)));
            var solution = solutionQueryService.handle(query);
            
            if (solution.isEmpty()) {
                return null;
            }
            
            var s = solution.get();
            return new SolutionBasicInfo(
                s.getId().id().toString(),
                s.getChallengeId().id().toString(),
                s.getStudentId().id().toString(),
                s.getLanguage().toString(),
                null, // submittedAt not available in current model
                false // isSubmitted not available in current model
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean hasSolutionForChallenge(String studentId, String challengeId) {
        try {
            var query = new GetSolutionByStudentIdAndChallengeIdQuery(
                new StudentId(UUID.fromString(studentId)),
                new ChallengeId(UUID.fromString(challengeId))
            );
            var solution = solutionQueryService.handle(query);
            return solution.isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String fetchSolutionIdByStudentAndChallenge(String studentId, String challengeId) {
        try {
            var query = new GetSolutionByStudentIdAndChallengeIdQuery(
                new StudentId(UUID.fromString(studentId)),
                new ChallengeId(UUID.fromString(challengeId))
            );
            var solution = solutionQueryService.handle(query);
            return solution.isEmpty() ? null : solution.get().getId().id().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> fetchSolutionIdsByChallenge(String challengeId) {
        try {
            var query = new GetSolutionsByChallengeIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var solutions = solutionQueryService.handle(query);
            return solutions.stream()
                    .map(solution -> solution.getId().id().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<String> fetchSolutionIdsByStudent(String studentId) {
        try {
            var query = new GetSolutionsByStudentIdQuery(new StudentId(UUID.fromString(studentId)));
            var solutions = solutionQueryService.handle(query);
            return solutions.stream()
                    .map(solution -> solution.getId().id().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public int getSolutionsCountByChallenge(String challengeId) {
        try {
            var solutions = fetchSolutionIdsByChallenge(challengeId);
            return solutions.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getSolutionsCountByStudent(String studentId) {
        try {
            var solutions = fetchSolutionIdsByStudent(studentId);
            return solutions.size();
        } catch (Exception e) {
            return 0;
        }
    }
}
