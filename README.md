# 🐾 PetSalud — Sistema de Gestión de Clínica Veterinaria

> Proyecto educativo desarrollado con **Spring Boot** y **MySQL** para los ciclos formativos de
> **DAM** (Desarrollo de Aplicaciones Multiplataforma) y **DAW** (Desarrollo de Aplicaciones Web)
> de primer año de Formación Profesional.

---

## Tabla de contenidos

1. [¿Qué es PetSalud?](#1-qué-es-petsalud)
2. [Tecnologías utilizadas](#2-tecnologías-utilizadas)
3. [Objetivo funcional del sistema](#3-objetivo-funcional-del-sistema)
4. [Arquitectura de la aplicación](#4-arquitectura-de-la-aplicación)
5. [Estructura de paquetes y archivos](#5-estructura-de-paquetes-y-archivos)
6. [Análisis de la base de datos](#6-análisis-de-la-base-de-datos)
7. [Inicialización de la base de datos](#7-inicialización-de-la-base-de-datos)
8. [Levantar el entorno local (sin Docker)](#8-levantar-el-entorno-local-sin-docker)
9. [Despliegue con Docker](#9-despliegue-con-docker)
10. [Flujo completo de una funcionalidad](#10-flujo-completo-de-una-funcionalidad)
11. [Glosario de conceptos clave](#11-glosario-de-conceptos-clave)

---

## 1. ¿Qué es PetSalud?

**PetSalud** es una aplicación web para la gestión integral de una clínica veterinaria. Permite
registrar y administrar propietarios, mascotas, veterinarios, citas médicas, consultas clínicas y
el historial de vacunaciones, todo desde un navegador web.

Este proyecto está pensado para que los alumnos de primer año de DAM/DAW puedan estudiar de forma
práctica cómo se construye una aplicación web completa con Java, siguiendo buenas prácticas de
arquitectura y separación de responsabilidades.

---

## 2. Tecnologías utilizadas

| Tecnología | Versión | Rol en el proyecto |
|---|---|---|
| Java | 21 (LTS) | Lenguaje de programación principal |
| Spring Boot | 4.0.4 | Framework que arranca y configura toda la aplicación |
| Spring MVC | Incluido en Boot | Maneja las peticiones HTTP y el enrutado de URLs |
| Spring JDBC | Incluido en Boot | Ejecuta sentencias SQL contra la base de datos |
| Thymeleaf | Incluido en Boot | Motor de plantillas HTML del lado del servidor |
| Bean Validation | Incluido en Boot | Valida los datos que llegan desde los formularios |
| MySQL | 8.x | Base de datos relacional |
| Maven | 3.9 | Gestión de dependencias y ciclo de construcción |
| Bootstrap | 5 | Estilos y componentes visuales responsive |
| Bootstrap Icons | — | Iconografía vectorial |
| Docker + Compose | — | Empaquetado y despliegue en entornos de producción |

---

## 3. Objetivo funcional del sistema

Imagina que trabajas en una clínica veterinaria. Cada día llegan propietarios con sus mascotas,
los veterinarios atienden consultas, se recetan medicamentos y se aplican vacunas. PetSalud
digitaliza todo ese flujo:

```
Propietario
    └── tiene una o más → Mascotas
                              └── pueden tener → Citas agendadas
                                                      └── al atenderse → Consulta clínica
                                                                              ├── Medicamentos recetados
                                                                              └── Vacunas aplicadas
```

### Módulos disponibles

| Módulo | Descripción |
|---|---|
| **Dashboard** | Resumen del día: citas pendientes, consultas recientes y estadísticas |
| **Propietarios** | CRUD completo de los dueños o tutores de las mascotas |
| **Mascotas** | CRUD de pacientes con foto, especie, raza y propietario asignado |
| **Veterinarios** | CRUD del personal médico con foto y especialidad |
| **Citas** | Agendamiento de citas con filtros, paginación y cambio de estado |
| **Consultas** | Registro clínico completo: signos vitales, diagnóstico, medicamentos y vacunas |
| **Vacunaciones** | Vista de solo lectura del historial de vacunas por mascota |
| **Catálogos** | Gestión de tablas maestras: especies, razas, especialidades, medicamentos, vacunas |

---

## 4. Arquitectura de la aplicación

### 4.1 El patrón MVC

PetSalud sigue el patrón **MVC (Modelo-Vista-Controlador)**. Este patrón divide la aplicación en
tres capas con responsabilidades claras, lo que facilita el mantenimiento y la comprensión del código.

```
NAVEGADOR
    │
    │  HTTP Request  (ej: GET /citas)
    ▼
┌─────────────────────────────────────────────────────┐
│                    CONTROLADOR                       │
│  CitaController.java                                 │
│  • Recibe la petición                                │
│  • Llama al Servicio para obtener datos              │
│  • Pone los datos en el Model                        │
│  • Devuelve el nombre de la plantilla a renderizar   │
└──────────────────────┬──────────────────────────────┘
                       │
          llama a      │
                       ▼
┌─────────────────────────────────────────────────────┐
│                     SERVICIO                         │
│  CitaServiceImpl.java                                │
│  • Contiene la lógica de negocio                     │
│  • Coordina llamadas al repositorio                  │
│  • No sabe nada de HTTP ni de HTML                   │
└──────────────────────┬──────────────────────────────┘
                       │
          llama a      │
                       ▼
┌─────────────────────────────────────────────────────┐
│                   REPOSITORIO                        │
│  CitaJdbcRepository.java                             │
│  • Ejecuta sentencias SQL                            │
│  • Mapea filas de BD a objetos Java (RowMapper)      │
│  • No sabe nada de negocio ni de HTTP                │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
              ┌────────────────┐
              │   MySQL 8.x    │
              │  Base de datos │
              └────────────────┘

   ◄───── datos regresan por el mismo camino ──────

THYMELEAF renderiza la plantilla HTML con los datos
    │
    ▼
NAVEGADOR recibe HTML y lo muestra al usuario
```

💡 **¿Por qué esta separación?** Si mañana cambiamos MySQL por PostgreSQL, solo modificamos los
repositorios. Si cambiamos Thymeleaf por una API REST, solo tocamos los controladores. El negocio
(servicios) no cambia. Eso se llama **bajo acoplamiento**.

---

### 4.2 ¿Por qué Spring JDBC y no JPA/Hibernate?

Este proyecto usa `NamedParameterJdbcTemplate` (Spring JDBC puro) en lugar de JPA/Hibernate, que
es la alternativa más común en proyectos Spring Boot.

| | Spring JDBC (este proyecto) | JPA / Hibernate |
|---|---|---|
| SQL | Escribes tú cada sentencia | Se genera automáticamente |
| Aprendizaje | Ves exactamente qué consulta se ejecuta | La BD queda oculta |
| Control | Total | Parcial (depende del ORM) |
| Curva de aprendizaje | Menor | Mayor |
| Adecuado para | Aprender, proyectos medianos | Proyectos grandes con mucho CRUD |

💡 **Decisión pedagógica:** para un alumno de primer año, es fundamental entender qué SQL se
ejecuta realmente. Con JPA, el ORM "hace magia" que puede confundir. Con Spring JDBC, el SQL
está siempre visible y aprendemos las dos cosas a la vez: Java y SQL.

---

### 4.3 El patrón Repository (Interfaz + Implementación)

Cada entidad tiene:
- Una **interfaz** (`CitaRepository.java`) que define qué operaciones existen
- Una **implementación** (`CitaJdbcRepository.java`) que las resuelve con SQL

```java
// Interfaz — el "contrato"
public interface CitaRepository {
    Page<Cita> search(...);
    Optional<Cita> findById(Integer id);
    void save(Cita cita);
}

// Implementación — el "cómo"
@Repository
public class CitaJdbcRepository implements CitaRepository {
    // aquí van los SELECT, INSERT, UPDATE...
}
```

💡 **¿Por qué una interfaz?** Si en el futuro quisiéramos tener una implementación que usa
ficheros en lugar de base de datos (por ejemplo, para tests), podemos crear `CitaFileRepository`
sin tocar ningún otro archivo. El servicio usa la interfaz y no le importa quién la implementa.

---

## 5. Estructura de paquetes y archivos

### 5.1 Código Java

```
src/main/java/com/example/petsalud/
│
├── PetsaludApplication.java          ← Punto de entrada. Contiene el main() que arranca Spring Boot
│
├── config/
│   ├── DatabaseConfig.java           ← Crea el DataSource (conexión a MySQL) leyendo application.properties
│   └── WebMvcConfig.java             ← Configura que /uploads/** sirva ficheros físicos del disco
│
├── controller/                       ← Capa de CONTROLADORES: reciben peticiones HTTP y devuelven vistas
│   ├── RootController.java           ← Maneja la raíz "/" y el dashboard (index.html)
│   ├── MascotaController.java        ← CRUD de mascotas + subida de foto
│   ├── PropietarioController.java    ← CRUD de propietarios
│   ├── VeterinarioController.java    ← CRUD de veterinarios + subida de foto
│   ├── CitaController.java           ← CRUD de citas, cancelación y búsqueda/paginación
│   ├── ConsultaController.java       ← Registro de consulta clínica completa
│   ├── VacunacionController.java     ← Vista de solo lectura del historial de vacunas
│   └── catalogo/                     ← Controladores para tablas maestras
│       ├── EspecieController.java    ← CRUD de especies (Perro, Gato, Ave...)
│       ├── RazaController.java       ← CRUD de razas filtrando por especie
│       ├── EspecialidadController.java ← CRUD de especialidades veterinarias
│       ├── MedicamentoController.java  ← CRUD del catálogo de medicamentos
│       └── VacunaController.java     ← CRUD del catálogo de vacunas
│
├── model/                            ← Capa de MODELO: clases que representan los datos
│   ├── Mascota.java                  ← Entidad mascota con validaciones Bean Validation
│   ├── Propietario.java              ← Entidad propietario
│   ├── Veterinario.java              ← Entidad veterinario con fotoUrl
│   ├── Cita.java                     ← Entidad cita + campos extra de JOINs (nombreMascota, etc.)
│   ├── Consulta.java                 ← Entidad consulta clínica con signos vitales
│   ├── ConsultaForm.java             ← DTO especial para el formulario de consulta (incluye listas de meds/vacunas)
│   ├── ConsultaMedicamento.java      ← Representa una línea de medicamento en una consulta
│   ├── ConsultaVacuna.java           ← Representa una vacuna aplicada en una consulta
│   ├── VacunacionRow.java            ← Fila de la vista de historial de vacunaciones (datos de JOINs)
│   ├── DashboardStats.java           ← Contadores para el resumen del dashboard
│   ├── DashboardCitaRow.java         ← Fila de cita reciente para el dashboard
│   ├── DashboardConsultaRow.java     ← Fila de consulta reciente para el dashboard
│   ├── Tratamiento.java              ← Tratamiento médico asociado a una consulta
│   ├── Page.java                     ← Clase genérica de paginación: contenido + metadata de página
│   └── catalogo/                     ← Entidades de las tablas maestras
│       ├── Especie.java
│       ├── Raza.java
│       ├── Especialidad.java
│       ├── EstadoCita.java           ← Estados posibles de una cita (Pendiente, Confirmada, etc.)
│       ├── Medicamento.java
│       └── Vacuna.java
│
├── repository/                       ← Capa de REPOSITORIOS: acceso a base de datos mediante SQL
│   ├── CitaRepository.java           ← Interfaz: define qué operaciones hay para Cita
│   ├── CitaJdbcRepository.java       ← Implementación: SQL real con JOINs, filtros y paginación
│   ├── MascotaRepository.java
│   ├── MascotaJdbcRepository.java    ← Búsqueda de mascotas con filtro de raza dinámica
│   ├── PropietarioRepository.java
│   ├── PropietarioJdbcRepository.java
│   ├── VeterinarioRepository.java
│   ├── VeterinarioJdbcRepository.java
│   ├── ConsultaRepository.java
│   ├── ConsultaJdbcRepository.java   ← Guarda consulta + medicamentos + vacunas en una transacción
│   ├── ConsultaMedicamentoRepository.java
│   ├── ConsultaMedicamentoJdbcRepository.java
│   ├── ConsultaVacunaRepository.java
│   ├── ConsultaVacunaJdbcRepository.java
│   ├── TratamientoRepository.java
│   ├── TratamientoJdbcRepository.java
│   ├── DashboardRepository.java
│   ├── DashboardJdbcRepository.java  ← Consultas SQL de agregación para el dashboard
│   └── catalogo/                     ← Repositorios de tablas maestras (mismo patrón)
│       ├── EspecieRepository.java / EspecieJdbcRepository.java
│       ├── RazaRepository.java / RazaJdbcRepository.java
│       ├── EspecialidadRepository.java / EspecialidadJdbcRepository.java
│       ├── EstadoCitaRepository.java / EstadoCitaJdbcRepository.java
│       ├── MedicamentoRepository.java / MedicamentoJdbcRepository.java
│       └── VacunaRepository.java / VacunaJdbcRepository.java
│
└── service/                          ← Capa de SERVICIOS: lógica de negocio
    ├── CitaService.java              ← Interfaz del servicio de citas
    ├── MascotaService.java
    ├── PropietarioService.java
    ├── VeterinarioService.java
    ├── ConsultaService.java
    ├── VacunacionService.java
    ├── DashboardService.java
    ├── FileStorageService.java       ← Guarda fotos subidas en disco con nombre UUID único
    ├── impl/                         ← Implementaciones concretas de los servicios
    │   ├── CitaServiceImpl.java      ← Lógica: cancelar cambia el estado, no borra el registro
    │   ├── MascotaServiceImpl.java
    │   ├── PropietarioServiceImpl.java
    │   ├── VeterinarioServiceImpl.java
    │   ├── ConsultaServiceImpl.java  ← Orquesta: guarda consulta + borra/inserta meds y vacunas
    │   ├── VacunacionServiceImpl.java
    │   └── DashboardServiceImpl.java
    └── catalogo/
        ├── EspecieService.java / impl/EspecieServiceImpl.java
        ├── RazaService.java / impl/RazaServiceImpl.java
        ├── EspecialidadService.java / impl/EspecialidadServiceImpl.java
        ├── EstadoCitaService.java / impl/EstadoCitaServiceImpl.java
        ├── MedicamentoService.java / impl/MedicamentoServiceImpl.java
        └── VacunaService.java / impl/VacunaServiceImpl.java
```

---

### 5.2 Recursos y plantillas

```
src/main/resources/
│
├── application.properties            ← Configuración central: BD, Thymeleaf, tamaño de uploads
│
├── static/
│   ├── css/
│   │   ├── base.css                  ← Variables CSS globales (colores, tipografías, utilidades)
│   │   ├── layout.css                ← Estructura: sidebar, topbar, contenedor principal
│   │   └── components.css            ← Componentes reutilizables: cards, tablas, badges, botones
│   └── js/
│       └── app.js                    ← Scripts globales (toggle del sidebar, alerts)
│
└── templates/
    ├── layout/
    │   └── base.html                 ← Plantilla base: incluye CSS, sidebar, topbar y define el slot "content"
    ├── fragments/
    │   ├── sidebar.html              ← Menú lateral de navegación con íconos
    │   └── topbar.html               ← Barra superior con buscador y perfil de usuario
    ├── index.html                    ← Dashboard principal con estadísticas y accesos rápidos
    ├── mascotas/
    │   ├── lista.html                ← Tabla con foto, filtros y paginación
    │   └── form.html                 ← Formulario con avatar circular y previsualización de foto
    ├── veterinarios/
    │   ├── lista.html
    │   └── form.html                 ← Mismo patrón de avatar que mascotas
    ├── propietarios/
    │   ├── lista.html
    │   └── form.html
    ├── citas/
    │   ├── lista.html                ← Tabla con badges de estado, filtros y acciones contextuales
    │   └── form.html                 ← Avatar dinámico de la mascota seleccionada (sin AJAX)
    ├── consultas/
    │   ├── lista.html
    │   ├── form.html                 ← Formulario multi-sección con signos vitales, meds y vacunas
    │   └── detalle.html              ← Vista de solo lectura de una consulta ya registrada
    ├── vacunaciones/
    │   └── lista.html                ← Historial de vacunaciones (solo lectura)
    └── catalogos/
        ├── especies/   (lista.html + form.html)
        ├── razas/      (lista.html + form.html)
        ├── especialidades/ (lista.html + form.html)
        ├── medicamentos/   (lista.html + form.html)
        └── vacunas/    (lista.html + form.html)
```

---

### 5.3 Archivos raíz del proyecto

```
petsalud/
├── pom.xml                 ← Descriptor Maven: dependencias, plugins y versión de Java
├── Dockerfile              ← Build multietapa para generar imagen Docker de producción
├── docker-compose.yml      ← Orquesta la app + MySQL en una red interna privada
├── .env.example            ← Plantilla de variables de entorno (copiar como .env)
├── .dockerignore           ← Excluye target/, .git y uploads/ del contexto de build
├── .gitignore              ← Excluye .env, target/ e IDE files del repositorio
└── docs/
    ├── ini_db.sql          ← Crea la base de datos, todas las tablas y datos de catálogo base
    ├── seed_data.sql       ← Inserta datos de ejemplo para desarrollo y pruebas
    └── modelo_datos.md     ← Documentación del modelo relacional
```

---

## 6. Análisis de la base de datos

### 6.1 Diagrama relacional simplificado

```
┌──────────┐        ┌──────────┐
│ especie  │◄───────│  raza    │
└──────────┘  1:N   └─────┬────┘
                           │ N:1
                    ┌──────▼──────┐     ┌─────────────┐
                    │   mascota   │────►│ propietario  │
                    └──────┬──────┘ N:1 └─────────────┘
                           │ 1:N
                    ┌──────▼──────┐     ┌──────────────┐   ┌─────────────┐
                    │    cita     │────►│  veterinario  │──►│ especialidad│
                    └──────┬──────┘ N:1 └──────────────┘N:1└─────────────┘
                           │  N:1
                    ┌──────▼──────┐
                    │  estado_cita│
                    └─────────────┘
                           │ 1:1
                    ┌──────▼──────┐
                    │   consulta  │
                    └──────┬──────┘
                    ┌───────────────────┐
                    │                   │
             ┌──────▼──────┐   ┌────────▼────────┐
             │ consulta_   │   │  consulta_vacuna │
             │ medicamento │   └────────┬─────────┘
             └──────┬──────┘            │ N:1
                    │ N:1        ┌──────▼──────┐
             ┌──────▼──────┐    │   vacuna     │
             │ medicamento  │   └─────────────┘
             └─────────────┘
```

---

### 6.2 Tablas de catálogo (tablas maestras)

Son tablas que contienen los valores fijos del sistema. El administrador las gestiona desde el
menú "Catálogos". Sin estos datos base, el resto de la aplicación no funciona correctamente.

| Tabla | Propósito | Ejemplos de datos |
|---|---|---|
| `especie` | Tipos de animales atendidos | Perro, Gato, Ave, Reptil |
| `raza` | Razas por especie | Labrador (Perro), Siamés (Gato) |
| `especialidad` | Especialidades del personal veterinario | Cirugía, Dermatología, Oftalmología |
| `estado_cita` | Ciclo de vida de una cita | Pendiente, Confirmada, En consulta, Completada, Cancelada |
| `medicamento` | Medicamentos disponibles para prescribir | Amoxicilina, Meloxicam |
| `vacuna` | Vacunas disponibles para registrar | Rabia, Parvovirus, Leptospirosis |

---

### 6.3 Descripción detallada de cada tabla

#### `especie`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(60) UNIQUE | Nombre de la especie (índice único) |
| `descripcion` | VARCHAR(255) NULL | Descripción opcional |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico: 0 = desactivada |

#### `raza`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(100) | Nombre de la raza |
| `id_especie` | INT FK → especie | A qué especie pertenece |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico |

#### `especialidad`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(100) UNIQUE | Nombre de la especialidad |
| `descripcion` | VARCHAR(255) NULL | Descripción |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico |

#### `estado_cita`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(50) UNIQUE | Nombre del estado |

#### `medicamento`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(150) | Nombre comercial o genérico |
| `presentacion` | VARCHAR(100) NULL | Forma farmacéutica (ej: Tableta 500mg) |
| `descripcion` | VARCHAR(255) NULL | Indicaciones generales |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico |

#### `vacuna`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(150) | Nombre de la vacuna |
| `laboratorio` | VARCHAR(100) NULL | Laboratorio fabricante |
| `descripcion` | VARCHAR(255) NULL | Enfermedades que previene |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico |

---

#### `propietario`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(100) | Nombre(s) del propietario |
| `apellido` | VARCHAR(100) | Apellido(s) |
| `documento` | VARCHAR(20) UNIQUE | DNI, cédula o pasaporte |
| `telefono` | VARCHAR(20) NULL | Teléfono de contacto |
| `email` | VARCHAR(150) NULL | Correo electrónico |
| `direccion` | VARCHAR(255) NULL | Dirección de residencia |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico |
| `created_at` | TIMESTAMP | Fecha de registro automática |

#### `veterinario`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(100) | Nombre(s) |
| `apellido` | VARCHAR(100) | Apellido(s) |
| `matricula` | VARCHAR(50) UNIQUE | Número de matrícula profesional |
| `telefono` | VARCHAR(20) NULL | Teléfono |
| `email` | VARCHAR(150) NULL | Email profesional |
| `id_especialidad` | INT NULL FK → especialidad | Puede ser NULL (medicina general) |
| `foto_url` | VARCHAR(500) NULL | Ruta de la foto almacenada en disco |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico |
| `created_at` | TIMESTAMP | Fecha de alta |

#### `mascota`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `nombre` | VARCHAR(100) | Nombre de la mascota |
| `fecha_nacimiento` | DATE NULL | Fecha de nacimiento (aproximada) |
| `sexo` | ENUM('M','F','Desconocido') | Sexo biológico |
| `color` | VARCHAR(80) NULL | Color o descripción del pelaje |
| `id_especie` | INT FK → especie | Especie (obligatorio) |
| `id_raza` | INT NULL FK → raza | Raza (NULL si es mestiza) |
| `id_propietario` | INT FK → propietario | Dueño o tutor |
| `foto_url` | VARCHAR(500) NULL | Ruta de la foto |
| `activo` | TINYINT(1) DEFAULT 1 | Borrado lógico |
| `created_at` | TIMESTAMP | Fecha de registro |

#### `cita`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `id_mascota` | INT FK → mascota | Paciente de la cita |
| `id_veterinario` | INT FK → veterinario | Veterinario asignado |
| `id_estado_cita` | INT FK → estado_cita | Estado actual |
| `fecha_hora` | DATETIME | Fecha y hora de la cita |
| `motivo` | VARCHAR(255) | Motivo de la consulta (obligatorio) |
| `observaciones` | TEXT NULL | Notas del agendamiento |
| `created_at` | TIMESTAMP | Fecha de creación |

#### `consulta`
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `id_cita` | INT UNIQUE FK → cita | Una cita solo puede tener una consulta |
| `fecha_hora` | DATETIME | Fecha/hora efectiva de atención |
| `peso_kg` | DECIMAL(5,2) NULL | Peso en kilogramos |
| `temperatura_c` | DECIMAL(4,1) NULL | Temperatura en °C |
| `frecuencia_cardiaca` | INT NULL | Latidos por minuto |
| `frecuencia_resp` | INT NULL | Respiraciones por minuto |
| `anamnesis` | TEXT NULL | Historia clínica relatada por el propietario |
| `examen_fisico` | TEXT NULL | Hallazgos del examen físico |
| `diagnostico` | TEXT NULL | Diagnóstico o impresión diagnóstica |
| `observaciones` | TEXT NULL | Recomendaciones generales |

#### `consulta_medicamento` (relación N:M entre consulta y medicamento)
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `id_consulta` | INT FK → consulta | Consulta en la que se recetó |
| `id_medicamento` | INT FK → medicamento | Medicamento prescrito |
| `dosis` | VARCHAR(100) | Ej: "1 tableta", "5ml" |
| `frecuencia` | VARCHAR(100) | Ej: "cada 8 horas" |
| `duracion` | VARCHAR(100) NULL | Ej: "7 días" |
| `indicaciones` | TEXT NULL | Instrucciones especiales |

#### `consulta_vacuna` (relación N:M entre consulta y vacuna)
| Columna | Tipo | Descripción |
|---|---|---|
| `id` | INT AUTO_INCREMENT | Clave primaria |
| `id_consulta` | INT FK → consulta | Consulta en la que se aplicó |
| `id_vacuna` | INT FK → vacuna | Vacuna aplicada |
| `lote` | VARCHAR(50) NULL | Número de lote del vial |
| `proxima_dosis` | DATE NULL | Fecha estimada del refuerzo |
| `observaciones` | VARCHAR(255) NULL | Notas adicionales |

💡 **¿Por qué `consulta_medicamento` y `consulta_vacuna` son tablas separadas?**
Una consulta puede tener múltiples medicamentos y múltiples vacunas. Esto es una relación
**muchos a muchos (N:M)**. En una base de datos relacional, las relaciones N:M siempre se
modelan con una tabla intermedia. Estas tablas intermedias añaden además información propia
de esa relación (dosis, lote, fechas), lo que las convierte en tablas de **asociación enriquecida**.

---

### 6.4 Borrado lógico

La mayoría de las tablas tienen una columna `activo TINYINT(1)`. En lugar de eliminar registros
con `DELETE`, el sistema los desactiva poniendo `activo = 0`. Esto preserva el historial clínico
y evita problemas de integridad referencial.

```sql
-- En lugar de esto (peligroso, puede romper FKs):
DELETE FROM veterinario WHERE id = 5;

-- Se hace esto (seguro, preserva historial):
UPDATE veterinario SET activo = 0 WHERE id = 5;
```

---

## 7. Inicialización de la base de datos

El directorio `docs/` contiene dos scripts SQL que debes ejecutar en orden:

### 7.1 `ini_db.sql` — Esquema completo

Este script hace exactamente lo siguiente, en orden:
1. Elimina la base de datos `petsalud` si ya existe (`DROP DATABASE IF EXISTS`)
2. Crea la base de datos con codificación `utf8mb4` y colación española
3. Crea **todas las tablas** en el orden correcto (primero las que no tienen FK, luego las que sí)
4. Define todas las **claves foráneas** (relaciones entre tablas)
5. Crea los **índices** para acelerar las búsquedas más frecuentes
6. Inserta los **datos de catálogo base** (estados de cita, especies iniciales, etc.)

> ⚠️ **Importante:** cada vez que ejecutas `ini_db.sql` se borra y recrea toda la base de datos.
> Úsalo solo al iniciar el proyecto o cuando quieras partir de cero.

### 7.2 `seed_data.sql` — Datos de ejemplo

Este script inserta datos ficticios pero realistas para que puedas probar la aplicación sin tener
que introducir datos manualmente:
- Propietarios de ejemplo
- Mascotas con diferentes especies y razas
- Veterinarios con distintas especialidades
- Citas en diferentes estados
- Consultas clínicas con medicamentos y vacunas asociados

### 7.3 Comandos para ejecutar los scripts

**Desde la terminal (línea de comandos):**

```bash
# Opción A: ejecutar ambos scripts en una sola línea
mysql -u root -p < docs/ini_db.sql && mysql -u root -p petsalud < docs/seed_data.sql

# Opción B: entrar al cliente MySQL y ejecutar manualmente
mysql -u root -p

# Dentro del cliente MySQL:
source /ruta/completa/al/proyecto/docs/ini_db.sql
source /ruta/completa/al/proyecto/docs/seed_data.sql
exit
```

**Desde MySQL Workbench:**
1. Abre MySQL Workbench y conéctate a tu servidor local
2. Menú **File → Open SQL Script** → selecciona `docs/ini_db.sql`
3. Pulsa el rayo ⚡ (Execute) o `Ctrl+Shift+Enter`
4. Repite con `docs/seed_data.sql`

**Verificar que todo fue bien:**
```sql
USE petsalud;
SHOW TABLES;
SELECT COUNT(*) FROM propietario;
SELECT COUNT(*) FROM mascota;
SELECT COUNT(*) FROM cita;
```

---

## 8. Levantar el entorno local (sin Docker)

### Requisitos previos

| Herramienta | Versión mínima | Cómo verificar |
|---|---|---|
| Java JDK | 21 | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| MySQL | 8.0+ | `mysql --version` |
| Git | cualquiera | `git --version` |

### Paso a paso

**1. Clonar el repositorio**
```bash
git clone https://github.com/sulbaranjc/petsalud.git
cd petsalud
```

**2. Crear la base de datos y cargar datos**
```bash
# Crear esquema y tablas
mysql -u root -p < docs/ini_db.sql

# Cargar datos de ejemplo
mysql -u root -p petsalud < docs/seed_data.sql
```

**3. Crear el usuario de base de datos** (si no usas root)
```sql
CREATE USER 'petsalud'@'localhost' IDENTIFIED BY 'petsalud';
GRANT ALL PRIVILEGES ON petsalud.* TO 'petsalud'@'localhost';
FLUSH PRIVILEGES;
```

**4. Configurar la conexión**

Edita el archivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/petsalud?useSSL=false&serverTimezone=America/Bogota&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=petsalud
spring.datasource.password=petsalud
```

💡 Si tu MySQL tiene contraseña de root diferente, ajusta `username` y `password`. Si usas
Windows, puede que también necesites cambiar `America/Bogota` por `Europe/Madrid`.

**5. Arrancar la aplicación**
```bash
mvn spring-boot:run
```

Verás en la consola algo como:
```
Started PetsaludApplication in 3.4 seconds (JVM running for 4.1)
```

**6. Abrir en el navegador**
```
http://localhost:8080
```

---

## 9. Despliegue con Docker

Docker permite empaquetar la aplicación y sus dependencias en contenedores aislados, de forma
que el entorno de producción (un servidor Proxmox, un VPS, etc.) no necesita tener Java ni
Maven instalados.

### 9.1 ¿Qué hace el `Dockerfile`?

Usa un **build multietapa** (multi-stage build): dos bloques `FROM` en el mismo fichero.

```dockerfile
# Etapa 1: CONSTRUCCIÓN
# Usa una imagen que tiene Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21 AS construccion
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline   # descarga dependencias (aprovecha caché)
COPY src ./src
RUN mvn package -DskipTests    # compila y genera el JAR

# Etapa 2: EJECUCIÓN
# Usa solo el JRE (mucho más pequeño que el JDK)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=construccion /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

💡 **¿Por qué dos etapas?** La imagen final solo contiene el JRE y el JAR. No incluye Maven,
el código fuente ni las dependencias descargadas durante el build. La imagen resultante es
mucho más pequeña y segura.

### 9.2 ¿Qué hace el `docker-compose.yml`?

Orquesta dos servicios que se comunican entre sí a través de una red interna privada:

```
Internet
    │
    │ puerto 80
    ▼
┌─────────────────────────────────────────┐
│           red-interna (bridge)           │
│                                          │
│  ┌──────────────┐    ┌───────────────┐  │
│  │  aplicacion  │───►│   basedatos   │  │
│  │ (Spring Boot)│    │   (MySQL 8)   │  │
│  │  :8080       │    │   :3306       │  │
│  └──────────────┘    └───────────────┘  │
└─────────────────────────────────────────┘
```

El servicio `basedatos` **no está expuesto a internet** (sin `ports:`). Solo la aplicación
puede conectarse a él, a través de la red interna Docker.

### 9.3 Comandos para desplegar

```bash
# 1. Clonar el proyecto en el servidor
git clone https://github.com/sulbaranjc/petsalud.git
cd petsalud

# 2. Crear el fichero de credenciales (nunca subir .env a git)
cp .env.example .env

# 3. Editar las contraseñas
nano .env
# Cambia DB_CONTRASENA y DB_ROOT_CONTRASENA por valores seguros

# 4. Construir las imágenes y levantar los contenedores
docker compose up -d --build

# 5. Ver que todo está corriendo
docker compose ps

# 6. Ver los logs de la aplicación
docker compose logs -f aplicacion
```

La aplicación estará disponible en `http://IP-del-servidor` (puerto 80).

### 9.4 ¿Por qué la app espera al healthcheck de MySQL?

```yaml
depends_on:
  basedatos:
    condition: service_healthy
```

MySQL tarda unos segundos en arrancar y aceptar conexiones. Sin el healthcheck, Spring Boot
intentaría conectarse antes de que MySQL esté listo y la aplicación fallaría. El healthcheck
ejecuta `mysqladmin ping` cada 10 segundos y solo cuando responde con éxito se arranca la app.

### 9.5 Volúmenes persistentes

```yaml
volumes:
  datos-mysql:    # guarda los datos de MySQL aunque el contenedor se borre
  fotos-subidas:  # guarda las fotos de mascotas y veterinarios
```

💡 Sin volúmenes, cada vez que se reinicia un contenedor se perderían todos los datos. Los
volúmenes de Docker son directorios gestionados por Docker que persisten independientemente
del ciclo de vida del contenedor.

---

## 10. Flujo completo de una funcionalidad

Vamos a trazar de principio a fin lo que ocurre cuando un usuario registra una nueva cita.

### Paso 1 — El usuario navega a "Nueva Cita"

El navegador envía:
```
GET /citas/nueva
```

### Paso 2 — El Controlador recibe la petición

```java
// CitaController.java
@GetMapping("/nueva")
public String nuevaForm(@RequestParam(required = false) Integer idMascota,
                        Model model) {
    Cita cita = new Cita();
    // Pre-selecciona el estado "Pendiente"
    estadoCitaService.findByNombre("Pendiente")
            .ifPresent(e -> cita.setIdEstadoCita(e.getId()));
    // Si viene desde la lista de mascotas, pre-selecciona la mascota
    if (idMascota != null) cita.setIdMascota(idMascota);
    // Carga los datos para los desplegables del formulario
    cargarFormModel(model, cita);
    return "citas/form";  // nombre de la plantilla Thymeleaf
}
```

### Paso 3 — El Servicio obtiene los datos de los desplegables

```java
// El método cargarFormModel llama a tres servicios:
model.addAttribute("mascotas",     mascotaService.findAllActivas());
model.addAttribute("veterinarios", veterinarioService.findAllActivos());
model.addAttribute("estados",      estadoCitaService.findAll());
```

### Paso 4 — El Repositorio ejecuta SQL

```java
// MascotaJdbcRepository.java (simplificado)
public List<Mascota> findAllActivas() {
    String sql = "SELECT m.*, e.nombre AS nombre_especie, " +
                 "CONCAT(p.apellido, ', ', p.nombre) AS nombre_propietario " +
                 "FROM mascota m JOIN especie e ON e.id = m.id_especie " +
                 "JOIN propietario p ON p.id = m.id_propietario " +
                 "WHERE m.activo = 1 ORDER BY m.nombre";
    return jdbc.query(sql, ROW_MAPPER);
}
```

### Paso 5 — Thymeleaf renderiza el HTML

```html
<!-- citas/form.html -->
<select id="idMascota" th:field="*{idMascota}" class="form-select">
  <option value="">— Seleccione una mascota —</option>
  <option th:each="m : ${mascotas}"
          th:value="${m.id}"
          th:text="${m.nombre + ' (' + m.nombreEspecie + ') – ' + m.nombrePropietario}"
          th:attr="data-foto-url=${m.fotoUrl != null ? m.fotoUrl : ''}">
  </option>
</select>
```

El navegador recibe el HTML completo con todas las mascotas en el desplegable.

### Paso 6 — El usuario rellena el formulario y pulsa Guardar

El navegador envía:
```
POST /citas
Content-Type: application/x-www-form-urlencoded

idMascota=3&idVeterinario=2&idEstadoCita=1&fechaHora=2025-04-15T10:30&motivo=Control+anual
```

### Paso 7 — El Controlador valida los datos

```java
@PostMapping
public String guardar(@Valid @ModelAttribute Cita cita,
                      BindingResult result,
                      Model model,
                      RedirectAttributes flash) {

    if (result.hasErrors()) {
        // Si hay errores, volvemos al formulario con los mensajes
        cargarFormModel(model, cita);
        return "citas/form";
    }
    // Sin errores: guardamos
    citaService.save(cita);
    flash.addFlashAttribute("mensajeExito", "Cita guardada correctamente.");
    return "redirect:/citas";
}
```

`@Valid` activa las validaciones definidas en `Cita.java` con anotaciones como `@NotNull`,
`@NotBlank` y `@Size`. Si alguna falla, `result.hasErrors()` devuelve `true`.

### Paso 8 — El Repositorio ejecuta el INSERT

```java
// CitaJdbcRepository.java
private void insert(Cita c) {
    String sql = """
            INSERT INTO cita (id_mascota, id_veterinario, id_estado_cita,
                              fecha_hora, motivo, observaciones)
            VALUES (:idMascota, :idVeterinario, :idEstadoCita,
                    :fechaHora, :motivo, :observaciones)
            """;
    jdbc.update(sql, toParams(c));
}
```

Los parámetros `:idMascota` son **parámetros nombrados** (a diferencia del `?` de JDBC
básico). Esto hace el SQL más legible y evita errores por orden de parámetros.

### Paso 9 — Redirect y mensaje flash

Spring hace un `redirect` a `/citas`. Esto evita el problema de doble envío del formulario
si el usuario recarga la página. El mensaje "Cita guardada correctamente" viaja a través de
`RedirectAttributes` y se muestra solo una vez en la siguiente página.

---

## 11. Glosario de conceptos clave

| Concepto | Dónde se usa en PetSalud | Explicación |
|---|---|---|
| `@Controller` | `CitaController`, `MascotaController`... | Marca una clase como controlador Spring MVC. Sus métodos responden a peticiones HTTP y devuelven el nombre de una plantilla Thymeleaf. |
| `@Service` | `CitaServiceImpl`, `MascotaServiceImpl`... | Marca una clase como servicio. Spring la crea automáticamente y la inyecta donde se necesite. Contiene la lógica de negocio. |
| `@Repository` | `CitaJdbcRepository`, `MascotaJdbcRepository`... | Marca una clase como repositorio de datos. Spring la identifica como capa de acceso a BD y gestiona sus excepciones. |
| `NamedParameterJdbcTemplate` | Todos los `*JdbcRepository` | Herramienta de Spring para ejecutar SQL con parámetros nombrados (`:nombre`) en lugar de `?`. Más seguro y legible. |
| `RowMapper<T>` | Todos los `*JdbcRepository` | Función que convierte una fila de resultado SQL en un objeto Java. Cada repositorio define el suyo como constante estática. |
| `th:field` | Todos los `form.html` | Atributo Thymeleaf que vincula un campo HTML con una propiedad del objeto del formulario. Gestiona automáticamente `name`, `value` e `id`. |
| `th:each` | Todos los `lista.html` y selects | Equivalente al `for-each` de Java en HTML. Itera sobre una colección y genera un elemento por cada ítem. |
| `@Valid` | Métodos POST de todos los controladores | Activa la validación de Bean Validation sobre el objeto recibido del formulario. |
| `BindingResult` | Métodos POST de todos los controladores | Contiene los errores de validación. Siempre debe ir justo después del parámetro anotado con `@Valid`. |
| `RedirectAttributes` | Métodos POST exitosos | Permite enviar atributos (como mensajes de éxito) a través de un redirect HTTP sin que aparezcan en la URL. |
| `Page<T>` | `CitaController`, `MascotaController`... | Clase propia que encapsula una lista de resultados + metadatos de paginación (total, página actual, si hay siguiente/anterior). |
| `MultipartFile` | `MascotaController`, `VeterinarioController` | Tipo de Spring para recibir ficheros subidos desde formularios HTML (`enctype="multipart/form-data"`). |
| `Optional<T>` | `findById` en todos los repositorios | Envoltorio de Java que fuerza a manejar el caso en que un resultado puede no existir, evitando `NullPointerException`. |

---

> 💡 **Consejo para el estudio:** empieza por entender el flujo de `CitaController` →
> `CitaServiceImpl` → `CitaJdbcRepository`. Una vez que dominas ese ciclo para las citas,
> el resto de módulos siguen exactamente el mismo patrón. La repetición del patrón es
> intencional: en el desarrollo profesional, la consistencia es más valiosa que la
> originalidad en cada fichero.

---

*Proyecto desarrollado con fines educativos para DAM/DAW — Formación Profesional.*
