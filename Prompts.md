
- **Herramienta:** ChatGPT
- **Prompt:** Estoy haciendo un sistema de reservas de hotel con Spring Boot. En un proyecto anterior usé una entidad Role con una relación ManyToMany hacia Usuario, pero en este sistema solo existen tres roles fijos (Administrador, Recepcionista y Huésped). ¿Es mejor volver a usar una entidad Role o simplemente un enum? Explícame ventajas y desventajas pensando en un proyecto académico.
- **Qué generó la IA:** Recomendó un enum `RolUsuario` (`ADMINISTRADOR`, `RECEPCIONISTA`, `HUESPED`) en vez de una entidad `Role` con `@ManyToMany`, con la justificación de que la relación M:N solo se justifica cuando los roles son dinámicos/configurables en tiempo de ejecución; acá son 3 roles fijos y cerrados, así que el enum es más simple sin perder claridad, y `Usuario.getAuthorities()` puede construirse igual devolviendo `"ROLE_" + rol.name()`.
- **Qué se corrigió:** Se aceptó tal cual.
- 
- **Prompt:** Tengo una duda con la entidad Usuario. Solo los recepcionistas pertenecen a una sucursal, mientras que los administradores y huéspedes no. ¿Es correcto dejar la relación con Sucursal como nullable o es una mala práctica? ¿Cómo podría validar después que únicamente los recepcionistas tengan una sucursal asignada?
- **Qué generó la IA:** Confirmó que `@ManyToOne` nullable es correcto en este caso (no es mala práctica per se, es una relación condicional legítima al dominio), y propuso que la validación de "RECEPCIONISTA debe traer sucursalId" viva en `UsuarioService.registrarInterno()`, lanzando `BusinessRuleException` si el rol es RECEPCIONISTA y `sucursalId` viene nulo.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Para las reservas estaba pensando crear una entidad Huésped aparte, pero también podría reutilizar la entidad Usuario y diferenciarlo por el rol. ¿Cuál opción hace el modelo más simple sin perder claridad?
- **Qué generó la IA:** Recomendó reutilizar `Usuario` (un huésped *es* un `Usuario` con `rol = HUESPED`), evitando una entidad `Huesped` redundante y una relación 1:1 innecesaria. `Reserva.huesped` apunta directo a `Usuario`.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** En mis proyectos anteriores he usado UUID como llave primaria. ¿Tiene sentido seguir usando UUID en un sistema de reservas o sería mejor Long autoincremental? También quiero saber si BigDecimal sigue siendo la mejor opción para guardar precios.
- **Qué generó la IA:** Mantuvo UUID (`GenerationType.UUID`) en las 4 entidades principales por consistencia con proyectos anteriores del curso, y `BigDecimal` (no `double`/`float`) para `Habitacion.precio`, evitando errores de redondeo en dinero.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Tengo un UsuarioDTORequest que recibe la contraseña. Cuando responda el usuario registrado, ¿es buena práctica reutilizar ese mismo DTO o debería crear un DTOResponse separado? No quiero exponer información sensible por accidente.
- **Qué generó la IA:** Recomendó crear `UsuarioDTOResponse` separado, que nunca incluye el campo `password` — ni siquiera codificado. No confiar en anotaciones como `@JsonProperty(WRITE_ONLY)` como única defensa, porque un cambio futuro accidental en el DTO podría filtrar el hash.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Estoy haciendo los Mapper estáticos. Tengo una duda: ¿el PasswordEncoder debería usarse dentro del Mapper o es mejor que el Service envíe la contraseña ya codificada? Quiero respetar la separación de responsabilidades.
- **Qué generó la IA:** Recomendó mantener el Mapper 100% estático y sin dependencias de Spring (sin inyectar `PasswordEncoder` ahí). `UsuarioService` codifica la contraseña primero y se la pasa a `UsuarioMapper.toEntity()` ya como `encodedPassword`.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** En Habitación y Reserva estoy enviando únicamente los IDs de las entidades relacionadas (habitacionId, sucursalId, huespedId) en lugar de objetos completos. ¿Es una buena práctica en una API REST o sería mejor enviar objetos anidados?
- **Qué generó la IA:** Confirmó que enviar solo IDs es el patrón preferido en REST (más liviano, evita sobre-anidamiento y ambigüedad de qué se debe actualizar en cascada); el Service se encarga de resolver esos IDs contra el repositorio antes de pasarlos al Mapper.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Quiero validar los DTO con Bean Validation usando @NotBlank, @NotNull, @Future y @DecimalMin. ¿Conviene poner todas las validaciones en los DTO o parte de las reglas deberían quedar en los servicios?
- **Qué generó la IA:** Propuso dividir en dos capas: Bean Validation en los DTO para validaciones de **formato/presencia** (campo obligatorio, fecha futura, número positivo), y `BusinessRuleException` en el service para reglas que dependen del **estado de la base de datos** (username duplicado, habitación no disponible, fechas cruzadas con otra reserva).
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Mi proyecto ya tiene spring-boot-starter-security porque así venía el esqueleto del proyecto, pero todavía no voy a implementar JWT. ¿Hay alguna forma de permitir todas las peticiones temporalmente para poder desarrollar primero los CRUD?
- **Qué generó la IA:** Explicó que sin una `SecurityConfiguration` propia, Spring Boot autogenera un password random y bloquea todo con Basic Auth apenas detecta `spring-boot-starter-security` en el classpath. Generó una `SecurityConfiguration` temporal con `.anyRequest().permitAll()`, dejando comentado que se reemplazaría al llegar a JWT.
- **Qué se corrigió:** Se aceptó tal cual; se verificó corriendo el proyecto que el prompt de password autogenerado ya no bloqueaba las peticiones.

