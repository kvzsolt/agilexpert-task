package hu.agileexpert.smartos.controller;

import hu.agileexpert.smartos.dto.application.ApplicationRequest;
import hu.agileexpert.smartos.dto.application.ApplicationResponse;
import hu.agileexpert.smartos.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hu.agileexpert.smartos.controller.constants.Endpoints.APPLICATION_MAPPING;

@RestController
@RequestMapping(APPLICATION_MAPPING)
@AllArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse created = applicationService.create(request);
        log.info("HTTP POST {} - Application created with id: {}", APPLICATION_MAPPING, created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> findById(@PathVariable Long id) {
        ApplicationResponse response = applicationService.findByIdResponse(id);
        log.info("HTTP GET {}/{} - Application found", APPLICATION_MAPPING, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> findAll() {
        List<ApplicationResponse> responses = applicationService.findAllResponses();
        log.info("HTTP GET {} - Returning {} applications", APPLICATION_MAPPING, responses.size());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse updated = applicationService.update(id, request);
        log.info("HTTP PUT {}/{} - Application updated", APPLICATION_MAPPING, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.deleteById(id);
        log.info("HTTP DELETE {}/{} - Application deleted", APPLICATION_MAPPING, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
