package hu.agileexpert.smartos.controller;

import hu.agileexpert.smartos.dto.account.AccountRequest;
import hu.agileexpert.smartos.dto.account.AccountResponse;
import hu.agileexpert.smartos.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static hu.agileexpert.smartos.controller.constants.Endpoints.ACCOUNT_MAPPING;

@RestController
@RequestMapping(ACCOUNT_MAPPING)
@AllArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        AccountResponse response = accountService.findByIdResponse(id);
        log.info("HTTP GET {}/{} - Account found", ACCOUNT_MAPPING, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<AccountResponse> responses = accountService.findAllResponses();
        log.info("HTTP GET {} - Returning {} accounts", ACCOUNT_MAPPING, responses.size());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.update(id, request);
        log.info("HTTP PUT {}/{} - Account updated", ACCOUNT_MAPPING, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.deleteById(id);
        log.info("HTTP DELETE {}/{} - Account deleted", ACCOUNT_MAPPING, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/theme/{themeId}")
    public ResponseEntity<AccountResponse> setTheme(@PathVariable Long id, @PathVariable Long themeId) {
        AccountResponse response = accountService.setThemeResponse(id, themeId);
        log.info("HTTP PUT {}/{}/theme/{} - Account theme updated", ACCOUNT_MAPPING, id, themeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/wallpaper/{wallpaperId}")
    public ResponseEntity<AccountResponse> setWallpaper(@PathVariable Long id, @PathVariable Long wallpaperId) {
        AccountResponse response = accountService.setWallpaperResponse(id, wallpaperId);
        log.info("HTTP PUT {}/{}/wallpaper/{} - Account wallpaper updated", ACCOUNT_MAPPING, id, wallpaperId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/applications/{appId}")
    public ResponseEntity<AccountResponse> installApplication(@PathVariable Long id, @PathVariable Long appId) {
        AccountResponse response = accountService.installApplicationResponse(id, appId);
        log.info("HTTP POST {}/{}/applications/{} - Application installed", ACCOUNT_MAPPING, id, appId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/applications/{appId}")
    public ResponseEntity<AccountResponse> removeApplication(@PathVariable Long id, @PathVariable Long appId) {
        AccountResponse response = accountService.removeApplicationResponse(id, appId);
        log.info("HTTP DELETE {}/{}/applications/{} - Application removed", ACCOUNT_MAPPING, id, appId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/launch/{appId}")
    public ResponseEntity<Map<String, String>> launchApplication(@PathVariable Long id, @PathVariable Long appId) {
        String result = accountService.launchApplication(id, appId);
        Map<String, String> response = Map.of("message", result);
        log.info("HTTP POST {}/{}/launch/{} - Application launched", ACCOUNT_MAPPING, id, appId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
