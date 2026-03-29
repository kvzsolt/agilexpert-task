package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Wallpaper;
import hu.agileexpert.smartos.dto.wallpaper.WallpaperRequest;
import hu.agileexpert.smartos.dto.wallpaper.WallpaperResponse;
import hu.agileexpert.smartos.exception.account.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.WallpaperRepository;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WallpaperService {

	private final WallpaperRepository wallpaperRepository;
	private final ModelMapper modelMapper;

	public WallpaperService(WallpaperRepository wallpaperRepository, ModelMapper modelMapper) {
		this.wallpaperRepository = wallpaperRepository;
		this.modelMapper = modelMapper;
	}

	public WallpaperResponse create(WallpaperRequest request) {
		Wallpaper wallpaper = modelMapper.map(request, Wallpaper.class);
		Wallpaper created = create(wallpaper);
		return modelMapper.map(created, WallpaperResponse.class);
	}

	public WallpaperResponse findByIdResponse(Long id) {
		return modelMapper.map(findById(id), WallpaperResponse.class);
	}

	public List<WallpaperResponse> findAllResponses() {
		return findAll().stream()
				.map(wallpaper -> modelMapper.map(wallpaper, WallpaperResponse.class))
				.toList();
	}

	public WallpaperResponse update(Long id, WallpaperRequest request) {
		Wallpaper wallpaper = modelMapper.map(request, Wallpaper.class);
		Wallpaper updated = update(id, wallpaper);
		return modelMapper.map(updated, WallpaperResponse.class);
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

		existing.setUniqueIdentifier(wallpaper.getUniqueIdentifier());
		existing.setName(wallpaper.getName());

		return existing;
	}

	public void deleteById(Long id) {
		findById(id);
		wallpaperRepository.deleteById(id);
	}
}
