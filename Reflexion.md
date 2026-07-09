La IA ayudó bastante en la generación de la estructura inicial del proyecto, como las entidades, los DTO, los servicios CRUD y la configuración básica de Spring Security. También fueron útiles las recomendaciones de utilizar UUID como llave primaria, BigDecimal para los precios y separar los DTO de petición y respuesta para no exponer información sensible.
En Docker y GitHub Actions también generó una base funcional que después solo fue necesario adaptar al proyecto.

En algunos casos la IA proponía soluciones más complejas de lo necesario. Por ejemplo, sugería manejar los roles con una entidad Role y una relación ManyToMany, cuando para este proyecto bastaba un enum porque únicamente existen tres roles.
También propuso recibir siempre el huespedId desde el body al crear una reserva. Finalmente se decidió que, cuando el usuario autenticado es un huésped, ese valor se ignore y se utilice el ID obtenido del JWT para evitar que pueda crear reservas a nombre de otra persona.

Las respuestas de la IA se compararon con los requisitos que se pedia en la rúbrica y con la documentación de Spring Boot. Antes de aceptar una propuesta se verificó si realmente aportaba valor al proyecto o solo agregaba complejidad.
También se revisó que cada responsabilidad estuviera en la capa correspondiente, por ejemplo, dejando el PasswordEncoder dentro del servicio y no en el mapper.

El sistema no valida únicamente el rol del usuario. Cuando un recepcionista intenta modificar una reserva, también se compara la sucursal del usuario autenticado con la sucursal de la reserva. Si ambas coinciden, la operación se permite; de lo contrario, el sistema la rechaza.

Por lo que la IA fue una herramienta útil para acelerar el desarrollo y resolver dudas técnicas, pero fue necesario revisar cada propuesta antes de implementarla. En temas de seguridad entendí que no basta con que el código funcione, sino que también debe cumplir con las reglas del negocio y aplicar buenas prácticas para evitar posibles vulnerabilidades.
