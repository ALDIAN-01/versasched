# Explicación de los cambios recientes

Este documento explica las modificaciones implementadas recientemente en el proyecto, por qué se hicieron y cómo utilizarlas.

## Resumen técnico

- Se agregó hashing de contraseñas usando `BCrypt` a través de la dependencia `spring-security-crypto`.
- Se creó `SecurityConfig` con un bean `PasswordEncoder` y se actualizó la lógica de registro e inicio de sesión para usar el encoder.
- `UsuarioService` ahora valida con `passwordEncoder.matches(...)` y migrará contraseñas en texto plano durante el primer login exitoso.
- `PerfilService` valida la contraseña actual contra el hash y guarda la nueva contraseña hasheada.
- Se añadió validación de aceptación de términos en el frontend (`registro.html`) y en el backend (`RegistroController`). Además se creó la vista `terminos.html`.
- Se externalizó la lógica de cliente desde plantillas a archivos JS en `src/main/resources/static/js/` y se corrigió el scope de estilos de autenticación en `style.css` para evitar que los estilos de login/registro afecten otras páginas.

## Archivos modificados / añadidos

- `pom.xml`: se añadió `spring-security-crypto`.
- `src/main/java/dilash/config/SecurityConfig.java`: nuevo bean `PasswordEncoder`.
- `src/main/java/dilash/service/UsuarioService.java`: autenticación y registro con bcrypt.
- `src/main/java/dilash/service/PerfilService.java`: cambio de contraseña con bcrypt.
- `src/main/resources/templates/registro.html`: checkbox de términos y validación JS.
- `src/main/resources/templates/terminos.html`: nueva página de términos y condiciones.
- `src/main/java/dilash/controller/RegistroController.java`: validación de aceptación de términos y ruta `/terminos`.

## Recomendaciones para publicar en GitHub

1. Nunca subas `src/main/resources/application.properties` si contiene credenciales reales.
2. Crea `application.properties.example` con valores ficticios y sube sólo ese archivo.
3. Añade a `.gitignore` la línea:

```
src/main/resources/application.properties
```

4. Si ya subiste credenciales, cámbialas inmediatamente y elimina el archivo del historial con herramientas como `git filter-repo` o BFG.

## Cómo ejecutar localmente (variables de entorno)

Configura las variables de entorno antes de ejecutar la aplicación (ejemplo Windows PowerShell):

```powershell
$env:DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=DilashDB_Test;encrypt=true;trustServerCertificate=true'
$env:DB_USER = 'tu_usuario'
$env:DB_PASS = 'tu_contraseña_segura'
./mvnw.cmd spring-boot:run
```

En `application.properties` puedes usar referencias a variables:

