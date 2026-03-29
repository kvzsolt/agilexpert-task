package hu.agileexpert.smartos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.dto.menu.MenuResponse;
import hu.agileexpert.smartos.repository.MenuRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ModelMapper modelMapper;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, modelMapper);
    }

    @Test
    void findByIdResponseShouldReturnOnlyRootItemsWithNestedChildren() {
        Menu menu = Menu.builder()
                .id(1L)
                .uniqueIdentifier("menu-1")
                .name("Main menu")
                .build();

        MenuItem child = MenuItem.builder()
                .id(11L)
                .uniqueIdentifier("mi-child")
                .name("Child")
                .menu(menu)
                .build();

        MenuItem root = MenuItem.builder()
                .id(10L)
                .uniqueIdentifier("mi-root")
                .name("Root")
                .menu(menu)
                .children(List.of(child))
                .build();
        child.setParent(root);

        menu.setMenuItems(List.of(root, child));

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        MenuResponse response = menuService.findByIdResponse(1L);

        assertEquals(1, response.getMenuItems().size());
        assertEquals(10L, response.getMenuItems().getFirst().getId());
        assertEquals(1, response.getMenuItems().getFirst().getChildren().size());
        assertEquals(11L, response.getMenuItems().getFirst().getChildren().getFirst().getId());
        assertEquals(10L, response.getMenuItems().getFirst().getChildren().getFirst().getParentId());
    }
}
