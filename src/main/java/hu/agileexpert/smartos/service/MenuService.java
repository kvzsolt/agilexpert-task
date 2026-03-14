package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
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
                .orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + id));
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Menu update(Long id, Menu menu) {
        findById(id);
        return menuRepository.save(menu);
    }

    public void deleteById(Long id) {
        findById(id);
        menuRepository.deleteById(id);
    }
}

