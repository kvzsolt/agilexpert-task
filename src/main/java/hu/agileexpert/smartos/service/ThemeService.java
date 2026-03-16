package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Theme;
import hu.agileexpert.smartos.exception.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.ThemeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
				.orElseThrow(() -> new ResourceNotFoundException("Theme", id));
	}

	public List<Theme> findAll() {
		return themeRepository.findAll();
	}

	public Theme update(Long id, Theme theme) {
		Theme existing = findById(id);

		if (theme.getId() != null && !theme.getId().equals(id)) {
			throw new IdMismatchException("Theme", id, theme.getId());
		}

		existing.setExternalId(theme.getExternalId());
		existing.setName(theme.getName());

		return existing;
	}

	public void deleteById(Long id) {
		findById(id);
		themeRepository.deleteById(id);
	}
}

