package com.levelupjourney.microservicechallenges.challenge.interfaces.rest;

import com.levelupjourney.microservicechallenges.challenge.domain.services.TestCommandService;
import com.levelupjourney.microservicechallenges.challenge.domain.services.TestQueryService;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.CreateTestResource;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.TestResource;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources.UpdateTestResource;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.TestCommandFromResourceAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.TestQueryFromParametersAssembler;
import com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform.TestResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Test aggregate
 * Handles test management within challenges
 */
@RestController
@RequestMapping("/api/v1/tests")
@CrossOrigin(origins = "*")
public class TestController {

    private final TestCommandService testCommandService;
    private final TestQueryService testQueryService;

    public TestController(TestCommandService testCommandService,
                         TestQueryService testQueryService) {
        this.testCommandService = testCommandService;
        this.testQueryService = testQueryService;
    }

    // POST /api/v1/tests - Create new test
    @PostMapping
    public ResponseEntity<TestResource> createTest(
            @RequestParam Long challengeVersionId,
            @RequestBody CreateTestResource resource) {
        try {
            var command = TestCommandFromResourceAssembler.toCommandFromResource(challengeVersionId, resource);
            var result = testCommandService.handle(command);
            
            if (result.isPresent()) {
                var testResource = TestResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(testResource, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/tests - Get tests by challenge ID
    @GetMapping
    public ResponseEntity<List<TestResource>> getTestsByChallengeId(@RequestParam String challengeId) {
        try {
            var query = TestQueryFromParametersAssembler.toGetTestsByChallengeIdQuery(challengeId);
            var tests = testQueryService.handle(query).stream()
                    .map(TestResourceFromEntityAssembler::toResourceFromEntity)
                    .collect(Collectors.toList());
            
            return new ResponseEntity<>(tests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/tests/{testId} - Get test by ID
    @GetMapping("/{testId}")
    public ResponseEntity<TestResource> getTestById(@PathVariable String testId) {
        try {
            var query = TestQueryFromParametersAssembler.toGetTestByIdQuery(testId);
            var result = testQueryService.handle(query);
            
            if (result.isPresent()) {
                var testResource = TestResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(testResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/v1/tests/{testId} - Update test
    @PutMapping("/{testId}")
    public ResponseEntity<TestResource> updateTest(
            @PathVariable String testId,
            @RequestBody UpdateTestResource resource) {
        try {
            var command = TestCommandFromResourceAssembler.toCommandFromResource(testId, resource);
            var result = testCommandService.handle(command);
            
            if (result.isPresent()) {
                var testResource = TestResourceFromEntityAssembler.toResourceFromEntity(result.get());
                return new ResponseEntity<>(testResource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/v1/tests/{testId} - Delete test
    @DeleteMapping("/{testId}")
    public ResponseEntity<Void> deleteTest(@PathVariable String testId) {
        try {
            var command = TestCommandFromResourceAssembler.toDeleteCommandFromResource(testId);
            testCommandService.handle(command);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
