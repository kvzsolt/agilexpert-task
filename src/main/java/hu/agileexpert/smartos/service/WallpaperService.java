package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Wallpaper;
import hu.agileexpert.smartos.exception.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.WallpaperRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
				.orElseThrow(() -> new ResourceNotFoundException("Wallpaper", id));
	}

	public List<Wallpaper> findAll() {
		return wallpaperRepository.findAll();
	}

	public Wallpaper update(Long id, Wallpaper wallpaper) {
		Wallpaper existing = findById(id);

		if (wallpaper.getId() != null && !wallpaper.getId().equals(id)) {
			throw new IdMismatchException("Wallpaper", id, wallpaper.getId());
		}

		existing.setExternalId(wallpaper.getExternalId());
		existing.setName(wallpaper.getName());

		return existing;
	}

	public void deleteById(Long id) {
		findById(id);
		wallpaperRepository.deleteById(id);
	}
}