- **Prompt:** Si todavía no existe autenticación JWT, ¿vale la pena crear desde ahora el bean PasswordEncoder o debería esperar hasta implementar el login?
- **Qué generó la IA:** Recomendó crearlo desde ya (junto con la `SecurityConfiguration` temporal), porque `UsuarioService.registrar()` ya necesitaba codificar contraseñas antes de guardarlas, independientemente de si el login con JWT ya existía o no.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** La rúbrica solo dice que la habitación tiene disponibilidad. ¿Crees que debería implementar disponibilidad por rango de fechas o basta con un campo booleano disponible? No quiero agregar complejidad que después no se evalúe.
- **Qué generó la IA:** Recomendó el campo booleano simple, citando textualmente que el README describe la habitación como "tiene tipo, precio y disponibilidad" sin mencionar rangos de fechas — decisión consciente de no sobrecomplicar algo que la rúbrica no pide, con el trade-off explícito de que esto bloquea reservas futuras en paralelo para la misma habitación.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Cuando un huésped haga una reserva usando JWT, ¿es seguro recibir el huespedId desde el body o es mejor ignorarlo y obtener el usuario autenticado desde el token?
- **Qué generó la IA:** Señaló que confiar en `huespedId` del body permitiría que un huésped autenticado creara reservas a nombre de otro usuario. Implementó en `ReservaService.crear()` que, si el rol autenticado es `HUESPED`, se ignora `dto.huespedId()` y se fuerza el id del usuario del token; solo ADMIN/RECEPCIONISTA pueden usar el valor del body (para reservar a nombre de un huésped en el mostrador).
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Estoy usando una excepción BusinessRuleException para las reglas de negocio. ¿Qué código HTTP sería más adecuado devolver, 400 o 409? ¿En qué casos conviene cada uno?
- **Qué generó la IA:** Explicó que 409 (Conflict) típicamente se reserva para conflictos de concurrencia/estado (ej. edición simultánea), mientras que 400 es más apropiado para violaciones de reglas de negocio expresadas en la petición misma (username duplicado, fechas inválidas). Se estandarizó `BusinessRuleException` → siempre 400 en todo el proyecto.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Para confirmar o cancelar una reserva solamente cambia el estado. ¿Es más correcto usar PUT o PATCH? Quiero seguir buenas prácticas REST.
- **Qué generó la IA:** Recomendó `PATCH` (`/reservas/{id}/confirmar`, `/reservas/{id}/cancelar`) porque `PUT` implica reemplazar el recurso completo, mientras que estas operaciones son transiciones de estado parciales — mismo criterio ya usado en un proyecto anterior del curso (API de RRHH).
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Estoy preparando el Dockerfile del proyecto. ¿Vale la pena usar un Dockerfile multi-stage con Gradle o para un proyecto pequeño no hace diferencia?
- **Qué generó la IA:** Recomendó multi-stage (etapa 1 con JDK+Gradle para compilar el `.jar`, etapa 2 solo con JRE para correrlo) — la imagen final no arrastra el JDK completo ni el código fuente, resultando en una imagen más liviana y más cercana a cómo se haría en un entorno real, aunque el proyecto sea pequeño.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Cuando la API y PostgreSQL arrancan juntos con docker-compose, algunas veces la API intenta conectarse antes de que la base de datos esté lista. ¿Cuál es la forma correcta de evitar ese problema?
- **Qué generó la IA:** Agregó un `healthcheck` (`pg_isready`) al servicio `db` y `depends_on: db: condition: service_healthy` en el servicio `api`, para que Docker Compose espere a que Postgres esté realmente listo para aceptar conexiones antes de arrancar la API (no solo que el contenedor haya iniciado).
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** ¿Es buena práctica dejar el JWT Secret directamente en application.yaml o debería usar variables de entorno dentro de Docker Compose?
- **Qué generó la IA:** Recomendó parametrizar con `${JWT_SECRET:valor_por_defecto}` en `application.yaml`, sobreescribible por variable de entorno en `docker-compose.yml`, con un valor de ejemplo en `.env.example` (nunca committeado el `.env` real).
- **Qué se corrigió:** Al revisar después por qué fallaba el job de Gitleaks en el pipeline, se detectó que ese "valor por defecto" hardcodeado en `application.yaml` y `docker-compose.yml` es en sí mismo un secreto de alta entropía, y el propio escáner lo marcaba como filtración. Corrección pendiente: quitar el fallback hardcodeado y hacer que la app falle explícitamente si `JWT_SECRET` no está seteado, dejando el valor de ejemplo únicamente en `.env.example` con excepción en la config de Gitleaks.

