# ProyectoCrediya

Plataforma de solicitudes de crédito **CrediYa**, organizada como microservicios independientes con Clean Architecture
(scaffold de Bancolombia, `reactive=false`, Spring MVC + JPA, Java 25).

| Microservicio | Carpeta | Puerto | Historia de usuario |
|---|---|---|---|
| Autenticación | [`crediya-autenticacion-ms/`](crediya-autenticacion-ms/README.md) | 8080 | HU 1 — Registrar usuarios (`POST /api/v1/usuarios`) |
| Solicitudes | [`crediya-solicitudes-ms/`](crediya-solicitudes-ms/README.md) | 8081 | HU 2 — Registrar solicitud de préstamo (`POST /api/v1/solicitud`) |

Cada microservicio es un proyecto Gradle independiente (su propio `gradlew`) y comparten la base de datos PostgreSQL
definida en `docker-compose.yml`.

## Estructura común

```
crediya-<nombre>-ms/
├── applications/app-service          # arranque y configuración (application.yaml)
├── domain/model                      # modelos, gateways (puertos) y excepciones
├── domain/usecase                    # casos de uso
├── infrastructure/driven-adapters/jpa-repository
│   ├── entity/                       # entidades JPA
│   ├── repository/                   # repositorios Spring Data (<Recurso>JpaRepository)
│   ├── <Recurso>RepositoryAdapter    # implementación de los gateways
│   ├── config/ helper/ transaction/  # JPA, helper genérico y transacciones
│   └── seed/                         # datos iniciales (solo si el servicio los necesita)
└── infrastructure/entry-points/api-rest
    ├── ApiRest                       # controlador REST
    ├── dto/ exception/ config/       # DTOs, manejo global de errores, CORS y cabeceras
    └── trace/                        # X-Trace-Id en respuestas y logs
```

## Cómo ejecutar (PowerShell)

```powershell
cd C:\Users\juanm\Crediya
$env:DB_USERNAME="crediya"
$env:DB_PASSWORD="crediya"
docker compose up -d
```

Autenticación (terminal 1):

```powershell
cd C:\Users\juanm\Crediya\crediya-autenticacion-ms
$env:DB_USERNAME="crediya"; $env:DB_PASSWORD="crediya"
.\gradlew.bat clean build
.\gradlew.bat :app-service:bootRun
```

Solicitudes (terminal 2):

```powershell
cd C:\Users\juanm\Crediya\crediya-solicitudes-ms
$env:DB_USERNAME="crediya"; $env:DB_PASSWORD="crediya"
.\gradlew.bat clean build
.\gradlew.bat :app-service:bootRun
```

Salud de cada servicio: `http://localhost:8080/actuator/health` y `http://localhost:8081/actuator/health`.
