package hu.agileexpert.smartos.dto.menu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class MenuRequest {
    @NotBlank
    @Size(max = 50)
    private String uniqueIdentifier;
    @NotBlank
    @Size(max = 80)
    private String name;
}
