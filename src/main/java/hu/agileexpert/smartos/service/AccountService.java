package hu.agileexpert.smartos.service;

import hu.agileexpert.smartos.domain.Account;
import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.domain.Theme;
import hu.agileexpert.smartos.domain.Wallpaper;
import hu.agileexpert.smartos.dto.account.AccountRequest;
import hu.agileexpert.smartos.dto.account.AccountResponse;
import hu.agileexpert.smartos.dto.account.UserInfo;
import hu.agileexpert.smartos.dto.auth.RegisterRequest;
import hu.agileexpert.smartos.dto.auth.RegistrationInfo;
import hu.agileexpert.smartos.exception.account.IdMismatchException;
import hu.agileexpert.smartos.exception.ResourceNotFoundException;
import hu.agileexpert.smartos.exception.account.FieldNotAvailableException;
import hu.agileexpert.smartos.exception.account.PasswordMismatchException;
import hu.agileexpert.smartos.repository.AccountRepository;
import hu.agileexpert.smartos.repository.ApplicationRepository;
import hu.agileexpert.smartos.repository.ThemeRepository;
import hu.agileexpert.smartos.repository.WallpaperRepository;

import java.util.List;
import java.util.UUID;
import hu.agileexpert.smartos.service.interfaces.PasswordConfirmable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ThemeRepository themeRepository;
    private final WallpaperRepository wallpaperRepository;
    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegistrationInfo registerAccount(RegisterRequest data) {
        confirmPassword(data);
        checkFieldAvailability(data);
        Account account = modelMapper.map(data, Account.class);
        account.setUniqueIdentifier(generateAccountUniqueIdentifier());
        account.setPassword(passwordEncoder.encode(data.getPassword()));

        accountRepository.save(account);
        return new RegistrationInfo("Successful registration.");
    }

    public UserInfo getUserInfo(String username) {
        Account userAccount = findByUsername(username);
        return modelMapper.map(userAccount, UserInfo.class);
    }

    public AccountResponse findByIdResponse(Long id) {
        return toResponse(findById(id));
    }

    public AccountResponse findByUsernameResponse(String username) {
        return toResponse(findByUsername(username));
    }

    public List<AccountResponse> findAllResponses() {
        return findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse update(Long id, AccountRequest request) {
        Account account = modelMapper.map(request, Account.class);
        Account updated = update(id, account);
        return toResponse(updated);
    }

    public AccountResponse setThemeResponse(Long accountId, Long themeId) {
        return toResponse(setTheme(accountId, themeId));
    }

    public AccountResponse setWallpaperResponse(Long accountId, Long wallpaperId) {
        return toResponse(setWallpaper(accountId, wallpaperId));
    }

    public AccountResponse installApplicationResponse(Long accountId, Long applicationId) {
        return toResponse(installApplication(accountId, applicationId));
    }

    public AccountResponse removeApplicationResponse(Long accountId, Long applicationId) {
        return toResponse(removeApplication(accountId, applicationId));
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id));
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "username", username));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    public Account update(Long id, Account account) {
        Account existing = findById(id);

        if (account.getId() != null && !account.getId().equals(id)) {
            throw new IdMismatchException("Account", id, account.getId());
        }

        existing.setUniqueIdentifier(account.getUniqueIdentifier());
        existing.setName(account.getName());

        return existing;
    }

    public void deleteById(Long id) {
        findById(id);
        accountRepository.deleteById(id);
    }

    public Account setTheme(Long accountId, Long themeId) {
        Account account = findById(accountId);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", themeId));
        account.setTheme(theme);
        return account;
    }

    public Account setWallpaper(Long accountId, Long wallpaperId) {
        Account account = findById(accountId);
        Wallpaper wallpaper = wallpaperRepository.findById(wallpaperId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallpaper", wallpaperId));
        account.setWallpaper(wallpaper);
        return account;
    }

    public Account installApplication(Long accountId, Long applicationId) {
        Account account = findById(accountId);
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        if (account.getApplications().stream().noneMatch(a -> a.getId().equals(applicationId))) {
            account.getApplications().add(application);
        }
        return account;
    }

    public Account removeApplication(Long accountId, Long applicationId) {
        Account account = findById(accountId);
        account.getApplications().removeIf(app -> app.getId().equals(applicationId));
        return account;
    }

    public String launchApplication(Long accountId, Long applicationId) {
        Account account = findById(accountId);
        boolean installed = account.getApplications().stream()
                .anyMatch(app -> app.getId().equals(applicationId));
        if (!installed) {
            throw new IllegalArgumentException(
                    "Application " + applicationId + " is not installed for account " + accountId);
        }
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        return application.getName() + " elindítva (" + account.getName() + ")";
    }

    private <T extends PasswordConfirmable> void confirmPassword(T data) {
        if (!data.getPassword().equals(data.getPasswordConfirm())) {
            throw new PasswordMismatchException();
        }
    }

    private void checkFieldAvailability(RegisterRequest data) {
        if (accountRepository.existsByUsername(data.getUsername())) {
            throw new FieldNotAvailableException("Account with " + data.getUsername() + " already exist.");
        }
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .uniqueIdentifier(account.getUniqueIdentifier())
                .name(account.getName())
                .username(account.getUsername())
                .themeName(account.getTheme() != null ? account.getTheme().getName() : null)
                .wallpaperName(account.getWallpaper() != null ? account.getWallpaper().getName() : null)
                .build();
    }

    public String generateAccountUniqueIdentifier() {
        String uniqueIdentifier;
        do {
            uniqueIdentifier = "acc-" + UUID.randomUUID();
        } while (accountRepository.existsByUniqueIdentifier(uniqueIdentifier));
        return uniqueIdentifier;
    }
}
