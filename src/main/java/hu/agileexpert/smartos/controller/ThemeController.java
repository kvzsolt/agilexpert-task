package hu.agileexpert.smartos.controller;

import hu.agileexpert.smartos.dto.theme.ThemeRequest;
import hu.agileexpert.smartos.dto.theme.ThemeResponse;
import hu.agileexpert.smartos.service.ThemeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hu.agileexpert.smartos.controller.constants.Endpoints.THEME_MAPPING;

@RestController
@RequestMapping(THEME_MAPPING)
@AllArgsConstructor
@Slf4j
public class ThemeController {

    private final ThemeService themeService;

    @PostMapping
    public ResponseEntity<ThemeResponse> create(@Valid @RequestBody ThemeRequest request) {
        ThemeResponse created = themeService.create(request);
        log.info("HTTP POST {} - Theme created with id: {}", THEME_MAPPING, created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThemeResponse> findById(@PathVariable Long id) {
        ThemeResponse response = themeService.findByIdResponse(id);
        log.info("HTTP GET {}/{} - Theme found", THEME_MAPPING, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        List<ThemeResponse> responses = themeService.findAllResponses();
        log.info("HTTP GET {} - Returning {} themes", THEME_MAPPING, responses.size());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThemeResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody ThemeRequest request) {
        ThemeResponse updated = themeService.update(id, request);
        log.info("HTTP PUT {}/{} - Theme updated", THEME_MAPPING, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        themeService.deleteById(id);
        log.info("HTTP DELETE {}/{} - Theme deleted", THEME_MAPPING, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
