package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.repository.MenuItemRepository;
import jakarta.persistence.EntityNotFoundException;
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
				.orElseThrow(() -> new EntityNotFoundException("MenuItem not found with id: " + id));
	}

	public List<MenuItem> findAll() {
		return menuItemRepository.findAll();
	}

	public MenuItem update(Long id, MenuItem menuItem) {
		findById(id);
		return menuItemRepository.save(menuItem);
	}

	public void deleteById(Long id) {
		findById(id);
		menuItemRepository.deleteById(id);
	}
}

