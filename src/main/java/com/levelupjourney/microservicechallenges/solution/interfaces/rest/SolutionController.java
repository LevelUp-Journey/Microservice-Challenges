package com.levelupjourney.microservicechallenges.solution.interfaces.rest;

import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionCommandService;
import com.levelupjourney.microservicechallenges.solution.domain.services.SolutionQueryService;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.CreateSolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.SolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.SubmitSolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources.UpdateSolutionResource;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform.SolutionCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform.SolutionQueryFromParametersAssembler;
import com.levelupjourney.microservicechallenges.solution.interfaces.rest.transform.SolutionResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Solution aggregate
 * Follows RESTful conventions and DDD best practices
 */
@RestController
@RequestMapping("/api/v1/solutions")
@CrossOrigin(origins = "*")
public class SolutionController {

    private final SolutionCommandService solutionCommandService;
    private final SolutionQueryService solutionQueryService;

    public SolutionController(SolutionCommandService solutionCommandService,
                             SolutionQueryService solutionQueryService) {
        this.solutionCommandService = solutionCommandService;
        this.solutionQueryService = solutionQueryService;
    }

    // POST /api/v1/solutions - Create new solution
    @PostMapping
    public ResponseEntity<SolutionResource> createSolution(@RequestBody CreateSolutionResource resource) {
        try {
            var command = SolutionCommandFromResourceAssembler.toCommandFromResource(resource);
            var result = solutionCommandService.handle(command);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/solutions - Get all solutions
    @GetMapping
    public ResponseEntity<List<SolutionResource>> getAllSolutions(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String challengeId) {
        try {
            List<SolutionResource> solutions;
            
            if (studentId != null) {
                var query = SolutionQueryFromParametersAssembler.toGetSolutionsByStudentIdQuery(studentId);
                solutions = solutionQueryService.handle(query).stream()
                        .map(SolutionResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            } else if (challengeId != null) {
                var query = SolutionQueryFromParametersAssembler.toGetSolutionsByChallengeIdQuery(challengeId);
                solutions = solutionQueryService.handle(query).stream()
                        .map(SolutionResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            } else {
                var query = SolutionQueryFromParametersAssembler.toGetAllSolutionsQuery();
                solutions = solutionQueryService.handle(query).stream()
                        .map(SolutionResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
            }
            
            return new ResponseEntity<>(solutions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/solutions/{solutionId} - Get solution by ID
    @GetMapping("/{solutionId}")
    public ResponseEntity<SolutionResource> getSolutionById(@PathVariable String solutionId) {
        try {
            var query = SolutionQueryFromParametersAssembler.toGetSolutionByIdQuery(solutionId);
            var result = solutionQueryService.handle(query);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/v1/solutions/{solutionId} - Update solution
    @PutMapping("/{solutionId}")
    public ResponseEntity<SolutionResource> updateSolution(
            @PathVariable String solutionId,
            @RequestBody UpdateSolutionResource resource) {
        try {
            var command = SolutionCommandFromResourceAssembler.toCommandFromResource(solutionId, resource);
            var result = solutionCommandService.handle(command);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // POST /api/v1/solutions/submit - Submit solution (requires challengeId and studentId as params)
    @PostMapping("/submit")
    public ResponseEntity<SolutionResource> submitSolution(
            @RequestParam String challengeId,
            @RequestParam String studentId,
            @RequestBody SubmitSolutionResource resource) {
        try {
            var command = SolutionCommandFromResourceAssembler.toCommandFromResource(challengeId, resource);
            var result = solutionCommandService.handle(command);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/solutions/student/{studentId}/challenge/{challengeId} - Get student solution for challenge
    @GetMapping("/student/{studentId}/challenge/{challengeId}")
    public ResponseEntity<SolutionResource> getSolutionByStudentAndChallenge(
            @PathVariable String studentId,
            @PathVariable String challengeId) {
        try {
            var query = SolutionQueryFromParametersAssembler.toGetSolutionByStudentIdAndChallengeIdQuery(studentId, challengeId);
            var result = solutionQueryService.handle(query);
            
            if (result.isPresent()) {
                var solutionResource = SolutionResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(solutionResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
