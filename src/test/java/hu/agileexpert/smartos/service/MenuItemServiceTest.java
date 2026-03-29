package hu.agileexpert.smartos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.dto.menuitem.MenuItemRequest;
import hu.agileexpert.smartos.dto.menuitem.MenuItemResponse;
import hu.agileexpert.smartos.repository.MenuItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuService menuService;

    @Mock
    private ApplicationService applicationService;

    private MenuItemService menuItemService;

    @BeforeEach
    void setUp() {
        menuItemService = new MenuItemService(menuItemRepository, menuService, applicationService);
    }

    @Test
    void updateShouldChangeRelationshipsAndReturnUpdatedResponse() {
        Menu originalMenu = Menu.builder().id(1L).uniqueIdentifier("menu-1").name("Original").build();
        Menu targetMenu = Menu.builder().id(2L).uniqueIdentifier("menu-2").name("Target").build();
        Application targetApplication = Application.builder().id(20L).uniqueIdentifier("app-20").name("Paint").build();
        MenuItem parent = MenuItem.builder()
                .id(10L)
                .uniqueIdentifier("mi-parent")
                .name("Parent")
                .menu(targetMenu)
                .build();
        MenuItem existing = MenuItem.builder()
                .id(5L)
                .uniqueIdentifier("mi-old")
                .name("Old")
                .menu(originalMenu)
                .build();

        MenuItemRequest request = new MenuItemRequest();
        request.setUniqueIdentifier("mi-new");
        request.setName("New");
        request.setMenuId(2L);
        request.setApplicationId(20L);
        request.setParentId(10L);

        when(menuItemRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(parent));
        when(menuService.findById(2L)).thenReturn(targetMenu);
        when(applicationService.findById(20L)).thenReturn(targetApplication);

        MenuItemResponse response = menuItemService.update(5L, request);

        assertEquals("mi-new", existing.getUniqueIdentifier());
        assertEquals("New", existing.getName());
        assertSame(targetMenu, existing.getMenu());
        assertSame(targetApplication, existing.getApplication());
        assertSame(parent, existing.getParent());
        assertEquals(10L, response.getParentId());
        assertEquals(20L, response.getApplicationId());
    }

    @Test
    void createShouldRejectParentThatLaunchesApplication() {
        Menu menu = Menu.builder().id(2L).uniqueIdentifier("menu-2").name("Target").build();
        Application parentApplication = Application.builder().id(21L).uniqueIdentifier("app-21").name("OpenMap").build();
        MenuItem parent = MenuItem.builder()
                .id(10L)
                .uniqueIdentifier("mi-parent")
                .name("Parent")
                .menu(menu)
                .application(parentApplication)
                .build();

        MenuItemRequest request = new MenuItemRequest();
        request.setUniqueIdentifier("mi-child");
        request.setName("Child");
        request.setMenuId(2L);
        request.setParentId(10L);

        when(menuService.findById(2L)).thenReturn(menu);
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(parent));

        assertThrows(IllegalArgumentException.class, () -> menuItemService.create(request));
    }
}
