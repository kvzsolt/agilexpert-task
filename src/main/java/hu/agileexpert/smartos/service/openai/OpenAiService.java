package hu.agileexpert.smartos.service.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import hu.agileexpert.smartos.domain.*;
import hu.agileexpert.smartos.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    private final AccountService accountService;
    private final ApplicationService applicationService;
    private final MenuService menuService;
    private final MenuItemService menuItemService;
    private final ThemeService themeService;
    private final WallpaperService wallpaperService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    private OpenAIClient getClient() {
        return OpenAIOkHttpClient.fromEnv();
    }
    public String interpretAndExecute(String userPrompt) {
        log.info("Processing user prompt: {}", userPrompt);

        String systemPrompt = buildCommandSystemPrompt();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .addSystemMessage(systemPrompt)
                .addUserMessage(userPrompt)
                .build();

        ChatCompletion completion = getClient().chat().completions().create(params);
        String response = completion.choices().getFirst().message().content().orElse("");

        log.info("LLM Response: {}", response);

        return executeCommand(response);
    }


    public String runSimulation() {
        log.info("Starting simulation - generating sample data via LLM");

        String systemPrompt = buildSimulationSystemPrompt();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .addSystemMessage(systemPrompt)
                .addUserMessage("Generate a complete family simulation with parents (mother, father) and 2 children. " +
                        "The mother should install a recipe application, the father should install a GPS/map application, " +
                        "and the children should install games. Create themes, wallpapers, and configure menu structures.")
                .build();

        ChatCompletion completion = getClient().chat().completions().create(params);
        String response = completion.choices().getFirst().message().content().orElse("");

        log.info("Simulation LLM Response: {}", response);

        return executeSimulationCommands(response);
    }

    private String buildCommandSystemPrompt() {
        List<Application> apps = applicationService.findAll();
        List<Account> accounts = accountService.findAll();

        StringBuilder appList = new StringBuilder();
        for (Application app : apps) {
            appList.append(String.format("  - id=%d, name='%s'%n", app.getId(), app.getName()));
        }

        StringBuilder accountList = new StringBuilder();
        for (Account acc : accounts) {
            accountList.append(String.format("  - id=%d, name='%s', username='%s'%n",
                    acc.getId(), acc.getName(), acc.getUsername()));
        }

        return """
                You are a SmartOS command interpreter. Analyze the user's natural language request and return a JSON command.
                
                Available applications:
                %s
                
                Available accounts:
                %s
                
                Respond ONLY with a JSON object in this format:
                {
                  "action": "ACTION_TYPE",
                  "params": { ... }
                }
                
                Available actions and their params:
                
                1. LAUNCH_APP - Launch an application
                   {"action": "LAUNCH_APP", "params": {"accountId": <id>, "applicationId": <id>}}
                   
                2. INSTALL_APP - Install an application to an account
                   {"action": "INSTALL_APP", "params": {"accountId": <id>, "applicationId": <id>}}
                   
                3. REMOVE_APP - Remove an application from an account
                   {"action": "REMOVE_APP", "params": {"accountId": <id>, "applicationId": <id>}}
                   
                4. CHANGE_THEME - Change account theme
                   {"action": "CHANGE_THEME", "params": {"accountId": <id>, "themeId": <id>}}
                   
                5. CHANGE_WALLPAPER - Change account wallpaper
                   {"action": "CHANGE_WALLPAPER", "params": {"accountId": <id>, "wallpaperId": <id>}}
                   
                6. LIST_APPS - List all applications
                   {"action": "LIST_APPS", "params": {}}
                   
                7. LIST_ACCOUNTS - List all accounts
                   {"action": "LIST_ACCOUNTS", "params": {}}
                   
                8. ACCOUNT_INFO - Show account details
                   {"action": "ACCOUNT_INFO", "params": {"accountId": <id>}}
                   
                9. UNKNOWN - If you cannot understand the request
                   {"action": "UNKNOWN", "params": {"message": "explanation of the issue"}}
                
                If the user mentions an app by name (e.g., "map", "térkép", "openmap", "gps"), find the closest matching application.
                If the user doesn't specify an account, use accountId 1 as default.
                
                Examples:
                - "indítsd el a térkép alkalmazást" -> LAUNCH_APP with openmap/gps app
                - "start the map application" -> LAUNCH_APP with openmap/gps app
                - "listázd az alkalmazásokat" -> LIST_APPS
                - "telepítsd az aknakeresőt az apa fiókjára" -> INSTALL_APP
                """.formatted(appList.toString(), accountList.toString());
    }

    private String buildSimulationSystemPrompt() {
        return """
                You are a SmartOS data generator. Generate a JSON array of commands to create a complete family simulation.
                
                The family consists of:
                - Mother (Édesanya) - installs recipe application
                - Father (Édesapa) - installs GPS/map application  
                - Child 1 (Gyerek1) - installs games
                - Child 2 (Gyerek2) - installs games
                
                Required applications to create: aknakereső, openmap, paint, címtár, recipes, doodle_jump
                Required themes: Light, Dark, Blue
                Required wallpapers: Forest, Ocean, Mountains, Space
                
                Generate commands in this JSON array format:
                [
                  {"type": "CREATE_APP", "params": {"uniqueIdentifier": "app-xxx", "name": "App Name"}},
                  {"type": "CREATE_THEME", "params": {"uniqueIdentifier": "theme-xxx", "name": "Theme Name"}},
                  {"type": "CREATE_WALLPAPER", "params": {"uniqueIdentifier": "wp-xxx", "name": "Wallpaper Name"}},
                  {"type": "CREATE_ACCOUNT", "params": {"uniqueIdentifier": "acc-xxx", "name": "Person Name", "username": "username", "password": "password123"}},
                  {"type": "INSTALL_APP", "params": {"accountId": 1, "applicationId": 1}},
                  {"type": "SET_THEME", "params": {"accountId": 1, "themeId": 1}},
                  {"type": "SET_WALLPAPER", "params": {"accountId": 1, "wallpaperId": 1}},
                  {"type": "ADD_MENU_ITEM", "params": {"uniqueIdentifier": "icon-xxx", "name": "Icon Name", "menuId": 1, "applicationId": 1, "parentId": null}}
                ]
                
                IMPORTANT RULES:
                - Respond ONLY with a valid JSON array. No additional text, no comments, no trailing commas.
                - Do NOT include // comments inside the JSON. Pure JSON only.
                - Use unique usernames that include a random suffix, e.g. "anna.sim123", "janos.sim456".
                - First create all applications, themes, wallpapers.
                - Then create accounts (which auto-creates menus).
                - Then install apps and set preferences.
                - Then add menu items to organize the menus.
                - The IDs used in INSTALL_APP, SET_THEME, SET_WALLPAPER, ADD_MENU_ITEM must reference
                  the creation ORDER (1-based). For example, the 1st CREATE_APP produces applicationId=1, etc.
                
                Use realistic Hungarian names for the family members.
                """;
    }

    private String executeCommand(String llmResponse) {
        try {
            String jsonStr = extractJson(llmResponse);
            JsonNode json = objectMapper.readTree(jsonStr);
            String action = json.get("action").asText();
            JsonNode params = json.get("params");

            return switch (action) {
                case "LAUNCH_APP" -> {
                    Long accountId = params.get("accountId").asLong();
                    Long appId = params.get("applicationId").asLong();
                    yield accountService.launchApplication(accountId, appId);
                }
                case "INSTALL_APP" -> {
                    Long accountId = params.get("accountId").asLong();
                    Long appId = params.get("applicationId").asLong();
                    accountService.installApplication(accountId, appId);
                    Application app = applicationService.findById(appId);
                    Account acc = accountService.findById(accountId);
                    yield "Alkalmazás telepítve: " + app.getName() + " -> " + acc.getName();
                }
                case "REMOVE_APP" -> {
                    Long accountId = params.get("accountId").asLong();
                    Long appId = params.get("applicationId").asLong();
                    accountService.removeApplication(accountId, appId);
                    yield "Alkalmazás eltávolítva.";
                }
                case "CHANGE_THEME" -> {
                    Long accountId = params.get("accountId").asLong();
                    Long themeId = params.get("themeId").asLong();
                    Account acc = accountService.setTheme(accountId, themeId);
                    yield "Téma módosítva: " + acc.getTheme().getName() + " (" + acc.getName() + ")";
                }
                case "CHANGE_WALLPAPER" -> {
                    Long accountId = params.get("accountId").asLong();
                    Long wallpaperId = params.get("wallpaperId").asLong();
                    Account acc = accountService.setWallpaper(accountId, wallpaperId);
                    yield "Háttérkép módosítva: " + acc.getWallpaper().getName() + " (" + acc.getName() + ")";
                }
                case "LIST_APPS" -> {
                    List<Application> apps = applicationService.findAll();
                    StringBuilder sb = new StringBuilder("Alkalmazások:\n");
                    for (Application app : apps) {
                        sb.append(String.format("  [%d] %s%n", app.getId(), app.getName()));
                    }
                    yield sb.toString();
                }
                case "LIST_ACCOUNTS" -> {
                    List<Account> accounts = accountService.findAll();
                    StringBuilder sb = new StringBuilder("Felhasználók:\n");
                    for (Account acc : accounts) {
                        sb.append(String.format("  [%d] %s (%s)%n", acc.getId(), acc.getName(), acc.getUsername()));
                    }
                    yield sb.toString();
                }
                case "ACCOUNT_INFO" -> {
                    Long accountId = params.get("accountId").asLong();
                    Account acc = accountService.findById(accountId);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Felhasználó: ").append(acc.getName()).append("\n");
                    sb.append("Téma: ").append(acc.getTheme() != null ? acc.getTheme().getName() : "-").append("\n");
                    sb.append("Háttérkép: ").append(acc.getWallpaper() != null ? acc.getWallpaper().getName() : "-").append("\n");
                    sb.append("Telepített alkalmazások: ");
                    if (acc.getApplications().isEmpty()) {
                        sb.append("-");
                    } else {
                        for (Application app : acc.getApplications()) {
                            sb.append(app.getName()).append(", ");
                        }
                    }
                    yield sb.toString();
                }
                case "UNKNOWN" -> {
                    String message = params.has("message") ? params.get("message").asText() : "Ismeretlen parancs";
                    yield "Nem sikerült értelmezni a kérést: " + message;
                }
                default -> "Ismeretlen művelet: " + action;
            };
        } catch (Exception e) {
            log.error("Error executing command", e);
            return "Hiba történt a parancs végrehajtása során: " + e.getMessage();
        }
    }

    private String executeSimulationCommands(String llmResponse) {
        StringBuilder result = new StringBuilder();
        result.append("Szimuláció indítása\n\n");

        try {
            String jsonStr = extractJson(llmResponse);
            JsonNode commands = objectMapper.readTree(jsonStr);

            if (!commands.isArray()) {
                return "Hiba: Az LLM válasz nem egy JSON tömb.";
            }

            // Track created entity IDs: LLM 1-based order -> actual DB ID
            Map<Integer, Long> appIdMap = new HashMap<>();
            Map<Integer, Long> themeIdMap = new HashMap<>();
            Map<Integer, Long> wallpaperIdMap = new HashMap<>();
            Map<Integer, Long> accountIdMap = new HashMap<>();
            Map<Integer, Long> menuIdMap = new HashMap<>();
            int appSeq = 0, themeSeq = 0, wpSeq = 0, accSeq = 0;

            for (JsonNode cmd : commands) {
                String type = cmd.get("type").asText();
                JsonNode params = cmd.get("params");

                try {
                    switch (type) {
                        case "CREATE_APP" -> {
                            Application app = Application.builder()
                                    .uniqueIdentifier(params.get("uniqueIdentifier").asText())
                                    .name(params.get("name").asText())
                                    .build();
                            app = applicationService.create(app);
                            appSeq++;
                            appIdMap.put(appSeq, app.getId());
                            result.append("✓ Alkalmazás létrehozva: ").append(app.getName())
                                    .append(" (seq=").append(appSeq).append(" -> id=").append(app.getId()).append(")\n");
                        }
                        case "CREATE_THEME" -> {
                            Theme theme = Theme.builder()
                                    .uniqueIdentifier(params.get("uniqueIdentifier").asText())
                                    .name(params.get("name").asText())
                                    .build();
                            theme = themeService.create(theme);
                            themeSeq++;
                            themeIdMap.put(themeSeq, theme.getId());
                            result.append("✓ Téma létrehozva: ").append(theme.getName())
                                    .append(" (seq=").append(themeSeq).append(" -> id=").append(theme.getId()).append(")\n");
                        }
                        case "CREATE_WALLPAPER" -> {
                            Wallpaper wp = Wallpaper.builder()
                                    .uniqueIdentifier(params.get("uniqueIdentifier").asText())
                                    .name(params.get("name").asText())
                                    .build();
                            wp = wallpaperService.create(wp);
                            wpSeq++;
                            wallpaperIdMap.put(wpSeq, wp.getId());
                            result.append("✓ Háttérkép létrehozva: ").append(wp.getName())
                                    .append(" (seq=").append(wpSeq).append(" -> id=").append(wp.getId()).append(")\n");
                        }
                        case "CREATE_ACCOUNT" -> {
                            Menu menu = menuService.create(Menu.builder()
                                    .uniqueIdentifier("menu-" + params.get("username").asText())
                                    .name(params.get("name").asText() + " menü")
                                    .build());

                            Account acc = Account.builder()
                                    .uniqueIdentifier(accountService.generateAccountUniqueIdentifier())
                                    .name(params.get("name").asText())
                                    .username(params.get("username").asText())
                                    .password(passwordEncoder.encode(params.get("password").asText()))
                                    .menu(menu)
                                    .build();
                            acc = accountService.create(acc);
                            accSeq++;
                            accountIdMap.put(accSeq, acc.getId());
                            menuIdMap.put(accSeq, menu.getId());
                            result.append("✓ Felhasználó létrehozva: ").append(acc.getName())
                                    .append(" (seq=").append(accSeq).append(" -> id=").append(acc.getId()).append(")\n");
                        }
                        case "INSTALL_APP" -> {
                            int accRef = params.get("accountId").asInt();
                            int appRef = params.get("applicationId").asInt();
                            Long realAccId = accountIdMap.get(accRef);
                            Long realAppId = appIdMap.get(appRef);
                            if (realAccId == null || realAppId == null) {
                                result.append("⚠ INSTALL_APP kihagyva: érvénytelen hivatkozás (acc=").append(accRef).append(", app=").append(appRef).append(")\n");
                            } else {
                                accountService.installApplication(realAccId, realAppId);
                                result.append("✓ Alkalmazás telepítve fiókra #").append(realAccId).append("\n");
                            }
                        }
                        case "SET_THEME" -> {
                            int accRef = params.get("accountId").asInt();
                            int themeRef = params.get("themeId").asInt();
                            Long realAccId = accountIdMap.get(accRef);
                            Long realThemeId = themeIdMap.get(themeRef);
                            if (realAccId == null || realThemeId == null) {
                                result.append("⚠ SET_THEME kihagyva: érvénytelen hivatkozás\n");
                            } else {
                                accountService.setTheme(realAccId, realThemeId);
                                result.append("✓ Téma beállítva fiókra #").append(realAccId).append("\n");
                            }
                        }
                        case "SET_WALLPAPER" -> {
                            int accRef = params.get("accountId").asInt();
                            int wpRef = params.get("wallpaperId").asInt();
                            Long realAccId = accountIdMap.get(accRef);
                            Long realWpId = wallpaperIdMap.get(wpRef);
                            if (realAccId == null || realWpId == null) {
                                result.append("⚠ SET_WALLPAPER kihagyva: érvénytelen hivatkozás\n");
                            } else {
                                accountService.setWallpaper(realAccId, realWpId);
                                result.append("✓ Háttérkép beállítva fiókra #").append(realAccId).append("\n");
                            }
                        }
                        case "ADD_MENU_ITEM" -> {
                            int menuRef = params.get("menuId").asInt();
                            Long realMenuId = menuIdMap.get(menuRef);
                            if (realMenuId == null) {
                                result.append("⚠ ADD_MENU_ITEM kihagyva: érvénytelen menü hivatkozás\n");
                            } else {
                                int appRef = params.has("applicationId") && !params.get("applicationId").isNull()
                                        ? params.get("applicationId").asInt() : 0;
                                Long realAppId = appRef > 0 ? appIdMap.get(appRef) : null;
                                Long parentId = params.has("parentId") && !params.get("parentId").isNull()
                                        ? params.get("parentId").asLong() : null;

                                Menu menu = menuService.findById(realMenuId);
                                Application app = realAppId != null ? applicationService.findById(realAppId) : null;
                                MenuItem parent = parentId != null ? menuItemService.findById(parentId) : null;

                                MenuItem item = MenuItem.builder()
                                        .uniqueIdentifier(params.get("uniqueIdentifier").asText())
                                        .name(params.get("name").asText())
                                        .menu(menu)
                                        .application(app)
                                        .parent(parent)
                                        .build();
                                menuItemService.create(item);
                                result.append("✓ Menüelem létrehozva: ").append(item.getName()).append("\n");
                            }
                        }
                        default -> result.append("⚠ Ismeretlen parancs típus: ").append(type).append("\n");
                    }
                } catch (Exception e) {
                    result.append("⚠ Hiba: ").append(e.getMessage()).append("\n");
                }
            }

            result.append("\n=== Szimuláció befejezve ===\n");
            result.append(String.format("Létrehozva: %d alkalmazás, %d téma, %d háttérkép, %d felhasználó%n",
                    appSeq, themeSeq, wpSeq, accSeq));

        } catch (Exception e) {
            log.error("Error executing simulation", e);
            result.append("Hiba történt a szimuláció során: ").append(e.getMessage());
        }

        return result.toString();
    }

    private String extractJson(String response) {
        String cleaned = response.replaceAll("//[^\n]*", "");

        int start = cleaned.indexOf('[');
        int end = cleaned.lastIndexOf(']');
        
        if (start == -1 || end == -1) {
            start = cleaned.indexOf('{');
            end = cleaned.lastIndexOf('}');
        }
        
        if (start != -1 && end != -1 && end > start) {
            return cleaned.substring(start, end + 1);
        }
        return cleaned;
    }
}
