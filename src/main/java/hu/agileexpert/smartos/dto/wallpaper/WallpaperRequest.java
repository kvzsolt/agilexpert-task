package hu.agileexpert.smartos.dto.wallpaper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class WallpaperRequest {
    @NotBlank
    @Size(max = 100)
    private String uniqueIdentifier;
    @NotBlank
    @Size(max = 120)
    private String name;
}
