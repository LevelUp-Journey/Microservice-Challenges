package com.levelupjourney.microservicechallenges.challenge.application.acl;

import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.CreateChallengeCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.StarChallengeByStudentIdCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.commands.UnStarChallengeByStudentIdCommand;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeByIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengesByStateQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengesByTeacherIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeStarsAmountQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeTestsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.ChallengeState;
import com.levelupjourney.microservicechallenges.challenge.domain.model.valueobjects.TeacherId;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeCommandService;
import com.levelupjourney.microservicechallenges.challenge.domain.services.ChallengeQueryService;
import com.levelupjourney.microservicechallenges.challenge.domain.services.TestQueryService;
import com.levelupjourney.microservicechallenges.challenge.interfaces.acl.ChallengeContextFacade;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.StudentId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChallengeContextFacadeImpl implements ChallengeContextFacade {
    
    private final ChallengeCommandService challengeCommandService;
    private final ChallengeQueryService challengeQueryService;
    private final TestQueryService testQueryService;

    public ChallengeContextFacadeImpl(ChallengeCommandService challengeCommandService,
                                    ChallengeQueryService challengeQueryService,
                                    TestQueryService testQueryService) {
        this.challengeCommandService = challengeCommandService;
        this.challengeQueryService = challengeQueryService;
        this.testQueryService = testQueryService;
    }

    @Override
    public String createChallenge(String title, String description, String difficulty, String teacherId) {
        try {
            var command = new CreateChallengeCommand(
                new TeacherId(UUID.fromString(teacherId)),
                title,
                description
            );
            var challenge = challengeCommandService.handle(command);
            return challenge.isEmpty() ? null : challenge.get().getId().id().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ChallengeBasicInfo fetchChallengeBasicInfoById(String challengeId) {
        try {
            var query = new GetChallengeByIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var challenge = challengeQueryService.handle(query);
            
            if (challenge.isEmpty()) {
                return null;
            }
            
            var c = challenge.get();
            return new ChallengeBasicInfo(
                c.getId().id().toString(),
                c.getTitle(),
                c.getDescription(),
                "MEDIUM", // Default difficulty since it's not stored in the entity
                c.getState().toString(),
                c.getTeacherId().id().toString()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isChallengePublished(String challengeId) {
        try {
            var challenge = fetchChallengeBasicInfoById(challengeId);
            return challenge != null && "PUBLISHED".equals(challenge.state());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<String> fetchPublishedChallengeIds() {
        try {
            var query = new GetChallengesByStateQuery(ChallengeState.PUBLISHED);
            var challenges = challengeQueryService.handle(query);
            return challenges.stream()
                    .map(challenge -> challenge.getId().id().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<String> fetchChallengeIdsByTeacherId(String teacherId) {
        try {
            var query = new GetChallengesByTeacherIdQuery(new TeacherId(UUID.fromString(teacherId)));
            var challenges = challengeQueryService.handle(query);
            return challenges.stream()
                    .map(challenge -> challenge.getId().id().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public boolean starChallenge(String challengeId, String studentId) {
        try {
            var command = new StarChallengeByStudentIdCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new StudentId(UUID.fromString(studentId))
            );
            challengeCommandService.handle(command);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean unstarChallenge(String challengeId, String studentId) {
        try {
            var command = new UnStarChallengeByStudentIdCommand(
                new ChallengeId(UUID.fromString(challengeId)),
                new StudentId(UUID.fromString(studentId))
            );
            challengeCommandService.handle(command);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getChallengeStarsCount(String challengeId) {
        try {
            var query = new GetChallengeStarsAmountQuery(new ChallengeId(UUID.fromString(challengeId)));
            return challengeQueryService.handle(query);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getChallengeTestsCount(String challengeId) {
        try {
            var query = new GetChallengeTestsByChallengeIdQuery(new ChallengeId(UUID.fromString(challengeId)));
            var tests = testQueryService.handle(query);
            return tests.size();
        } catch (Exception e) {
            return 0;
        }
    }
}
