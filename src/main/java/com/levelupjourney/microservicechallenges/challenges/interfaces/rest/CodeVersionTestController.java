package com.levelupjourney.microservicechallenges.challenges.interfaces.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/code-versions-tests", produces = MediaType.APPLICATION_JSON_VALUE)
public class CodeVersionTestController {
}
