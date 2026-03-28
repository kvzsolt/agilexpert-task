package hu.agileexpert.smartos.dto.menuitem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class MenuItemRequest {
    @NotBlank
    @Size(max = 50)
    private String uniqueIdentifier;
    @NotBlank
    @Size(max = 80)
    private String name;
    @NotNull
    private Long menuId;
    private Long applicationId;
    private Long parentId;
}
