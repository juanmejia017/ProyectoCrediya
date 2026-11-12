# CrediYa — Microservicio de Autenticación (`crediya-autenticacion-ms`)

Microservicio **imperativo (Spring MVC + JPA)** construido con Clean Architecture (scaffold de Bancolombia, `reactive=false`).

## Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

# Cómo ejecutar el microservicio de autenticación

## Requisitos

- Java 25
- Docker Desktop (para la base de datos PostgreSQL)

## Variables de entorno

| Variable      | Descripción                     | Valor por defecto                              |
|---------------|---------------------------------|------------------------------------------------|
| `DB_URL`      | URL JDBC de PostgreSQL          | `jdbc:postgresql://localhost:5432/crediya_db`  |
| `DB_USERNAME` | Usuario de la base de datos     | *(obligatoria)*                                |
| `DB_PASSWORD` | Contraseña de la base de datos  | *(obligatoria)*                                |

## Pasos (PowerShell)

```powershell
$env:DB_USERNAME="crediya"
$env:DB_PASSWORD="crediya"

docker compose up -d            # desde la raíz del repositorio
cd crediya-autenticacion-ms
.\gradlew.bat clean build
.\gradlew.bat :app-service:bootRun
```

## Endpoints

### `POST /api/v1/usuarios` — HU 1: Registrar usuarios

Campos obligatorios: `nombres`, `apellidos`, `correoElectronico`, `salarioBase` (entre 0 y 15.000.000).
Campos opcionales validados si se envían: `fechaNacimiento` (pasada), `telefono` (7–15 dígitos), `documentoIdentidad` (5–20 alfanuméricos).

| Código | Cuándo                                                        |
|--------|---------------------------------------------------------------|
| 201    | Usuario registrado (header `Location` con la URL del recurso) |
| 400    | Datos inválidos o JSON mal formado                            |
| 409    | Correo o documento de identidad ya registrados                |
| 415    | El `Content-Type` no es `application/json`                    |
| 500    | Error inesperado (mensaje genérico, detalle solo en el log)   |
