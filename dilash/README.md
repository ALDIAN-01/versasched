# Dilash - Sistema de Agendamiento de Citas

## Descripción del Proyecto

Dilash es un sistema web de gestión de citas diseñado para un negocio de belleza en el hogar. Permite la autenticación de usuarios, el agendamiento de citas basado en la disponibilidad de horarios, y la gestión del historial de clientes. La aplicación está construida siguiendo el patrón arquitectónico MVC (Modelo-Vista-Controlador), utilizando tecnologías modernas como Spring Boot para el backend, Thymeleaf para las vistas, y Bootstrap para el diseño responsivo. La base de datos utilizada es Microsoft SQL Server.

El sistema soporta dos tipos de usuarios principales:
- **Clientes**: Pueden registrarse, iniciar sesión, agendar citas, ver su historial y gestionar su perfil.
- **Administradores**: Gestionan los servicios, horarios, citas, y tienen acceso a paneles de administración para supervisar y modificar el estado de las citas.

## Tecnologías Utilizadas

- **Backend**:
  - Java 17
  - Spring Boot 3.x
  - Spring Data JPA (para acceso a datos)
  - Spring Security (para autenticación y autorización)
  - Thymeleaf (motor de plantillas para vistas)
  - JDBC (para procedimientos almacenados)

- **Frontend**:
  - HTML5
  - CSS3 (con Bootstrap 5)
  - JavaScript (jQuery para interactividad)

- **Base de Datos**:
  - Microsoft SQL Server

- **Herramientas de Desarrollo**:
  - Maven (gestión de dependencias y construcción)
  - Git (control de versiones)

## Requisitos del Sistema

- JDK 17 o superior
- Maven 3.6+
- Microsoft SQL Server (local o remoto)
- Navegador web moderno (Chrome, Firefox, Edge)

## Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/ALDIAN-01/dilash_corregido.git
cd dilash_corregido/dilash
```

### 2. Configurar la Base de Datos
- Asegúrate de tener Microsoft SQL Server instalado y ejecutándose.
- Crea una base de datos llamada `dilash_db` (o el nombre que prefieras).
- Ejecuta los scripts SQL para crear las tablas y procedimientos almacenados (ubicados en `src/main/resources/sql/` si existen, o configura manualmente según el esquema).

### 3. Configurar la Conexión a la Base de Datos
Edita el archivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=dilash_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

### 4. Construir y Ejecutar la Aplicación
```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`.

## Estructura del Proyecto

```
dilash/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── dilash/
│   │   │       ├── DilashApplication.java          # Clase principal de Spring Boot
│   │   │       ├── controller/                     # Controladores (Capa de presentación)
│   │   │       │   ├── AgendarController.java      # Gestión de agendamiento de citas
│   │   │       │   ├── CitaController.java         # Gestión de citas
│   │   │       │   ├── ClienteController.java      # Panel de cliente
│   │   │       │   ├── LoginController.java        # Autenticación
│   │   │       │   ├── PerfilController.java       # Gestión de perfil de usuario
│   │   │       │   ├── RegistroController.java     # Registro de nuevos usuarios
│   │   │       │   ├── ServicioController.java     # Gestión de servicios
│   │   │       │   └── VistaController.java        # Vistas administrativas
│   │   │       ├── model/                          # Modelos de datos (Entidades JPA)
│   │   │       │   ├── Cita.java                   # Entidad Cita
│   │   │       │   ├── Cliente.java                # Entidad Cliente
│   │   │       │   ├── DetalleCita.java            # Entidad DetalleCita
│   │   │       │   ├── EstadoCita.java             # Entidad EstadoCita
│   │   │       │   ├── Horario.java                # Entidad Horario
│   │   │       │   ├── Servicio.java               # Entidad Servicio
│   │   │       │   ├── Usuario.java                # Entidad Usuario
│   │   │       │   └── CategoriaServicio.java      # Entidad CategoriaServicio
│   │   │       ├── repository/                     # Repositorios (Acceso a datos)
│   │   │       │   ├── CitaRepository.java         # Repositorio para Citas
│   │   │       │   ├── DetalleCitaRepository.java  # Repositorio para Detalles de Cita
│   │   │       │   ├── EstadoCitaRepository.java   # Repositorio para Estados de Cita
│   │   │       │   ├── HorarioRepository.java      # Repositorio para Horarios
│   │   │       │   ├── ServicioRepository.java     # Repositorio para Servicios
│   │   │       │   └── UsuarioRepository.java      # Repositorio para Usuarios
│   │   │       └── service/                        # Servicios (Lógica de negocio)
│   │   │           ├── CitaService.java            # Servicio para lógica de citas
│   │   │           ├── PerfilService.java          # Servicio para gestión de perfiles
│   │   │           ├── ClienteService.java         # Servicio para clientes
│   │   │           └── AdminService.java           # Servicio para administración
│   │   └── resources/
│   │       ├── application.properties              # Configuración de la aplicación
│   │       ├── static/                             # Recursos estáticos (CSS, JS, imágenes)
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   ├── img/
│   │       │   └── js/
│   │       │       └── script.js
│   │       └── templates/                          # Plantillas Thymeleaf
│   │           ├── index.html                      # Página principal
│   │           ├── login.html                      # Página de login
│   │           ├── registro.html                   # Página de registro
│   │           ├── agendar.html                    # Página de agendamiento
│   │           ├── cliente.html                    # Panel de cliente
│   │           ├── admin.html                      # Panel de administrador
│   │           ├── perfil.html                     # Gestión de perfil
│   │           ├── faq-section.html                # Sección de preguntas frecuentes
│   │           └── fragments.html                  # Fragmentos reutilizables
│   └── test/
│       └── java/
│           └── dilash/
│               └── DilashApplicationTests.java     # Pruebas unitarias
├── pom.xml                                         # Archivo de configuración de Maven
├── mvnw                                            # Wrapper de Maven para Windows
├── mvnw.cmd                                        # Wrapper de Maven para Unix
├── HELP.md                                         # Documentación adicional
└── README.md                                        # Este archivo
```

## Funcionalidades Principales

### Para Clientes
- **Registro y Login**: Creación de cuenta y autenticación segura.
- **Agendamiento de Citas**: Selección de servicios, fechas y horarios disponibles.
- **Gestión de Perfil**: Actualización de datos personales y cambio de contraseña.
- **Historial de Citas**: Visualización de citas pasadas y futuras.

### Para Administradores
- **Gestión de Servicios**: Creación, edición y eliminación de servicios ofrecidos.
- **Gestión de Horarios**: Configuración de horarios disponibles.
- **Panel de Administración**: Vista general de todas las citas, con opciones para cambiar estados, reagendar o cancelar.
- **Gestión de Usuarios**: Supervisión de clientes registrados.

## Arquitectura del Sistema

El proyecto sigue el patrón **MVC (Modelo-Vista-Controlador)**:

- **Modelo**: Representa los datos y la lógica de negocio. Incluye las entidades JPA y los servicios.
- **Vista**: Maneja la presentación de datos al usuario. Utiliza Thymeleaf para renderizar HTML dinámico.
- **Controlador**: Gestiona las solicitudes del usuario, coordina con los servicios y selecciona las vistas apropiadas.

Además, se implementa una **separación de capas**:
- **Controladores**: Solo manejan el flujo HTTP, validaciones básicas y redirecciones.
- **Servicios**: Contienen la lógica de negocio, validaciones complejas y orquestación de operaciones.
- **Repositorios**: Abstraen el acceso a la base de datos mediante JPA.

## Base de Datos

La aplicación utiliza Microsoft SQL Server. La conexión se configura en `application.properties`. Las entidades JPA mapean directamente a las tablas de la base de datos.

- **Tablas principales**:
  - `Usuario`: Información de usuarios (clientes y admins).
  - `Cita`: Citas agendadas.
  - `Servicio`: Servicios disponibles.
  - `Horario`: Horarios de disponibilidad.
  - `EstadoCita`: Estados posibles de las citas (pendiente, confirmada, cancelada, etc.).
  - `DetalleCita`: Detalles específicos de cada cita (servicios seleccionados).

Se utilizan procedimientos almacenados para operaciones complejas, accedidos vía JDBC.

## Contribución

Este proyecto fue desarrollado como parte de un curso de Desarrollo Web. Para contribuciones, por favor contacta al autor.

## Autor

- **Nombre**: [Tu Nombre]
- **Correo**: [tu.email@ejemplo.com]
- **GitHub**: [https://github.com/ALDIAN-01]

## Licencia

Este proyecto es de uso educativo y no tiene licencia específica.

