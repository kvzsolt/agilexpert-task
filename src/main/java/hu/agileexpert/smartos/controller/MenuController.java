package hu.agileexpert.smartos.controller;

import hu.agileexpert.smartos.dto.menu.MenuRequest;
import hu.agileexpert.smartos.dto.menu.MenuResponse;
import hu.agileexpert.smartos.service.MenuService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hu.agileexpert.smartos.controller.constants.Endpoints.MENU_MAPPING;

@RestController
@RequestMapping(MENU_MAPPING)
@AllArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuResponse> create(@Valid @RequestBody MenuRequest request) {
        MenuResponse created = menuService.create(request);
        log.info("HTTP POST {} - Menu created with id: {}", MENU_MAPPING, created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponse> findById(@PathVariable Long id) {
        MenuResponse response = menuService.findByIdResponse(id);
        log.info("HTTP GET {}/{} - Menu found", MENU_MAPPING, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> findAll() {
        List<MenuResponse> responses = menuService.findAllResponses();
        log.info("HTTP GET {} - Returning {} menus", MENU_MAPPING, responses.size());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody MenuRequest request) {
        MenuResponse updated = menuService.update(id, request);
        log.info("HTTP PUT {}/{} - Menu updated", MENU_MAPPING, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.deleteById(id);
        log.info("HTTP DELETE {}/{} - Menu deleted", MENU_MAPPING, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
