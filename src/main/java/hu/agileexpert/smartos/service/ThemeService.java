package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Theme;
import hu.agileexpert.smartos.repository.ThemeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {
//TODO: Implement model mapper after dtos implemented.

	private final ThemeRepository themeRepository;

	public ThemeService(ThemeRepository themeRepository) {
		this.themeRepository = themeRepository;
	}

	public Theme create(Theme theme) {
		return themeRepository.save(theme);
	}

	public Theme findById(Long id) {
		return themeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Theme not found with id: " + id));
	}

	public List<Theme> findAll() {
		return themeRepository.findAll();
	}

	public Theme update(Long id, Theme theme) {
		findById(id);
		return themeRepository.save(theme);
	}

	public void deleteById(Long id) {
		findById(id);
		themeRepository.deleteById(id);
	}
}

