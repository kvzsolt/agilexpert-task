```dotenv
MYSQL_DATABASE=smartos
MYSQL_ROOT_PASSWORD=changeit123
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=changeit123
OPENAI_API_KEY=
```

### 1. Indítás

```bash
docker compose up --build -d
```
Spring shell elérése:
```bash
docker attach smartos-app
```
Leállítás:
```bash
docker compose down
```

Adatbázis törlésével együtt (tiszta újraindításhoz):
```bash
docker compose down -v
```

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

```
Docker nélkül: Így nem működnek az AI parancsok, mert H2-es adatbázist használ.
mvn spring-boot:run
```

```
Spring Shell parancsok

| Parancs | Leírás |
|---|---|
| `smartos-help` | Összes parancs listázása |
| **Fiókkezelés** | |
| `account-create --name <név> --username <user> --password <pw>` | Új fiók létrehozása |
| `account-update --id <id> --uid <uid> --name <név>` | Fiók módosítása |
| `account-delete --id <id>` | Fiók törlése |
| `account-list` | Fiókok listázása |
| `account-info --id <id>` | Fiók részletei |
| **Alkalmazások** | |
| `app-list` | Alkalmazások listázása |
| `app-install --account-id <id> --app-id <id>` | Alkalmazás telepítése fiókra |
| `app-remove --account-id <id> --app-id <id>` | Alkalmazás eltávolítása |
| `app-launch --account-id <id> --app-id <id>` | Alkalmazás indítása |
| **Menükezelés** | |
| `menu-list` | Menük listázása |
| `menu-show --id <id>` | Menü fa struktúra |
| **Ikon (menüelem)** | |
| `icon-add --uid <uid> --name <név> --menu-id <id> [--app-id <id>] [--parent-id <id>]` | Új ikon hozzáadása |
| `icon-update --id <id> --uid <uid> --name <név> --menu-id <id> [--app-id <id>] [--parent-id <id>]` | Ikon módosítása |
| `icon-delete --id <id>` | Ikon törlése |
| **Téma** | |
| `theme-list` | Témák listázása |
| `theme-change --account-id <id> --theme-id <id>` | Arculatváltás |
| **Háttérkép** | |
| `wallpaper-list` | Hátterek listázása |
| `wallpaper-add --uid <uid> --name <név>` | Háttérkép hozzáadása |
| `wallpaper-select --account-id <id> --wallpaper-id <id>` | Háttérkép kiválasztása |
| **AI parancsok** | |
| `prompt --text "<természetes nyelvi parancs>"` | LLM értelmezi és végrehajtja |
| `simulation` | LLM-mel példaadatok generálása |
```


```
shell:> prompt --text "indítsd el a térkép alkalmazást"
shell:> prompt --text "telepítsd az aknakeresőt az apa fiókjára"
shell:> prompt --text "listázd az alkalmazásokat"
```

Szimuláció indítása:
```
shell:> simulation
```

## Inicializáció

Az alkalmazás induláskor automatikusan létrehozza a példa adatokat (ha az adatbázis üres):

- **Alkalmazások**: Aknakereső, OpenMap, Paint, Címtár, Receptek, Doodle Jump
- **Témák**: Alap, Sötét, Világos
- **Hátterek**: Alap, Természet, Absztrakt
- **Család**:
  - Kovács János (apa) — OpenMap telepítve, Sötét téma
  - Kovács Mária (anya) — Receptek telepítve, Világos téma
  - Kovács Péter (gyerek1) — Doodle Jump telepítve
  - Kovács Anna (gyerek2) — Aknakerező telepítve

