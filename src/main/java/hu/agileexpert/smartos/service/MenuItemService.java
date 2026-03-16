package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.exception.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.MenuItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuItemService {

	private final MenuItemRepository menuItemRepository;

	public MenuItemService(MenuItemRepository menuItemRepository) {
		this.menuItemRepository = menuItemRepository;
	}

	public MenuItem create(MenuItem menuItem) {
		return menuItemRepository.save(menuItem);
	}

	public MenuItem findById(Long id) {
		return menuItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
	}

	public List<MenuItem> findAll() {
		return menuItemRepository.findAll();
	}

	public MenuItem update(Long id, MenuItem menuItem) {
		MenuItem existing = findById(id);

		if (menuItem.getId() != null && !menuItem.getId().equals(id)) {
			throw new IdMismatchException("MenuItem", id, menuItem.getId());
		}

		existing.setExternalId(menuItem.getExternalId());
		existing.setName(menuItem.getName());
		existing.setMenu(menuItem.getMenu());
		existing.setApplication(menuItem.getApplication());

		return existing;
	}

	public void deleteById(Long id) {
		findById(id);
		menuItemRepository.deleteById(id);
	}
}

