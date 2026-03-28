# Guía de despliegue — Servidor Docker (docker-server)

## Índice

1. [Arquitectura del servidor](#arquitectura-del-servidor)
2. [El enrutador central: nginx-proxy](#el-enrutador-central-nginx-proxy)
3. [Proyectos desplegados actualmente](#proyectos-desplegados-actualmente)
4. [Cómo incorporar un nuevo proyecto](#cómo-incorporar-un-nuevo-proyecto)
5. [Estructura de directorios en el servidor](#estructura-de-directorios-en-el-servidor)
6. [Referencia rápida de comandos](#referencia-rápida-de-comandos)

---

## Arquitectura del servidor

El servidor ejecuta múltiples proyectos web en paralelo, todos conviviendo en el mismo host mediante contenedores Docker. El acceso desde el exterior pasa por un único punto de entrada: **nginx-proxy**.

```
Internet
    │
    ▼ puerto 80
┌─────────────────────────────────────────────────────┐
│                    nginx-proxy                      │
│           (nginxproxy/nginx-proxy)                  │
│                                                     │
│  Lee docker.sock y genera rutas automáticamente     │
│  según el dominio en la cabecera HTTP Host          │
└──────┬──────────────────┬──────────────────┬────────┘
       │                  │                  │
       ▼                  ▼                  ▼
  petsalud-app      clientes-api       [proyecto N]
  :8080             :8000              :XXXX
  (red nginx-proxy  (red nginx-proxy   (red nginx-proxy
   + red-interna)    + red propia)      + red propia)
       │
       ▼
  petsalud-basedatos
  (solo red-interna, no expuesto)
```

**Principios clave:**

- `nginx-proxy` ocupa el puerto 80 del host. **Ningún otro contenedor puede usar ese puerto.**
- Cada proyecto tiene su propia red interna Docker. Las bases de datos solo son accesibles desde esa red.
- La comunicación entre nginx-proxy y una app ocurre a través de la red compartida `nginx-proxy`.
- El enrutamiento se configura por dominio DNS: cada subdominio apunta a un contenedor diferente.

---

## El enrutador central: nginx-proxy

### Cómo funciona

`nginx-proxy` usa la imagen `nginxproxy/nginx-proxy`. Al arrancar monta el socket de Docker:

```
/var/run/docker.sock → /tmp/docker.sock
```

Esto le permite escuchar eventos del daemon: cuando un contenedor arranca o se detiene, nginx-proxy regenera automáticamente su configuración interna de nginx. No hay que tocar ningún archivo de configuración manualmente para añadir una nueva app.

### Detección automática

Para que nginx-proxy enrute tráfico a un contenedor, ese contenedor debe:

1. Tener la variable de entorno `VIRTUAL_HOST=mi.dominio.com`
2. Tener la variable `VIRTUAL_PORT=XXXX` con el puerto interno que escucha la app
3. Estar conectado a la red Docker llamada `nginx-proxy`

Con esas tres condiciones, nginx-proxy generará automáticamente:

```nginx
server {
    listen 80;
    server_name mi.dominio.com;
    location / {
        proxy_pass http://<nombre-del-contenedor>:XXXX;
    }
}
```

### Red compartida

La red `nginx-proxy` es **externa** (creada fuera de cualquier compose). Fue creada manualmente una sola vez:

```bash
docker network create nginx-proxy
```

Todos los proyectos que quieran ser enrutados deben unirse a ella declarándola como red externa en su `docker-compose.yml`.

### Configuración manual existente

Existe un `nginx.conf` en `~/nginx-proxy/nginx.conf` con una ruta configurada a mano para un proyecto antiguo. Las nuevas apps **no deben** editar ese archivo — deben usar el mecanismo de `VIRTUAL_HOST`.

---

## Proyectos desplegados actualmente

| Proyecto | Carpeta en `~/apps` | Dominio | Puerto interno |
|---|---|---|---|
| PetSalud (Spring Boot) | `petsalud/` | `petsalud.docker.sulbaranjc.com` | 8080 |
| Clientes API | `clientes_api/` | — | 8000 |
| Clientes Monolito Docker | `clientes-monolito-docker/` | — | 8000 |
| Clientes Frontend React | `clientes_frontend_react/` | — | 80 |
| Validación de Formularios | `ValidacionDeFormularios/` | — | 80 |
| Plantilla Base | `plantilla-base/` | — | — |
| Dockhand (panel Docker) | — | `127.0.0.1:3000` | 3000 |

---

## Cómo incorporar un nuevo proyecto

### Paso 1 — Preparar el `docker-compose.yml` del proyecto

El `docker-compose.yml` del nuevo proyecto debe cumplir dos requisitos respecto a la arquitectura del servidor:

**a) El servicio de la app no publica el puerto 80 en el host:**

```yaml
# ❌ Esto colisiona con nginx-proxy
ports:
  - "80:8080"

# ✅ Solo expone el puerto internamente
expose:
  - "8080"
```

**b) El servicio de la app declara las variables de enrutamiento y se une a la red `nginx-proxy`:**

```yaml
services:
  aplicacion:
    # ...
    environment:
      VIRTUAL_HOST: ${VIRTUAL_HOST:-mi-app.docker.sulbaranjc.com}
      VIRTUAL_PORT: "8080"          # puerto en que escucha la app dentro del contenedor
    networks:
      - red-interna                  # red privada del proyecto
      - proxy-externo                # red compartida con nginx-proxy

# Al final del compose, declarar la red del proxy como externa:
networks:
  red-interna:
    name: mi-app-red
    driver: bridge
  proxy-externo:
    external: true
    name: nginx-proxy               # debe llamarse exactamente "nginx-proxy"
```

**c) La base de datos (si existe) solo se conecta a la red interna:**

```yaml
  basedatos:
    # ...
    networks:
      - red-interna                  # nunca a proxy-externo
```

### Paso 2 — Preparar el `.env`

Crear un `.env` en la raíz del proyecto (nunca subir al repositorio):

```bash
cp .env.example .env
```

El `.env` debe incluir como mínimo:

```
VIRTUAL_HOST=mi-app.docker.sulbaranjc.com
```

Y las credenciales de base de datos si aplica.

### Paso 3 — Clonar y desplegar en el servidor

```bash
# Conectarse al servidor
ssh docker-server

# Entrar al directorio de proyectos
cd ~/apps

# Clonar el repositorio
git clone <url-del-repositorio> nombre-del-proyecto
cd nombre-del-proyecto

# Crear el .env con valores reales
cp .env.example .env
nano .env          # ajustar credenciales y VIRTUAL_HOST

# Levantar la arquitectura
docker compose up -d --build
```

### Paso 4 — Verificar el enrutamiento

```bash
# Comprobar que los contenedores están corriendo
docker ps --filter name=mi-app

# Ver los logs de la app
docker logs mi-app-container --tail 50

# Verificar que nginx-proxy detectó el contenedor
docker logs nginx-proxy --tail 30
```

Si todo está bien, la app será accesible en `http://mi-app.docker.sulbaranjc.com` en pocos segundos, sin reiniciar nginx-proxy.

### Paso 5 — Actualizar una versión ya desplegada

```bash
ssh docker-server
cd ~/apps/nombre-del-proyecto
git pull
docker compose up -d --build
```

---

## Estructura de directorios en el servidor

```
~/ (home del usuario en docker-server)
├── apps/                        ← todos los proyectos desplegados
│   ├── petsalud/
│   │   ├── .env                 ← credenciales reales (no en git)
│   │   ├── docker-compose.yml
│   │   └── ...
│   ├── clientes/
│   ├── plantilla-base/
│   └── ...
└── nginx-proxy/
    └── nginx.conf               ← configuración manual heredada (no modificar)
```

---

## Referencia rápida de comandos

```bash
# Ver todos los contenedores del servidor
docker ps

# Ver redes Docker existentes
docker network ls

# Ver logs de un contenedor
docker logs <nombre-contenedor> --tail 100 -f

# Detener un proyecto
cd ~/apps/nombre-proyecto
docker compose down

# Detener y eliminar volúmenes (¡borra la BD!)
docker compose down -v

# Reconstruir imagen sin usar caché
docker compose build --no-cache

# Liberar espacio (imágenes y capas sin usar)
docker system prune -f

# Inspeccionar a qué redes pertenece un contenedor
docker inspect <nombre-contenedor> --format '{{json .NetworkSettings.Networks}}' | python3 -m json.tool
```

---

## Diagrama de redes Docker

```
Red: nginx-proxy (172.21.0.0/16)   ← compartida entre nginx-proxy y todas las apps
  ├── nginx-proxy       172.21.0.3
  ├── petsalud-app      172.21.x.x
  └── [otras apps]      172.21.x.x

Red: petsalud-red                  ← privada de PetSalud
  ├── petsalud-app
  └── petsalud-basedatos           ← solo visible aquí

Red: clientes_api_backend          ← privada de Clientes API
  ├── clientes-api
  └── clientes-api-db

[cada proyecto tiene su red privada equivalente]
```

Las bases de datos **nunca** se conectan a la red `nginx-proxy`. Solo los contenedores de aplicación lo hacen, y únicamente para que nginx-proxy pueda alcanzarlos por HTTP.
