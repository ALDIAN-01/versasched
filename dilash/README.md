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
  - JavaScript (vanilla JS para interactividad)

- **Base de Datos**:
  - Microsoft SQL Server

- **Herramientas de Desarrollo**:
  - Maven (gestión de dependencias y construcción)
  - Git (control de versiones)

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

Se utilizan procedimientos almacenados y triggers para operaciones complejas, accedidos vía JDBC.

## Cambios recientes

- **Hashing de contraseñas (bcrypt):** Ahora las contraseñas de los usuarios se guardan usando `BCrypt` (vía `spring-security-crypto`). Las clases modificadas incluyen `UsuarioService` y `PerfilService`, y se añadió `SecurityConfig` con un `PasswordEncoder`.
- **Compatibilidad retroactiva:** Si existen usuarios con contraseñas en texto plano, al iniciar sesión por primera vez su contraseña se convertirá automáticamente a hash seguro.
- **Términos y condiciones:** Se añadió una checkbox obligatoria en `registro.html` y una nueva vista pública `/terminos` con el texto de términos y condiciones; el backend valida que el usuario acepte antes de crear la cuenta.
- **Limpieza de frontend:** Se separaron scripts en archivos externos (`/js/common.js`, `/js/registro.js`, `/js/login.js`, `/js/perfil.js`, `/js/agendar.js`, `/js/admin.js`) y se corrigieron estilos duplicados de autentificación con el nuevo scope `.auth-page`.


## Info

Este proyecto fue desarrollado como parte de mi curso de Desarrollo Web en mi quinto semestre estudiando Ingeniería de Software en el Tecnológico de Antioquia.

## Autor

- **Nombre**: Aldian Oropeza
- **Correo**: aldian.oropeza@correo.tdea.edu.co
- **GitHub**: https://github.com/ALDIAN-01

