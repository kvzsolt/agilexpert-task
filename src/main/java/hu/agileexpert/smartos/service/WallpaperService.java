package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Wallpaper;
import hu.agileexpert.smartos.repository.WallpaperRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WallpaperService {
//TODO: Implement model mapper after dtos implemented.

	private final WallpaperRepository wallpaperRepository;

	public WallpaperService(WallpaperRepository wallpaperRepository) {
		this.wallpaperRepository = wallpaperRepository;
	}

	public Wallpaper create(Wallpaper wallpaper) {
		return wallpaperRepository.save(wallpaper);
	}

	public Wallpaper findById(Long id) {
		return wallpaperRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Wallpaper not found with id: " + id));
	}

	public List<Wallpaper> findAll() {
		return wallpaperRepository.findAll();
	}

	public Wallpaper update(Long id, Wallpaper wallpaper) {
		findById(id);
		return wallpaperRepository.save(wallpaper);
	}

	public void deleteById(Long id) {
		findById(id);
		wallpaperRepository.deleteById(id);
	}
}