- **Prompt:** La rúbrica pide que falle el pipeline si existen vulnerabilidades críticas o secretos expuestos. ¿Qué herramientas gratuitas me recomendarías para hacerlo en GitHub Actions?
- **Qué generó la IA:** Recomendó el plugin Gradle de OWASP Dependency-Check (`failBuildOnCVSS = 9.0`, umbral estándar de severidad "Crítica") para vulnerabilidades de dependencias, y Gitleaks para secretos — corrido como imagen Docker directa (`zricethezav/gitleaks:latest`) en vez de la Action de marketplace, para evitar depender de sus términos de licenciamiento.
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** ¿Es mejor tener un solo workflow con todas las tareas o dividir el pipeline en varios jobs independientes? ¿Qué ventajas tendría cada opción?
- **Qué generó la IA:** Recomendó 3 jobs independientes (`build-and-test`, `vulnerabilidades-criticas`, `secretos-expuestos`) en el mismo workflow, para que cada falla se identifique por separado en la pestaña Actions, y para que el escaneo de secretos no tenga que esperar a que termine el build (puede correr en paralelo).
- **Qué se corrigió:** Se aceptó tal cual.

- **Prompt:** Para el rol Recepcionista debo impedir que modifique reservas de otras sucursales. ¿Basta con proteger los endpoints usando roles o necesito validar también la sucursal dentro del servicio? ¿Por qué?
- **Qué generó la IA:** Explicó que `@PreAuthorize("hasRole('RECEPCIONISTA')")` solo verifica el rol, no puede comparar la sucursal del usuario contra la sucursal del recurso puntual sin cargarlo primero de la base de datos — por eso esa comparación no puede vivir en la anotación, tiene que hacerse a nivel de servicio. Implementó `AutorizacionRecursoService.verificarSucursalDeRecepcionista()`, invocado desde `HabitacionService` y `ReservaService` antes de cualquier escritura.
- **Qué se corrigió:** Se aceptó tal cual.
