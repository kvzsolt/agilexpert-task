package hu.agileexpert.smartos.controller;

import hu.agileexpert.smartos.dto.menuitem.MenuItemRequest;
import hu.agileexpert.smartos.dto.menuitem.MenuItemResponse;
import hu.agileexpert.smartos.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hu.agileexpert.smartos.controller.constants.Endpoints.MENU_ITEM_MAPPING;

@RestController
@RequestMapping(MENU_ITEM_MAPPING)
@AllArgsConstructor
@Slf4j
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse created = menuItemService.create(request);
        log.info("HTTP POST {} - MenuItem created with id: {}", MENU_ITEM_MAPPING, created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> findById(@PathVariable Long id) {
        MenuItemResponse response = menuItemService.findByIdResponse(id);
        log.info("HTTP GET {}/{} - MenuItem found", MENU_ITEM_MAPPING, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> findAll() {
        List<MenuItemResponse> responses = menuItemService.findAllResponses();
        log.info("HTTP GET {} - Returning {} menu items", MENU_ITEM_MAPPING, responses.size());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse updated = menuItemService.update(id, request);
        log.info("HTTP PUT {}/{} - MenuItem updated", MENU_ITEM_MAPPING, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuItemService.deleteById(id);
        log.info("HTTP DELETE {}/{} - MenuItem deleted", MENU_ITEM_MAPPING, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
