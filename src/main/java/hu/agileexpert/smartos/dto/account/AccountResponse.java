package hu.agileexpert.smartos.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;
    private String uniqueIdentifier;
    private String name;
    private String username;
    private String themeName;
    private String wallpaperName;
}
