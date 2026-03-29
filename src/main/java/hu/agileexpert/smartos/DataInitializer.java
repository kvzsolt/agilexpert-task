package hu.agileexpert.smartos;

import hu.agileexpert.smartos.config.SmartOsBootstrapProperties;
import hu.agileexpert.smartos.domain.Account;
import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.domain.Theme;
import hu.agileexpert.smartos.domain.Wallpaper;
import hu.agileexpert.smartos.repository.AccountRepository;
import hu.agileexpert.smartos.service.AccountService;
import hu.agileexpert.smartos.service.ApplicationService;
import hu.agileexpert.smartos.service.MenuItemService;
import hu.agileexpert.smartos.service.MenuService;
import hu.agileexpert.smartos.service.ThemeService;
import hu.agileexpert.smartos.service.WallpaperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements SmartInitializingSingleton {

    private final ApplicationService applicationService;
    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final ThemeService themeService;
    private final WallpaperService wallpaperService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SmartOsBootstrapProperties bootstrapProperties;

    @Override
    @Transactional
    public void afterSingletonsInstantiated() {
        log.info("DataInitializer started, enabled={}, count={}", bootstrapProperties.isEnabled(), accountRepository.count());
        if (!bootstrapProperties.isEnabled() || accountRepository.count() > 0) {
            return;
        }

        log.info("========================================");
        log.info("  SmartOS — Inicializalas");
        log.info("========================================");

        Map<String, Application> applications = createSampleApplications();
        Map<String, Theme> themes = createThemes();
        Map<String, Wallpaper> wallpapers = createWallpapers();

        Theme defaultTheme = themes.get("theme-alap");
        Theme darkTheme = themes.get("theme-sotet");
        Theme lightTheme = themes.get("theme-vilagos");
        Wallpaper defaultWallpaper = wallpapers.get("wp-alap");
        Wallpaper natureWallpaper = wallpapers.get("wp-termeszet");
        Wallpaper abstractWallpaper = wallpapers.get("wp-absztrakt");

        // --- Apa (elso felhasznalo, o hozza letre a tobbi fiokot) ---
        Account fatherAccount = createFamilyMember(
                "acc-apa", "Kovacs Janos", "apa", "changeit123",
                "menu-apa", "Apa menuje"
        );
        installAppForAccount(fatherAccount, applications.get("OpenMap"), "mi-apa-openmap", "OpenMap");
        accountService.setTheme(fatherAccount.getId(), darkTheme.getId());
        accountService.setWallpaper(fatherAccount.getId(), natureWallpaper.getId());

        // --- Anya ---
        Account motherAccount = createFamilyMember(
                "acc-anya", "Kovacs Maria", "anya", "changeit123",
                "menu-anya", "Anya menuje"
        );
        installAppForAccount(motherAccount, applications.get("Receptek"), "mi-anya-receptek", "Receptek");
        accountService.setTheme(motherAccount.getId(), lightTheme.getId());
        accountService.setWallpaper(motherAccount.getId(), abstractWallpaper.getId());

        // --- Gyerek 1 ---
        Account sonAccount = createFamilyMember(
                "acc-gyerek1", "Kovacs Peter", "peter", "changeit123",
                "menu-gyerek1", "Peter menuje"
        );
        installAppForAccount(sonAccount, applications.get("Doodle Jump"), "mi-peter-doodlejump", "Doodle Jump");
        accountService.setTheme(sonAccount.getId(), defaultTheme.getId());
        accountService.setWallpaper(sonAccount.getId(), defaultWallpaper.getId());

        // --- Gyerek 2 ---
        Account daughterAccount = createFamilyMember(
                "acc-gyerek2", "Kovacs Anna", "anna", "changeit123",
                "menu-gyerek2", "Anna menuje"
        );
        installAppForAccount(daughterAccount, applications.get("Aknakereso"), "mi-anna-aknakereso", "Aknakereso");
        accountService.setTheme(daughterAccount.getId(), defaultTheme.getId());
        accountService.setWallpaper(daughterAccount.getId(), natureWallpaper.getId());

        log.info("[ALKALMAZASOK] {}", String.join(", ", applications.keySet()));
        log.info("[TEMAK]        Alapertelmezett, Sotet, Vilagos");
        log.info("[HATTERKÉPEK]  Alapertelmezett, Termeszet, Absztrakt");
        printAccount(fatherAccount, "OpenMap (GPS)");
        printAccount(motherAccount, "Receptek");
        printAccount(sonAccount, "Doodle Jump");
        printAccount(daughterAccount, "Aknakereso");
        log.info("Inicializalas kesz. {} felhasznalo letrehozva.", accountRepository.count());
        log.info("========================================");
    }

    // ---- Alkalmazasok ----

    private Map<String, Application> createSampleApplications() {
        Map<String, Application> applications = new LinkedHashMap<>();
        applications.put("Aknakereso", createApp("app-aknakereso", "Aknakereso"));
        applications.put("OpenMap", createApp("app-openmap", "OpenMap"));
        applications.put("Paint", createApp("app-paint", "Paint"));
        applications.put("Cimtar", createApp("app-cimtar", "Cimtar"));
        applications.put("Receptek", createApp("app-receptek", "Receptek"));
        applications.put("Doodle Jump", createApp("app-doodlejump", "Doodle Jump"));
        return applications;
    }

    // ---- Temak ----

    private Map<String, Theme> createThemes() {
        Map<String, Theme> themes = new LinkedHashMap<>();
        themes.put("theme-alap", createTheme("theme-alap", "Alapertelmezett"));
        themes.put("theme-sotet", createTheme("theme-sotet", "Sotet"));
        themes.put("theme-vilagos", createTheme("theme-vilagos", "Vilagos"));
        return themes;
    }

    // ---- Hatterképek ----

    private Map<String, Wallpaper> createWallpapers() {
        Map<String, Wallpaper> wallpapers = new LinkedHashMap<>();
        wallpapers.put("wp-alap", createWallpaper("wp-alap", "Alapertelmezett"));
        wallpapers.put("wp-termeszet", createWallpaper("wp-termeszet", "Termeszet"));
        wallpapers.put("wp-absztrakt", createWallpaper("wp-absztrakt", "Absztrakt"));
        return wallpapers;
    }

    // ---- Családtag létrehozása menüvel ----

    private Account createFamilyMember(String accountIdentifier, String name, String username,
                                       String password, String menuIdentifier, String menuName) {
        Menu menu = menuService.create(Menu.builder()
                .uniqueIdentifier(menuIdentifier)
                .name(menuName)
                .build());

        Account account = accountService.create(Account.builder()
                .uniqueIdentifier(accountIdentifier)
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password))
                .menu(menu)
                .build());

        // Alapertelmezett menu elem (Kedvencek mappa)
        menuItemService.create(MenuItem.builder()
                .uniqueIdentifier("mi-" + username + "-kedvencek")
                .name("Kedvencek")
                .menu(menu)
                .build());

        return account;
    }

    // ---- Alkalmazas telepitese + menu elem letrehozasa ----

    private void installAppForAccount(Account account, Application application,
                                      String menuItemIdentifier, String menuItemName) {
        accountService.installApplication(account.getId(), application.getId());

        Menu menu = account.getMenu();
        MenuItem favoritesRoot = menu.getMenuItems().stream()
                .filter(mi -> mi.getName().equals("Kedvencek"))
                .findFirst()
                .orElse(null);

        menuItemService.create(MenuItem.builder()
                .uniqueIdentifier(menuItemIdentifier)
                .name(menuItemName)
                .menu(menu)
                .application(application)
                .parent(favoritesRoot)
                .build());
    }

    // ---- Segéd metódusok ----

    private Application createApp(String uniqueIdentifier, String name) {
        return applicationService.create(Application.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .build());
    }

    private Theme createTheme(String uniqueIdentifier, String name) {
        return themeService.create(Theme.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .build());
    }

    private Wallpaper createWallpaper(String uniqueIdentifier, String name) {
        return wallpaperService.create(Wallpaper.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .build());
    }

    private void printAccount(Account account, String favoriteApp) {
        log.info("[FIOK] {} ({}) | Kedvenc: {} | Tema: {} | Hatterkep: {}",
                account.getName(), account.getUsername(), favoriteApp,
                account.getTheme() != null ? account.getTheme().getName() : "-",
                account.getWallpaper() != null ? account.getWallpaper().getName() : "-");
    }
}