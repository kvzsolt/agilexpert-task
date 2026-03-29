package hu.agileexpert.smartos.dto.auth;
import hu.agileexpert.smartos.service.interfaces.PasswordConfirmable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest implements PasswordConfirmable {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 6, message = "Username must be at least 6 characters long")
    @Pattern(regexp = "^\\S*$", message = "Username cannot contain whitespaces")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespaces")
    private String password;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespaces")
    private String passwordConfirm;

    @NotBlank
    @Size(max = 80)
    private String name;
}
