package hu.agileexpert.smartos.dto.application;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ApplicationRequest {
    @NotBlank
    @Size(max = 50)
    private String uniqueIdentifier;
    @NotBlank
    @Size(max = 80)
    private String name;
}
