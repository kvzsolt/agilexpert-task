package hu.agileexpert.smartos.dto.menuitem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private Long id;
    private String uniqueIdentifier;
    private String name;
    private Long menuId;
    private Long parentId;
    private Long applicationId;
    private String applicationName;
    private List<MenuItemResponse> children;
}
