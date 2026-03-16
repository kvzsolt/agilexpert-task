package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.exception.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.repository.MenuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
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

        existing.setExternalId(menu.getExternalId());
        existing.setName(menu.getName());
        existing.setMenuItems(menu.getMenuItems());

        return existing;
    }

    public void deleteById(Long id) {
        findById(id);
        menuRepository.deleteById(id);
    }
}

