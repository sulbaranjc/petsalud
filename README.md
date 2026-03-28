# PetSalud

Sistema web de gestión para clínicas veterinarias. Permite administrar propietarios, mascotas, veterinarios, citas médicas, consultas clínicas y vacunaciones desde un navegador.

## Tecnologías

- **Java 21** · **Spring Boot 4.0.4** (Spring MVC, Spring JDBC, Thymeleaf, Bean Validation)
- **MySQL 8** · **Maven 3.9**
- **Bootstrap 5** · Bootstrap Icons
- **Docker** + Docker Compose

## Requisitos previos

| Herramienta | Versión |
|---|---|
| Java JDK | 21 |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Git | cualquiera |

## Instalación y puesta en marcha

### 1. Clonar el repositorio

```bash
git clone https://github.com/sulbaranjc/petsalud.git
cd petsalud
```

### 2. Crear la base de datos

```bash
# Crear esquema y tablas
mysql -u root -p < docs/ini_db.sql

# Cargar datos de ejemplo (opcional)
mysql -u root -p petsalud < docs/seed_data.sql
```

### 3. Configurar la conexión

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/petsalud?useSSL=false&serverTimezone=America/Bogota&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=petsalud
spring.datasource.password=petsalud
```

### 4. Ejecutar

```bash
mvn spring-boot:run
```

Abre el navegador en `http://localhost:8080`.

---

## Despliegue con Docker

```bash
# Copiar y configurar variables de entorno
cp .env.example .env
nano .env

# Levantar la aplicación y la base de datos
docker compose up -d --build
```

La aplicación queda disponible en el puerto **80**. Los datos persisten en volúmenes Docker nombrados.

Variables disponibles en `.env`:

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_NOMBRE` | Nombre de la base de datos | `petsalud` |
| `DB_USUARIO` | Usuario MySQL | `petsalud` |
| `DB_CONTRASENA` | Contraseña del usuario | — |
| `DB_ROOT_CONTRASENA` | Contraseña root de MySQL | — |

---

## Estructura del proyecto

```
src/main/java/com/example/petsalud/
├── config/          # Configuración de DataSource y servidor de recursos estáticos
├── controller/      # Controladores Spring MVC (uno por módulo)
│   └── catalogo/    # Controladores de tablas maestras
├── model/           # Entidades y DTOs
│   └── catalogo/    # Entidades de catálogo
├── repository/      # Acceso a datos con NamedParameterJdbcTemplate
│   └── catalogo/
├── service/         # Lógica de negocio
│   └── catalogo/
│       └── impl/
└── service/impl/

src/main/resources/
├── templates/       # Plantillas Thymeleaf
│   ├── layout/      # Plantilla base (sidebar + topbar)
│   ├── fragments/
│   ├── mascotas/ · veterinarios/ · propietarios/
│   ├── citas/ · consultas/ · vacunaciones/
│   └── catalogos/
├── static/css/      # base.css · layout.css · components.css
└── application.properties

docs/
├── ini_db.sql       # Esquema completo de la base de datos
└── seed_data.sql    # Datos de ejemplo para desarrollo
```

## Módulos

| Módulo | Ruta | Descripción |
|---|---|---|
| Dashboard | `/` | Resumen del día con estadísticas |
| Mascotas | `/mascotas` | CRUD con foto y filtros |
| Propietarios | `/propietarios` | CRUD de tutores |
| Veterinarios | `/veterinarios` | CRUD con foto y especialidad |
| Citas | `/citas` | Agendamiento con estados y paginación |
| Consultas | `/consultas` | Registro clínico completo |
| Vacunaciones | `/vacunaciones` | Historial de vacunas |
| Catálogos | `/catalogos/...` | Especies, razas, especialidades, medicamentos, vacunas |

## Base de datos

El script `docs/ini_db.sql` crea la base de datos desde cero. Las tablas principales son:

```
especie ──< raza ──< mascota >── propietario
                        │
                       cita >── veterinario >── especialidad
                          │           └── estado_cita
                       consulta
                        ├──< consulta_medicamento >── medicamento
                        └──< consulta_vacuna      >── vacuna
```

> `ini_db.sql` elimina y recrea la base de datos completa. Úsalo solo en instalación inicial o para reiniciar el entorno de desarrollo.

## Arquitectura

La aplicación sigue el patrón **MVC** con tres capas bien diferenciadas:

- **Controller** — recibe peticiones HTTP, delega al servicio, devuelve la vista
- **Service** — contiene la lógica de negocio, desacoplado de HTTP y de SQL
- **Repository** — ejecuta SQL puro con `NamedParameterJdbcTemplate`, sin ORM

Cada entidad tiene una interfaz de repositorio y su implementación JDBC, lo que permite cambiar el mecanismo de persistencia sin afectar las capas superiores.

## Documentación extendida

El proyecto incluye documentación pedagógica orientada a alumnos de DAM/DAW en [`docs/modelo_datos.md`](docs/modelo_datos.md).

## Licencia

Proyecto educativo — uso libre para fines de aprendizaje y formación.
