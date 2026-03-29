package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Theme;
import hu.agileexpert.smartos.dto.theme.ThemeRequest;
import hu.agileexpert.smartos.dto.theme.ThemeResponse;
import hu.agileexpert.smartos.exception.account.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.ThemeRepository;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ThemeService {

	private final ThemeRepository themeRepository;
	private final ModelMapper modelMapper;

	public ThemeService(ThemeRepository themeRepository, ModelMapper modelMapper) {
		this.themeRepository = themeRepository;
		this.modelMapper = modelMapper;
	}

	public ThemeResponse create(ThemeRequest request) {
		Theme theme = modelMapper.map(request, Theme.class);
		Theme created = create(theme);
		return modelMapper.map(created, ThemeResponse.class);
	}

	public ThemeResponse findByIdResponse(Long id) {
		return modelMapper.map(findById(id), ThemeResponse.class);
	}

	public List<ThemeResponse> findAllResponses() {
		return findAll().stream()
				.map(theme -> modelMapper.map(theme, ThemeResponse.class))
				.toList();
	}

	public ThemeResponse update(Long id, ThemeRequest request) {
		Theme theme = modelMapper.map(request, Theme.class);
		Theme updated = update(id, theme);
		return modelMapper.map(updated, ThemeResponse.class);
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

		existing.setUniqueIdentifier(theme.getUniqueIdentifier());
		existing.setName(theme.getName());

		return existing;
	}

	public void deleteById(Long id) {
		findById(id);
		themeRepository.deleteById(id);
	}
}