```
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

## Notas legales y buenas prácticas

- Añadir los términos y condiciones en el flujo de registro mejora el cumplimiento y la transparencia.
- Este texto de `terminos.html` es un ejemplo: para un producto real, valida el contenido con asesoría legal.

## Preguntas frecuentes rápidas

- ¿Mis usuarios existentes perderán acceso? No — si su contraseña estaba en texto plano, podrán iniciar sesión y su contraseña será migrada a hash automáticamente.
- ¿Qué hacer si ya subí la contraseña al repo? Revoca la credencial y limpia el historial.

---

Si quieres, actualizo `application.properties` para usar variables de entorno y creo `application.properties.example` automáticamente. ¿Lo hago? 
# Explicación del Proyecto Dilash - Guía para Principiantes

¡Hola! Si estás leyendo esto, probablemente eres alguien que está viendo este código por primera vez y quieres entender cómo funciona el proyecto Dilash. No te preocupes, voy a explicarlo todo paso a paso, como si estuviéramos charlando. Imagina que soy tu amigo programador explicándote un proyecto que hice. Vamos a desglosarlo todo para que quede clarito.

## ¿Qué es Dilash?

Dilash es una aplicación web para agendar citas en un negocio de belleza en casa (como una peluquería o salón de belleza). Es como una agenda digital donde los clientes pueden reservar turnos para servicios como cortes de pelo, manicuras, etc., y el administrador puede gestionar todo.

**¿Por qué se llama Dilash?** Bueno, es solo un nombre creativo. Podría ser una abreviatura o algo inventado.

## ¿Cómo funciona la aplicación en general?

La aplicación tiene dos tipos de usuarios:
- **Clientes**: Personas que quieren agendar citas.
- **Administradores**: La persona que maneja el negocio, ve todas las citas, cambia estados, etc.

Los clientes pueden:
- Registrarse y loguearse.
- Ver servicios disponibles.
- Elegir fecha y hora para una cita.
- Ver sus citas pasadas y futuras.
- Cambiar su perfil (datos personales, contraseña).

Los administradores pueden:
- Ver todas las citas de todos los clientes.
- Cambiar el estado de las citas (por ejemplo, de "pendiente" a "confirmada").
- Agregar o editar servicios.
- Gestionar horarios disponibles.

Todo esto se hace a través de una página web, sin necesidad de instalar nada en el teléfono o computadora del usuario.

## Tecnologías usadas (¿Qué herramientas se usaron?)

Para hacer esta aplicación, usé varias herramientas modernas de desarrollo web:

- **Java**: El lenguaje de programación principal. Es como el "idioma" en el que está escrito el código.
- **Spring Boot**: Un framework (conjunto de herramientas) que hace fácil crear aplicaciones web en Java. Es como un kit de construcción para apps.
- **Thymeleaf**: Para crear las páginas web. Convierte datos del backend en HTML que el navegador puede mostrar.
- **Bootstrap**: Para que las páginas se vean bonitas y funcionen bien en teléfonos y computadoras.
- **Microsoft SQL Server**: La base de datos, donde se guardan todos los datos (usuarios, citas, etc.).
- **Maven**: Una herramienta para manejar las "librerías" (códigos reutilizables) que necesita el proyecto.

**¿Por qué estas tecnologías?** Spring Boot es muy popular para apps Java porque simplifica mucho el trabajo. Thymeleaf es fácil de usar con Spring. SQL Server es una base de datos robusta y gratuita para desarrollo.

## Arquitectura del proyecto (¿Cómo está organizado el código?)

El proyecto sigue un patrón llamado **MVC** (Modelo-Vista-Controlador). Es como dividir la aplicación en tres partes que trabajan juntas:

1. **Modelo (Model)**: Los datos y la lógica del negocio. Aquí están las "reglas" de cómo funciona todo.
2. **Vista (View)**: Lo que ve el usuario. Las páginas HTML.
3. **Controlador (Controller)**: El "intermediario" que recibe las peticiones del usuario y decide qué hacer.

Además, hay otras capas para organizar mejor:

- **Servicio (Service)**: Lógica de negocio más compleja.
- **Repositorio (Repository)**: Para acceder a la base de datos.

Esto se llama **separación de responsabilidades**. Cada parte hace solo lo que le toca, para que el código sea más fácil de entender y mantener.

**¿Por qué MVC?** Es un patrón estándar en desarrollo web. Hace que el código sea ordenado y fácil de cambiar.

## Estructura de carpetas (¿Dónde está cada cosa?)

Vamos a ver la estructura del proyecto. Imagina que el proyecto es una casa:

- `src/main/java/dilash/`: Aquí está todo el código Java.
  - `DilashApplication.java`: La "puerta de entrada" de la app. Aquí arranca todo.
  - `controller/`: Los controladores. Reciben las peticiones web y dicen qué hacer.
  - `model/`: Los modelos. Clases que representan los datos (como Usuario, Cita).
  - `repository/`: Para hablar con la base de datos.
  - `service/`: La lógica del negocio.

- `src/main/resources/`: Archivos de configuración y plantillas.
  - `application.properties`: Configuración (como la conexión a la base de datos).
  - `templates/`: Las páginas HTML (vistas).
  - `static/`: Archivos estáticos como CSS, JS, imágenes.

- `pom.xml`: Archivo que dice qué librerías usar (como las dependencias de Maven).

## ¿Cómo se conecta a la base de datos?

La base de datos es Microsoft SQL Server. Para conectarse, usamos algo llamado **JPA** (Java Persistence API), que es una forma estándar de trabajar con bases de datos en Java.

En `application.properties`, ponemos la información de conexión:
```
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=dilash_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

Esto le dice a la app dónde está la base de datos y cómo acceder.

Las tablas principales son:
- `Usuario`: Para clientes y admins.
- `Cita`: Las citas agendadas.
- `Servicio`: Los servicios disponibles (corte de pelo, etc.).
- `Horario`: Los horarios disponibles.
- Etc.

Usamos **repositorios** para hacer consultas a la base de datos. Por ejemplo, `UsuarioRepository` tiene métodos para buscar usuarios por email o guardar nuevos.

**¿Por qué SQL Server?** Es una base de datos profesional y fácil de usar en desarrollo. En producción, podría cambiarse a otra si es necesario.

## Decisiones tomadas en el desarrollo

Durante el desarrollo, tomé algunas decisiones importantes para hacer el código mejor:

1. **Separación de lógica**: Los controladores solo manejan las peticiones web y redirigen. Toda la lógica de negocio (validaciones, cálculos) está en los servicios. **¿Por qué?** Para que el código sea más limpio y fácil de probar.

