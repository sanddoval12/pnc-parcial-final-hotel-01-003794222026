# Sistema de Reservas de Hotel — API

API backend N-Capas para un sistema de reservas de una cadena hotelera con varias
sucursales, con autenticación JWT (Access + Refresh Token), autorización por rol y
por sucursal, Docker y CI/CD con GitHub Actions.

> Este proyecto corresponde al Parcial Final de la Unidad de Seguridad. Las
> instrucciones originales del profesor están en `ENUNCIADO.md`.

---

## 1. Cómo levantar el proyecto

### Requisitos
- Docker y Docker Compose instalados. Nada más — ni Java, ni Postgres, ni Gradle
  necesitan estar instalados localmente.

### Pasos

```bash
# 1. (Opcional) copia el archivo de variables de entorno de ejemplo
cp .env.example .env

# 2. Levanta todo (API + base de datos) con un solo comando
docker-compose up
```

Con eso:
- Postgres queda escuchando en `localhost:5432` (usuario `postgres`, password `admin`,
  base de datos `hotel_reservas_db`).
- La API queda escuchando en `http://localhost:8080`.
- Las tablas se crean solas al arrancar (`ddl-auto: update`), no hace falta correr
  ningún script SQL a mano.

Para bajar todo: `docker-compose down` (agrega `-v` si además quieres borrar los datos
de Postgres).

### Desarrollo local sin Docker (opcional)
Si prefieres correr la API directo con Gradle mientras solo Postgres corre en Docker:

```bash
docker-compose up db
./gradlew bootRun
```

---

## 2. Arquitectura (N-Capas)

```
com.uca.pncparcialfinalhotel/
├── entities/          Capa de dominio (JPA): Sucursal, Habitacion, Usuario, Reserva, RefreshToken
├── repository/         Acceso a datos: interfaces JpaRepository
├── dto/                 Contratos de entrada/salida (request/response), desacoplados de las entidades
├── service/             Lógica de negocio: reglas, validaciones, orquestación de repositorios
├── controller/          Capa de presentación: endpoints REST, delega todo al service
├── security/            JWT: generación/validación de tokens, filtro de autenticación
├── configuration/        Configuración de Spring Security
├── exception/            Excepciones de negocio + manejo centralizado de errores
└── utils/                Mappers estáticos Entity ↔ DTO
```

- **Presentación** = `controller/` (nunca contiene lógica de negocio, solo arma la
  respuesta y delega).
- **Lógica de negocio** = `service/` (aquí viven las reglas: duplicados, disponibilidad,
  fechas, y la regla de autorización por sucursal).
- **Acceso a datos** = `repository/` + `entities/`.

---

## 3. Roles y autorización

| Rol | Permisos |
|---|---|
| `ADMINISTRADOR` | Acceso total: gestiona sucursales, habitaciones, usuarios y reservas de todas las sucursales. |
| `RECEPCIONISTA` | Gestiona reservas y disponibilidad de habitaciones, **únicamente de la sucursal a la que pertenece**. |
| `HUESPED` | Solo puede crear, ver y cancelar **sus propias** reservas. |

El registro público (`POST /api/hotel/usuarios/register`) siempre crea usuarios con rol
`HUESPED`, sin importar qué rol venga en el body — es una decisión de seguridad
intencional (ver `REFLEXION.md`). Un `ADMINISTRADOR` autenticado es el único que puede
crear otros administradores o recepcionistas, vía `POST /api/hotel/usuarios`.

### Regla de negocio no trivial implementada: Opción B — Autorización por atributo (sucursal)

Un `RECEPCIONISTA` con el rol correcto NO puede automáticamente confirmar, editar o
cancelar cualquier reserva/habitación: el sistema compara la sucursal asignada al
usuario autenticado contra la sucursal del recurso puntual (habitación o reserva) que
está intentando tocar. Si no coinciden, la API responde `403 Forbidden`.

Esta lógica vive en `service/AutorizacionRecursoService.java` y se aplica desde
`HabitacionService` y `ReservaService` antes de cualquier operación de escritura.
`@PreAuthorize` solo valida el ROL (¿es RECEPCIONISTA?); la sucursal específica se
valida aparte, a nivel de servicio, porque `@PreAuthorize` no tiene forma de comparar
la sucursal del usuario contra la sucursal del recurso sin cargarlo primero de la base
de datos.

---

## 4. Autenticación JWT

- `POST /api/hotel/auth/login` — recibe `username`/`password`, devuelve `accessToken`
  (expira en 15 min) y `refreshToken` (expira en 7 días).
- `POST /api/hotel/auth/refresh` — recibe el `refreshToken`, devuelve un `accessToken`
  nuevo (el refresh token se reutiliza hasta que expire).
- Todas las demás rutas requieren `Authorization: Bearer <accessToken>`.

---

## 5. Endpoints principales

| Método | Ruta | Rol requerido |
|---|---|---|
| POST | `/api/hotel/auth/login` | Público |
| POST | `/api/hotel/auth/refresh` | Público |
| POST | `/api/hotel/usuarios/register` | Público (siempre crea HUESPED) |
| POST | `/api/hotel/usuarios` | ADMINISTRADOR |
| POST/PUT/DELETE | `/api/hotel/sucursales/**` | ADMINISTRADOR |
| GET | `/api/hotel/sucursales/**` | Cualquier autenticado |
| POST/DELETE | `/api/hotel/habitaciones` | ADMINISTRADOR |
| PUT | `/api/hotel/habitaciones/{id}` | ADMINISTRADOR o RECEPCIONISTA (de su sucursal) |
| GET | `/api/hotel/habitaciones/**` | Cualquier autenticado |
| POST | `/api/hotel/reservas` | HUESPED, RECEPCIONISTA o ADMINISTRADOR |
| GET | `/api/hotel/reservas` (todas) | ADMINISTRADOR |
| GET | `/api/hotel/reservas/huesped/{id}` | El propio HUESPED o ADMINISTRADOR |
| GET | `/api/hotel/reservas/sucursal/{id}` | RECEPCIONISTA (de esa sucursal) o ADMINISTRADOR |
| PATCH | `/api/hotel/reservas/{id}/confirmar` | RECEPCIONISTA (de esa sucursal) o ADMINISTRADOR |
| PATCH | `/api/hotel/reservas/{id}/cancelar` | El propio HUESPED, RECEPCIONISTA (de esa sucursal) o ADMINISTRADOR |

---

## 6. Documentación de evidencia de uso de IA

- `PROMPTS.md` — bitácora de prompts usados durante el desarrollo.
- `REFLEXION.md` — reflexión sobre el proceso, aciertos y errores de la IA.
