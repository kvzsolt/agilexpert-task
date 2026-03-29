package hu.agileexpert.smartos.dto.menuitem;

import hu.agileexpert.smartos.domain.MenuItem;

import java.util.List;

public final class MenuItemMapper {

    private MenuItemMapper() {
    }

    public static MenuItemResponse toFlatResponse(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .uniqueIdentifier(menuItem.getUniqueIdentifier())
                .name(menuItem.getName())
                .menuId(menuItem.getMenu() != null ? menuItem.getMenu().getId() : null)
                .parentId(menuItem.getParent() != null ? menuItem.getParent().getId() : null)
                .applicationId(menuItem.getApplication() != null ? menuItem.getApplication().getId() : null)
                .applicationName(menuItem.getApplication() != null ? menuItem.getApplication().getName() : null)
                .children(List.of())
                .build();
    }

    public static MenuItemResponse toTreeResponse(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .uniqueIdentifier(menuItem.getUniqueIdentifier())
                .name(menuItem.getName())
                .menuId(menuItem.getMenu() != null ? menuItem.getMenu().getId() : null)
                .parentId(menuItem.getParent() != null ? menuItem.getParent().getId() : null)
                .applicationId(menuItem.getApplication() != null ? menuItem.getApplication().getId() : null)
                .applicationName(menuItem.getApplication() != null ? menuItem.getApplication().getName() : null)
                .children(menuItem.getChildren().stream()
                        .map(MenuItemMapper::toTreeResponse)
                        .toList())
                .build();
    }
}
