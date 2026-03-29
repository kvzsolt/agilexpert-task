package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.dto.menuitem.MenuItemMapper;
import hu.agileexpert.smartos.dto.menuitem.MenuItemRequest;
import hu.agileexpert.smartos.dto.menuitem.MenuItemResponse;
import hu.agileexpert.smartos.exception.account.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.MenuItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuItemService {

	private final MenuItemRepository menuItemRepository;
	private final MenuService menuService;
	private final ApplicationService applicationService;

	public MenuItemService(MenuItemRepository menuItemRepository,
						   MenuService menuService,
						   ApplicationService applicationService) {
		this.menuItemRepository = menuItemRepository;
		this.menuService = menuService;
		this.applicationService = applicationService;
	}

	public MenuItemResponse create(MenuItemRequest request) {
		Menu menu = menuService.findById(request.getMenuId());
		MenuItem parent = resolveParent(request.getParentId(), menu.getId(), null);
		Application application = resolveApplication(request.getApplicationId());

		MenuItem menuItem = MenuItem.builder()
				.uniqueIdentifier(request.getUniqueIdentifier())
				.name(request.getName())
				.menu(menu)
				.parent(parent)
				.application(application)
				.build();

		validateApplicationAndChildren(menuItem);
		MenuItem created = create(menuItem);
		return MenuItemMapper.toTreeResponse(created);
	}

	public MenuItemResponse findByIdResponse(Long id) {
		return MenuItemMapper.toTreeResponse(findById(id));
	}

	public List<MenuItemResponse> findAllResponses() {
		return findAll().stream()
				.map(MenuItemMapper::toFlatResponse)
				.toList();
	}

	public MenuItemResponse update(Long id, MenuItemRequest request) {
		MenuItem updated = update(id, requestToEntity(id, request));
		return MenuItemMapper.toTreeResponse(updated);
	}

	public MenuItem create(MenuItem menuItem) {
		if (menuItem.getParent() != null) {
			MenuItem parent = resolveParent(menuItem.getParent().getId(), menuItem.getMenu().getId(), null);
			menuItem.setParent(parent);
		}
		validateApplicationAndChildren(menuItem);
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

		existing.setUniqueIdentifier(menuItem.getUniqueIdentifier());
		existing.setName(menuItem.getName());
		existing.setApplication(menuItem.getApplication());
		existing.setParent(menuItem.getParent());
		updateMenuRecursively(existing, menuItem.getMenu());
		validateApplicationAndChildren(existing);

		return existing;
	}

	public void deleteById(Long id) {
		findById(id);
		menuItemRepository.deleteById(id);
	}

	private MenuItem requestToEntity(Long id, MenuItemRequest request) {
		Menu menu = menuService.findById(request.getMenuId());
		MenuItem parent = resolveParent(request.getParentId(), menu.getId(), id);
		Application application = resolveApplication(request.getApplicationId());

		return MenuItem.builder()
				.id(id)
				.uniqueIdentifier(request.getUniqueIdentifier())
				.name(request.getName())
				.menu(menu)
				.parent(parent)
				.application(application)
				.build();
	}

	private Application resolveApplication(Long applicationId) {
		if (applicationId == null) {
			return null;
		}
		return applicationService.findById(applicationId);
	}

	private MenuItem resolveParent(Long parentId, Long menuId, Long currentItemId) {
		if (parentId == null) {
			return null;
		}

		MenuItem parent = findById(parentId);
		if (!parent.getMenu().getId().equals(menuId)) {
			throw new IllegalArgumentException("Parent menu item must belong to the same menu.");
		}
		if (parent.getApplication() != null) {
			throw new IllegalArgumentException("A submenu item cannot be attached under an item that launches an application.");
		}
		if (currentItemId != null && parent.getId().equals(currentItemId)) {
			throw new IllegalArgumentException("Menu item cannot be its own parent.");
		}
		if (currentItemId != null && isAncestor(parent, currentItemId)) {
			throw new IllegalArgumentException("Menu item cannot be moved under its own descendant.");
		}
		return parent;
	}

	private boolean isAncestor(MenuItem candidateParent, Long currentItemId) {
		MenuItem current = candidateParent;
		while (current != null) {
			if (current.getId().equals(currentItemId)) {
				return true;
			}
			current = current.getParent();
		}
		return false;
	}

	private void updateMenuRecursively(MenuItem menuItem, Menu menu) {
		menuItem.setMenu(menu);
		for (MenuItem child : menuItem.getChildren()) {
			updateMenuRecursively(child, menu);
		}
	}

	private void validateApplicationAndChildren(MenuItem menuItem) {
		if (menuItem.getApplication() != null && !menuItem.getChildren().isEmpty()) {
			throw new IllegalArgumentException("A menu item cannot both launch an application and contain submenu items.");
		}
	}
}
