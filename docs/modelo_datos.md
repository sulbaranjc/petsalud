# Modelo de datos — PetSalud

## Índice

1. [Diagrama de relaciones](#1-diagrama-de-relaciones)
2. [Descripción de tablas](#2-descripción-de-tablas)
3. [Relaciones entre tablas](#3-relaciones-entre-tablas)
4. [Casos de uso: raza nula y mestizaje](#4-casos-de-uso-raza-nula-y-mestizaje)
5. [Reglas de integridad referencial](#5-reglas-de-integridad-referencial)

---

## 1. Diagrama de relaciones

```
┌─────────────┐        ┌──────────────┐        ┌──────────┐
│ especialidad│◄───────│  veterinario │        │  especie │
└─────────────┘  0..1  └──────────────┘        └────┬─────┘
                                │                    │ 1
                                │                    │
                                │               ┌────▼─────┐
                                │               │   raza   │
                                │               └────┬─────┘
                                │ 1..n               │ 0..n
                                │                    │
┌─────────────┐        ┌────────▼───────────────────▼┐
│ propietario │◄───────│          mascota             │
└─────────────┘  1..n  │  id_especie  NOT NULL (FK)   │
                        │  id_raza     NULL    (FK)    │
                        │  id_propietario NOT NULL(FK) │
                        └──────────────┬───────────────┘
                                       │ 1..n
                               ┌───────▼──────┐
                               │     cita     │
                               └───────┬──────┘
                                       │ 1..1
                               ┌───────▼──────┐      ┌─────────────┐
                               │   consulta   │◄─────│ tratamiento │
                               └───────┬──────┘      └─────────────┘
                                       │
                               ┌───────▼──────┐      ┌─────────────┐
                               │    receta    │◄─────│ medicamento │
                               └─────────────┘      └─────────────┘

┌──────────┐     ┌──────────────────────┐     ┌─────────────┐
│  vacuna  │◄────│ historial_vacunacion │────►│  mascota    │
└──────────┘     └──────────────────────┘     └─────────────┘
```

---

## 2. Descripción de tablas

### Catálogos sin dependencias

| Tabla | Descripción | Campos clave |
|---|---|---|
| `especie` | Tipos de animales atendidos (Perro, Gato, Ave…) | `id`, `nombre`, `activo` |
| `especialidad` | Especialidades veterinarias | `id`, `nombre`, `activo` |
| `medicamento` | Catálogo de medicamentos disponibles | `id`, `nombre`, `presentacion`, `activo` |
| `vacuna` | Catálogo de vacunas aplicables | `id`, `nombre`, `laboratorio`, `activo` |
| `estado_cita` | Estados posibles de una cita (Pendiente, Confirmada…) | `id`, `nombre` |

### Catálogos con dependencias

| Tabla | Depende de | Descripción |
|---|---|---|
| `raza` | `especie` | Cada raza pertenece a exactamente una especie. Un Labrador solo puede ser de especie Perro. |

### Entidades principales

| Tabla | Depende de | Descripción |
|---|---|---|
| `propietario` | — | Dueño o tutor de una o más mascotas |
| `veterinario` | `especialidad` (opcional) | Personal veterinario de la clínica |
| `mascota` | `especie`, `raza` (opcional), `propietario` | Paciente registrado en la clínica |

### Entidades de gestión clínica

| Tabla | Depende de | Descripción |
|---|---|---|
| `cita` | `mascota`, `veterinario`, `estado_cita` | Cita médica agendada |
| `consulta` | `cita` | Registro clínico de la consulta efectuada |
| `tratamiento` | `consulta` | Tratamiento indicado en una consulta |
| `receta` | `consulta`, `medicamento` | Medicamento recetado (una fila por medicamento) |
| `historial_vacunacion` | `mascota`, `vacuna`, `veterinario` | Registro histórico de vacunas aplicadas |

---

## 3. Relaciones entre tablas

### `especie` → `raza` (1 a muchos)

```
especie (1) ──────────────── (N) raza
```

- Una especie puede tener cero o muchas razas registradas.
- Una raza pertenece a exactamente una especie.
- La FK `raza.id_especie` es `NOT NULL` — no existe raza sin especie.
- `ON DELETE RESTRICT`: no se puede eliminar una especie que tenga razas asociadas.

### `mascota` → `especie` (muchos a 1, obligatoria)

```
mascota (N) ──────────────── (1) especie
```

- Toda mascota debe tener especie definida (`id_especie NOT NULL`).
- La especie determina qué razas son válidas para esa mascota.

### `mascota` → `raza` (muchos a 1, **opcional**)

```
mascota (N) ──────────────── (0..1) raza
```

- `id_raza` es `INT NULL` — la raza es opcional.
- Cuando `id_raza = NULL`, la mascota no tiene raza específica registrada.
- Cuando `id_raza` tiene valor, la raza referenciada **debe pertenecer a la misma especie** que la mascota. Esta consistencia se garantiza en la capa de aplicación (el formulario solo muestra razas de la especie seleccionada).

### `mascota` → `propietario` (muchos a 1, obligatoria)

```
mascota (N) ──────────────── (1) propietario
```

- Toda mascota debe tener propietario (`id_propietario NOT NULL`).

---

## 4. Casos de uso: raza nula y mestizaje

### ¿Por qué `id_raza` es NULL y no una fila "Mestizo"?

La decisión de modelar el mestizaje como `id_raza = NULL` en lugar de insertar filas del tipo "Mestizo Perro", "Mestizo Gato", etc. responde a los siguientes criterios:

#### Opción A — Fila explícita por especie (descartada)

```sql
INSERT INTO raza (nombre, id_especie) VALUES ('Mestizo', 1); -- Perro
INSERT INTO raza (nombre, id_especie) VALUES ('Mestizo', 2); -- Gato
INSERT INTO raza (nombre, id_especie) VALUES ('Mestizo', 3); -- Ave
-- ...una fila por cada especie
```

**Problemas:**
- Requiere mantener una fila "Mestizo" por cada especie existente y por cada especie que se agregue en el futuro.
- Consultas de "¿cuántas mascotas de raza definida hay?" se complican porque hay que excluir todas las filas "Mestizo".
- El concepto de "Mestizo" no es una raza — es la *ausencia* de raza conocida. Modelarlo como raza distorsiona el catálogo.

#### Opción B — `id_raza = NULL` (adoptada)

```sql
-- Mascota con raza definida
INSERT INTO mascota (nombre, id_especie, id_raza, ...) VALUES ('Max', 1, 5, ...);

-- Mascota sin raza / mestiza
INSERT INTO mascota (nombre, id_especie, id_raza, ...) VALUES ('Firulais', 1, NULL, ...);
```

**Ventajas:**
- `NULL` expresa semánticamente "dato desconocido o no aplicable" — exactamente lo que representa el mestizaje o la ausencia de registro de raza.
- El catálogo `raza` queda limpio, conteniendo solo razas reales reconocidas.
- Las consultas son directas: `WHERE id_raza IS NULL` identifica mascotas sin raza; `WHERE id_raza IS NOT NULL` identifica las que sí tienen.
- No genera datos huérfanos si en el futuro se elimina o renombra el concepto.

### Tabla de casos de uso

| Caso | `id_especie` | `id_raza` | Descripción |
|---|---|---|---|
| Perro con raza definida | `1` (Perro) | `5` (Labrador) | Raza conocida y registrada |
| Perro sin raza / mestizo | `1` (Perro) | `NULL` | Raza desconocida o cruce |
| Gato con raza definida | `2` (Gato) | `12` (Siamés) | Raza conocida y registrada |
| Gato sin raza / mestizo | `2` (Gato) | `NULL` | Raza desconocida o cruce |
| Ave exótica sin raza | `3` (Ave) | `NULL` | Especie registrada, raza sin catalogar |

### Consultas frecuentes

```sql
-- Mascotas con raza definida
SELECT * FROM mascota WHERE id_raza IS NOT NULL;

-- Mascotas sin raza (mestizas o sin registro)
SELECT * FROM mascota WHERE id_raza IS NULL;

-- Mascotas por especie, indicando si tienen raza o no
SELECT m.nombre,
       e.nombre AS especie,
       COALESCE(r.nombre, 'Sin raza / Mestizo') AS raza
  FROM mascota m
  JOIN especie e ON e.id = m.id_especie
  LEFT JOIN raza r ON r.id = m.id_raza;
```

### Validación de consistencia especie-raza

La base de datos **no tiene un constraint** que impida asignar a una mascota una raza de otra especie (por ejemplo, asignar raza "Siamés" a un Perro). Esta validación se delega a la capa de aplicación:

- **Formulario web**: al seleccionar una especie, JavaScript filtra el dropdown de razas para mostrar únicamente las razas que pertenecen a esa especie. El usuario no puede seleccionar una raza de otra especie.
- **Capa de servicio** *(recomendación futura)*: validar en `MascotaServiceImpl.save()` que, cuando `idRaza != null`, la raza referenciada tenga el mismo `id_especie` que la mascota.

---

## 5. Reglas de integridad referencial

Todas las FK están definidas con `ON DELETE RESTRICT ON UPDATE CASCADE`:

| FK | Significado práctico |
|---|---|
| `raza.id_especie` | No se puede eliminar una especie que tenga razas |
| `mascota.id_especie` | No se puede eliminar una especie que tenga mascotas |
| `mascota.id_raza` | No se puede eliminar una raza asignada a una mascota |
| `mascota.id_propietario` | No se puede eliminar un propietario con mascotas activas |
| `cita.id_mascota` | No se puede eliminar una mascota con citas registradas |
| `consulta.id_cita` | No se puede eliminar una cita que ya tiene consulta |
| `receta.id_consulta` | No se puede eliminar una consulta con recetas |
| `historial_vacunacion.id_mascota` | No se puede eliminar una mascota con historial de vacunación |

> El borrado lógico (`activo = 0`) es la estrategia preferida para evitar estas restricciones en operaciones cotidianas. El borrado físico queda reservado para procesos administrativos controlados.
