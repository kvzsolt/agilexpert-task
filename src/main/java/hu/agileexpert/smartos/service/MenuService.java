package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.dto.menu.MenuRequest;
import hu.agileexpert.smartos.dto.menu.MenuResponse;
import hu.agileexpert.smartos.dto.menuitem.MenuItemResponse;
import hu.agileexpert.smartos.exception.account.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.MenuRepository;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuService {

	private final MenuRepository menuRepository;
	private final ModelMapper modelMapper;

	public MenuService(MenuRepository menuRepository, ModelMapper modelMapper) {
		this.menuRepository = menuRepository;
		this.modelMapper = modelMapper;
	}

	public MenuResponse create(MenuRequest request) {
		Menu menu = modelMapper.map(request, Menu.class);
		Menu created = create(menu);
		return toResponse(created);
	}

	public MenuResponse findByIdResponse(Long id) {
		return toResponse(findById(id));
	}

	public List<MenuResponse> findAllResponses() {
		return findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	public MenuResponse update(Long id, MenuRequest request) {
		Menu menu = modelMapper.map(request, Menu.class);
		Menu updated = update(id, menu);
		return toResponse(updated);
	}

	public Menu create(Menu menu) {
		return menuRepository.save(menu);
	}

    public Menu findById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu", id));
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Menu update(Long id, Menu menu) {
        Menu existing = findById(id);

        if (menu.getId() != null && !menu.getId().equals(id)) {
            throw new IdMismatchException("Menu", id, menu.getId());
        }

        existing.setUniqueIdentifier(menu.getUniqueIdentifier());
        existing.setName(menu.getName());

        return existing;
    }

    public void deleteById(Long id) {
        findById(id);
        menuRepository.deleteById(id);
    }

	private MenuResponse toResponse(Menu menu) {
		return MenuResponse.builder()
				.id(menu.getId())
				.uniqueIdentifier(menu.getUniqueIdentifier())
				.name(menu.getName())
				.menuItems(menu.getMenuItems().stream()
						.filter(menuItem -> menuItem.getParent() == null)
						.map(this::toTreeResponse)
						.toList())
				.build();
	}

	private MenuItemResponse toTreeResponse(MenuItem menuItem) {
		return MenuItemResponse.builder()
				.id(menuItem.getId())
				.uniqueIdentifier(menuItem.getUniqueIdentifier())
				.name(menuItem.getName())
				.menuId(menuItem.getMenu() != null ? menuItem.getMenu().getId() : null)
				.parentId(menuItem.getParent() != null ? menuItem.getParent().getId() : null)
				.applicationId(menuItem.getApplication() != null ? menuItem.getApplication().getId() : null)
				.applicationName(menuItem.getApplication() != null ? menuItem.getApplication().getName() : null)
				.children(menuItem.getChildren().stream()
						.map(this::toTreeResponse)
						.toList())
				.build();
	}
}
