package hu.agileexpert.smartos.shell;

import hu.agileexpert.smartos.domain.Account;
import hu.agileexpert.smartos.domain.Application;
import hu.agileexpert.smartos.domain.Menu;
import hu.agileexpert.smartos.domain.MenuItem;
import hu.agileexpert.smartos.domain.Theme;
import hu.agileexpert.smartos.domain.Wallpaper;
import hu.agileexpert.smartos.service.AccountService;
import hu.agileexpert.smartos.service.ApplicationService;
import hu.agileexpert.smartos.service.MenuItemService;
import hu.agileexpert.smartos.service.MenuService;
import hu.agileexpert.smartos.service.ThemeService;
import hu.agileexpert.smartos.service.WallpaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class SmartOsShellCommands {

    private final AccountService accountService;
    private final ApplicationService applicationService;
    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final ThemeService themeService;
    private final WallpaperService wallpaperService;
    private final BCryptPasswordEncoder passwordEncoder;


    @ShellMethod(key = "account-create", value = "Create a new user account with a menu.")
    public String createAccount(
            @ShellOption(value = "--name") String name,
            @ShellOption(value = "--username") String username,
            @ShellOption(value = "--password") String password) {

        String uniqueIdentifier = accountService.generateAccountUniqueIdentifier();

        Menu menu = menuService.create(Menu.builder()
                .uniqueIdentifier("menu-" + username)
                .name(name + " menu")
                .build());

        Account account = accountService.create(Account.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password))
                .menu(menu)
                .build());

        return "Account created: " + account.getName()
                + " (id=" + account.getId() + ", uid=" + account.getUniqueIdentifier()
                + ", username=" + account.getUsername() + ")";
    }

    @ShellMethod(key = "account-update", value = "Update the name of an existing user account.")
    public String updateAccount(
            @ShellOption(value = "--id") Long id,
            @ShellOption(value = "--uid") String uniqueIdentifier,
            @ShellOption(value = "--name") String name) {

        Account updated = accountService.update(id, Account.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .build());

        return "Account updated: " + updated.getName() + " (id=" + updated.getId() + ")";
    }

    @ShellMethod(key = "account-delete", value = "Delete a user account.")
    public String deleteAccount(@ShellOption(value = "--id") Long id) {
        Account account = accountService.findById(id);
        String name = account.getName();
        accountService.deleteById(id);
        return "Account deleted: " + name + " (id=" + id + ")";
    }

    @ShellMethod(key = "account-list", value = "List all user accounts.")
    public String listAccounts() {
        List<Account> accounts = accountService.findAll();
        if (accounts.isEmpty()) {
            return "No accounts found.";
        }
        StringBuilder sb = new StringBuilder("Accounts:\n");
        for (Account a : accounts) {
            sb.append(String.format("  [%d] %s (%s) | theme: %s | wallpaper: %s%n",
                    a.getId(), a.getName(), a.getUsername(),
                    a.getTheme() != null ? a.getTheme().getName() : "-",
                    a.getWallpaper() != null ? a.getWallpaper().getName() : "-"));
        }
        return sb.toString().trim();
    }

    @ShellMethod(key = "account-info", value = "Show detailed information about a user account.")
    public String accountInfo(@ShellOption(value = "--id") Long id) {
        Account a = accountService.findById(id);
        String apps = a.getApplications().stream()
                .map(app -> app.getName() + " (id=" + app.getId() + ")")
                .collect(Collectors.joining(", "));
        return String.format("Account: %s (id=%d, username=%s)%n  Theme: %s%n  Wallpaper: %s%n  Installed applications: %s",
                a.getName(), a.getId(), a.getUsername(),
                a.getTheme() != null ? a.getTheme().getName() : "-",
                a.getWallpaper() != null ? a.getWallpaper().getName() : "-",
                apps.isEmpty() ? "-" : apps);
    }

    @ShellMethod(key = "app-list", value = "List all applications.")
    public String listApplications() {
        List<Application> apps = applicationService.findAll();
        if (apps.isEmpty()) {
            return "No applications found.";
        }
        StringBuilder sb = new StringBuilder("Applications:\n");
        for (Application app : apps) {
            sb.append(String.format("  [%d] %s (%s)%n", app.getId(), app.getName(), app.getUniqueIdentifier()));
        }
        return sb.toString().trim();
    }

    @ShellMethod(key = "app-install", value = "Install an application to an account.")
    public String installApplication(
            @ShellOption(value = "--account-id") Long accountId,
            @ShellOption(value = "--app-id") Long applicationId) {

        Account account = accountService.installApplication(accountId, applicationId);
        Application app = applicationService.findById(applicationId);
        return "Application " + app.getName() + " installed to account " + account.getName() + ".";
    }

    @ShellMethod(key = "app-remove", value = "Remove an application from an account.")
    public String removeApplication(
            @ShellOption(value = "--account-id") Long accountId,
            @ShellOption(value = "--app-id") Long applicationId) {

        Account account = accountService.removeApplication(accountId, applicationId);
        return "Application removed from account " + account.getName() + ".";
    }

    @ShellMethod(key = "app-launch", value = "Launch an application.")
    public String launchApplication(
            @ShellOption(value = "--account-id") Long accountId,
            @ShellOption(value = "--app-id") Long applicationId) {

        return accountService.launchApplication(accountId, applicationId);
    }

    @ShellMethod(key = "menu-list", value = "List all menus.")
    public String listMenus() {
        List<Menu> menus = menuService.findAll();
        if (menus.isEmpty()) {
            return "No menus found.";
        }
        StringBuilder sb = new StringBuilder("Menus:\n");
        for (Menu m : menus) {
            sb.append(String.format("  [%d] %s (%s) - %d items%n",
                    m.getId(), m.getName(), m.getUniqueIdentifier(), m.getMenuItems().size()));
        }
        return sb.toString().trim();
    }

    @ShellMethod(key = "menu-show", value = "Show the tree structure of a menu.")
    public String showMenu(@ShellOption(value = "--id") Long id) {
        Menu menu = menuService.findById(id);
        StringBuilder sb = new StringBuilder();
        sb.append("Menu: ").append(menu.getName()).append(" (id=").append(menu.getId()).append(")\n");
        List<MenuItem> roots = menu.getMenuItems().stream()
                .filter(mi -> mi.getParent() == null)
                .toList();
        for (MenuItem root : roots) {
            printMenuTree(sb, root, 1);
        }
        return sb.toString().trim();
    }

    private void printMenuTree(StringBuilder sb, MenuItem item, int depth) {
        String indent = "  ".repeat(depth);
        String appInfo = item.getApplication() != null
                ? " -> [" + item.getApplication().getName() + "]"
                : "";
        sb.append(String.format("%s- %s (id=%d)%s%n", indent, item.getName(), item.getId(), appInfo));
        for (MenuItem child : item.getChildren()) {
            printMenuTree(sb, child, depth + 1);
        }
    }

    @ShellMethod(key = "icon-add", value = "Add a new icon (menu item) to a menu.")
    public String addMenuItem(
            @ShellOption(value = "--uid") String uniqueIdentifier,
            @ShellOption(value = "--name") String name,
            @ShellOption(value = "--menu-id") Long menuId,
            @ShellOption(value = "--app-id", defaultValue = ShellOption.NULL) Long applicationId,
            @ShellOption(value = "--parent-id", defaultValue = ShellOption.NULL) Long parentId) {

        Menu menu = menuService.findById(menuId);
        Application application = applicationId != null ? applicationService.findById(applicationId) : null;
        MenuItem parent = parentId != null ? menuItemService.findById(parentId) : null;

        MenuItem item = menuItemService.create(MenuItem.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .menu(menu)
                .application(application)
                .parent(parent)
                .build());

        return "Icon added: " + item.getName() + " (id=" + item.getId() + ")";
    }

    @ShellMethod(key = "icon-update", value = "Update the name of an icon (menu item).")
    public String updateMenuItem(
            @ShellOption(value = "--id") Long id,
            @ShellOption(value = "--uid") String uniqueIdentifier,
            @ShellOption(value = "--name") String name,
            @ShellOption(value = "--menu-id") Long menuId,
            @ShellOption(value = "--app-id", defaultValue = ShellOption.NULL) Long applicationId,
            @ShellOption(value = "--parent-id", defaultValue = ShellOption.NULL) Long parentId) {

        Menu menu = menuService.findById(menuId);
        Application application = applicationId != null ? applicationService.findById(applicationId) : null;
        MenuItem parent = parentId != null ? menuItemService.findById(parentId) : null;

        MenuItem updated = menuItemService.update(id, MenuItem.builder()
                .id(id)
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .menu(menu)
                .application(application)
                .parent(parent)
                .build());

        return "Icon updated: " + updated.getName() + " (id=" + updated.getId() + ")";
    }

    @ShellMethod(key = "icon-delete", value = "Delete an icon (menu item).")
    public String deleteMenuItem(@ShellOption(value = "--id") Long id) {
        MenuItem item = menuItemService.findById(id);
        String name = item.getName();
        menuItemService.deleteById(id);
        return "Icon deleted: " + name + " (id=" + id + ")";
    }

    @ShellMethod(key = "theme-list", value = "List all themes.")
    public String listThemes() {
        List<Theme> themes = themeService.findAll();
        if (themes.isEmpty()) {
            return "No themes found.";
        }
        StringBuilder sb = new StringBuilder("Themes:\n");
        for (Theme t : themes) {
            sb.append(String.format("  [%d] %s (%s)%n", t.getId(), t.getName(), t.getUniqueIdentifier()));
        }
        return sb.toString().trim();
    }

    @ShellMethod(key = "theme-change", value = "Change the theme for an account.")
    public String changeTheme(
            @ShellOption(value = "--account-id") Long accountId,
            @ShellOption(value = "--theme-id") Long themeId) {

        Account account = accountService.setTheme(accountId, themeId);
        return "Theme changed for " + account.getName() + " to: " + account.getTheme().getName();
    }

    @ShellMethod(key = "wallpaper-list", value = "List all wallpapers.")
    public String listWallpapers() {
        List<Wallpaper> wallpapers = wallpaperService.findAll();
        if (wallpapers.isEmpty()) {
            return "No wallpapers found.";
        }
        StringBuilder sb = new StringBuilder("Wallpapers:\n");
        for (Wallpaper w : wallpapers) {
            sb.append(String.format("  [%d] %s (%s)%n", w.getId(), w.getName(), w.getUniqueIdentifier()));
        }
        return sb.toString().trim();
    }

    @ShellMethod(key = "wallpaper-add", value = "Add a new wallpaper.")
    public String addWallpaper(
            @ShellOption(value = "--uid") String uniqueIdentifier,
            @ShellOption(value = "--name") String name) {

        Wallpaper wallpaper = wallpaperService.create(Wallpaper.builder()
                .uniqueIdentifier(uniqueIdentifier)
                .name(name)
                .build());
        return "Wallpaper added: " + wallpaper.getName() + " (id=" + wallpaper.getId() + ")";
    }

    @ShellMethod(key = "wallpaper-select", value = "Select a wallpaper for an account.")
    public String selectWallpaper(
            @ShellOption(value = "--account-id") Long accountId,
            @ShellOption(value = "--wallpaper-id") Long wallpaperId) {

        Account account = accountService.setWallpaper(accountId, wallpaperId);
        return "Wallpaper changed for " + account.getName() + " to: " + account.getWallpaper().getName();
    }

    @ShellMethod(key = "smartos-help", value = "Summary of SmartOS commands.")
    public String smartosHelp() {
        return """
                ========================================
                  SmartOS — Available Commands
                ========================================
                ACCOUNT MANAGEMENT:
                  account-create  --name <name> --username <user> --password <pw>
                  account-update  --id <id> --uid <uid> --name <name>
                  account-delete  --id <id>
                  account-list
                  account-info    --id <id>
               \s
                APPLICATION MANAGEMENT:
                  app-list
                  app-install     --account-id <id> --app-id <id>
                  app-remove      --account-id <id> --app-id <id>
                  app-launch      --account-id <id> --app-id <id>
               \s
                MENU MANAGEMENT:
                  menu-list
                  menu-show       --id <id>
               \s
                ICON (MENU ITEM) MANAGEMENT:
                  icon-add        --uid <uid> --name <name> --menu-id <id> [--app-id <id>] [--parent-id <id>]
                  icon-update     --id <id> --uid <uid> --name <name> --menu-id <id> [--app-id <id>] [--parent-id <id>]
                  icon-delete     --id <id>
               \s
                THEME MANAGEMENT:
                  theme-list
                  theme-change    --account-id <id> --theme-id <id>
               \s
                WALLPAPER MANAGEMENT:
                  wallpaper-list
                  wallpaper-add   --uid <uid> --name <name>
                  wallpaper-select --account-id <id> --wallpaper-id <id>
                ========================================
                """;
    }
}