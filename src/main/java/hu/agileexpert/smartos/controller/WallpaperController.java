package hu.agileexpert.smartos.controller;

import hu.agileexpert.smartos.dto.wallpaper.WallpaperRequest;
import hu.agileexpert.smartos.dto.wallpaper.WallpaperResponse;
import hu.agileexpert.smartos.service.WallpaperService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hu.agileexpert.smartos.controller.constants.Endpoints.WALLPAPER_MAPPING;

@RestController
@RequestMapping(WALLPAPER_MAPPING)
@AllArgsConstructor
@Slf4j
public class WallpaperController {

    private final WallpaperService wallpaperService;

    @PostMapping
    public ResponseEntity<WallpaperResponse> create(@Valid @RequestBody WallpaperRequest request) {
        WallpaperResponse created = wallpaperService.create(request);
        log.info("HTTP POST {} - Wallpaper created with id: {}", WALLPAPER_MAPPING, created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WallpaperResponse> findById(@PathVariable Long id) {
        WallpaperResponse response = wallpaperService.findByIdResponse(id);
        log.info("HTTP GET {}/{} - Wallpaper found", WALLPAPER_MAPPING, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<WallpaperResponse>> findAll() {
        List<WallpaperResponse> responses = wallpaperService.findAllResponses();
        log.info("HTTP GET {} - Returning {} wallpapers", WALLPAPER_MAPPING, responses.size());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WallpaperResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody WallpaperRequest request) {
        WallpaperResponse updated = wallpaperService.update(id, request);
        log.info("HTTP PUT {}/{} - Wallpaper updated", WALLPAPER_MAPPING, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wallpaperService.deleteById(id);
        log.info("HTTP DELETE {}/{} - Wallpaper deleted", WALLPAPER_MAPPING, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