2. **Uso de servicios**: Creé servicios como `CitaService`, `PerfilService`, etc., para agrupar la lógica relacionada. **¿Por qué?** Evita repetir código y hace que sea más fácil cambiar cosas.

3. **Validaciones en servicios**: Las validaciones (como "la contraseña debe tener al menos 8 caracteres") están en los servicios, no en los controladores. **¿Por qué?** Los controladores solo reciben datos y pasan al servicio. El servicio decide si es válido.

4. **Uso de Thymeleaf**: Para las vistas, en lugar de JSP o otros. **¿Por qué?** Es moderno y se integra bien con Spring Boot.

5. **Sesiones para autenticación**: Uso de sesiones HTTP para mantener al usuario logueado. **¿Por qué?** Es simple y seguro para una app web.

6. **Bootstrap para diseño**: Para que se vea bien en móviles. **¿Por qué?** Es gratis y fácil de usar.

## Explicación de cada parte del código

Vamos a ver qué hace cada archivo importante. Lo explico como si estuviéramos leyendo el código juntos.

### Controladores (controller/)

Estos son como los "recepcionistas" de la app. Reciben las peticiones del navegador y dicen qué hacer.

- `LoginController.java`: Maneja el login. Recibe email y contraseña, verifica si son correctos, y si sí, guarda al usuario en la sesión.
- `RegistroController.java`: Para nuevos usuarios. Recibe los datos del formulario y crea un nuevo usuario.
- `AgendarController.java`: Para agendar citas. Muestra los servicios disponibles y horarios, y cuando el usuario elige, llama al servicio para guardar la cita.
- `ClienteController.java`: El panel del cliente. Muestra sus citas.
- `PerfilController.java`: Para cambiar datos del perfil o contraseña.
- `VistaController.java`: Para el admin. Muestra todas las citas y permite cambiar estados.
- `CitaController.java`: Para ver detalles de citas.
- `ServicioController.java`: Para gestionar servicios (agregar, editar).

**¿Qué hacen exactamente?** Solo reciben datos del formulario, llaman a un servicio, y redirigen a otra página o muestran errores.

### Servicios (service/)

Aquí está la "inteligencia" de la app. Los controladores llaman a estos para hacer el trabajo real.

- `CitaService.java`: Maneja todo lo de citas. Verificar disponibilidad, guardar citas, obtener horarios libres.
- `PerfilService.java`: Para actualizar perfil o cambiar contraseña. Valida que la nueva contraseña sea segura.
- `ClienteService.java`: Obtiene las citas de un cliente.
- `AdminService.java`: Para el admin: obtener todas las citas, cambiar estados, etc.

**¿Por qué servicios?** Para no poner lógica en los controladores. Si cambias algo en un servicio, no afecta otros lugares.

### Modelos (model/)

Son las "plantillas" de los datos. Cada clase representa una tabla de la base de datos.

- `Usuario.java`: Tiene campos como nombre, email, contraseña, rol (cliente o admin).
- `Cita.java`: Fecha, hora, cliente, servicios, estado.
- `Servicio.java`: Nombre, precio, categoría.
- Etc.

Usan anotaciones de JPA para mapear a la base de datos.

### Repositorios (repository/)

Interfaces que extienden `JpaRepository`. Tienen métodos automáticos para guardar, buscar, etc.

- `UsuarioRepository.java`: Métodos para buscar usuarios por email, etc.

### Vistas (templates/)

Las páginas HTML que ve el usuario.

- `index.html`: Página principal.
- `login.html`: Formulario de login.
- `agendar.html`: Para elegir cita.
- `cliente.html`: Panel del cliente.
- `admin.html`: Panel del admin.
- Etc.

Usan Thymeleaf para mostrar datos dinámicos, como `th:text="${usuario.nombre}"`.

### Configuración

- `application.properties`: Configura la base de datos, puerto (8080), etc.
- `pom.xml`: Lista las dependencias (librerías) que necesita.

## ¿Cómo ejecutar el proyecto?

1. Instala Java 17 y Maven.
2. Instala SQL Server y crea la base de datos.
3. Configura `application.properties` con tus datos de BD.
4. Abre una terminal en la carpeta del proyecto.
5. Ejecuta `mvn spring-boot:run`.
6. Abre `http://localhost:8080` en el navegador.

¡Listo! La app debería funcionar.

## ¿Qué aprendí al hacer esto?

- **Separación de capas**: Hace el código más mantenible.
- **Validaciones**: Siempre validar datos del usuario.
- **Sesiones**: Para mantener el estado del usuario.
- **MVC**: Un buen patrón para apps web.
- **Pruebas**: Es importante probar cada parte.

Si tienes dudas, ¡pregunta! Este proyecto es un buen ejemplo de una app web completa con Java.