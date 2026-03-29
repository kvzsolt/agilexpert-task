# SmartOS Docker setup

This project can run fully in Docker with:
- the Spring Boot application
- a MySQL 8 database
- Docker Compose environment-based secrets for database credentials

## Quick start

```bash
export DB_NAME=smartos
export DB_USER=smartos
export DB_PASSWORD=changeit123
export MYSQL_ROOT_PASSWORD=rootchangeit123

docker compose up -d --build
```

Open:
- App: `http://localhost:8080`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Stop

```bash
docker compose down
```

## Reset database volume

```bash
docker compose down -v
```

## Files

- `Dockerfile` - multi-stage Java 21 image build
- `docker-compose.yml` - app + MySQL orchestration
- `src/main/resources/application-docker.yaml` - Docker-specific Spring profile

## Notes

- The application uses the `docker` Spring profile inside Compose.
- Local default configuration in `application.yaml` remains H2-based.
- Docker runtime switches to MySQL through `application-docker.yaml`.
- Secrets are passed via environment variables (`DB_NAME`, `DB_USER`, `DB_PASSWORD`, `MYSQL_ROOT_PASSWORD`); rotate them before any real deployment.
