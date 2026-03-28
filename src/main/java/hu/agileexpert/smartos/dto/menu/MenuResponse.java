package hu.agileexpert.smartos.dto.menu;
import hu.agileexpert.smartos.dto.menuitem.MenuItemResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    private Long id;
    private String uniqueIdentifier;
    private String name;
    private List<MenuItemResponse> menuItems;
}
