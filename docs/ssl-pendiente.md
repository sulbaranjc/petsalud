# SSL para el servidor Docker — Pendiente de implementar

## Estado actual

El servidor corre detrás de NAT (Proxmox). `nginx-proxy` solo escucha en el puerto 80.
No hay certificados SSL configurados. Las apps son accesibles únicamente por HTTP.

---

## Decisiones tomadas

| Pregunta | Respuesta |
|---|---|
| ¿Está detrás de NAT? | Sí — HTTP-01 challenge descartado |
| ¿Proveedor DNS? | Hostinger |
| ¿Estrategia elegida? | DNS-01 challenge con `acme-companion` + driver `dns_hostinger` |

---

## Arquitectura objetivo

```
Internet
    │
    ├── puerto 80  → nginx-proxy → redirige automáticamente a HTTPS
    └── puerto 443 → nginx-proxy → termina TLS → app:XXXX

nginx-proxy  ←── volumen compartido "certs" ───→  acme-companion
                                                        │
                                                        ▼
                                               API de Hostinger DNS
                                               (crea registro TXT _acme-challenge)
                                                        │
                                                        ▼
                                               Let's Encrypt emite certificado
```

`acme-companion` renueva los certificados automáticamente antes de que expiren (cada ~60 días).

---

## Pasos de implementación

### Paso 1 — Obtener la API Key de Hostinger

En hPanel: **Cuenta → API** (o sección de Seguridad del perfil).

Guardar el valor — se usará como variable de entorno en `acme-companion`.

---

### Paso 2 — Recrear nginx-proxy con soporte HTTPS

El nginx-proxy actual solo tiene el puerto 80. Hay que recrearlo añadiendo:
- Puerto `443:443`
- Tres volúmenes compartidos con `acme-companion`: `certs`, `vhost.d`, `html`

Ejemplo de cómo debería quedar el compose de nginx-proxy:

```yaml
services:
  nginx-proxy:
    image: nginxproxy/nginx-proxy
    container_name: nginx-proxy
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /var/run/docker.sock:/tmp/docker.sock:ro
      - certs:/etc/nginx/certs
      - vhost:/etc/nginx/vhost.d
      - html:/usr/share/nginx/html
    networks:
      - nginx-proxy

  acme-companion:
    image: nginxproxy/acme-companion
    container_name: acme-companion
    restart: unless-stopped
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock:ro
      - certs:/etc/nginx/certs
      - vhost:/etc/nginx/vhost.d
      - html:/usr/share/nginx/html
      - acme:/etc/acme.sh
    environment:
      DEFAULT_EMAIL: tu@email.com
      ACMESERVER: https://acme-v02.api.letsencrypt.org/directory
      ACME_DNS_API: dns_hostinger
      HOSTINGER_API_KEY: ${HOSTINGER_API_KEY}
    networks:
      - nginx-proxy

volumes:
  certs:
  vhost:
  html:
  acme:

networks:
  nginx-proxy:
    external: true
    name: nginx-proxy
```

> **Atención**: recrear nginx-proxy causa un breve corte de todos los proyectos.
> Planificar en horario de baja actividad.

---

### Paso 3 — Añadir variables SSL a cada proyecto

En el `.env` de cada proyecto añadir:

```
LETSENCRYPT_HOST=nombre.docker.sulbaranjc.com
LETSENCRYPT_EMAIL=tu@email.com
```

Y en el `docker-compose.yml` del proyecto, dentro del servicio de la app:

```yaml
environment:
  VIRTUAL_HOST: ${VIRTUAL_HOST:-nombre.docker.sulbaranjc.com}
  VIRTUAL_PORT: "XXXX"
  LETSENCRYPT_HOST: ${LETSENCRYPT_HOST:-nombre.docker.sulbaranjc.com}
  LETSENCRYPT_EMAIL: ${LETSENCRYPT_EMAIL:-tu@email.com}
```

---

### Paso 4 — Verificar que funciona

```bash
# Ver logs de acme-companion para seguir el proceso de emisión
docker logs acme-companion -f

# Verificar que el certificado fue emitido
docker exec acme-companion acme.sh --list

# Comprobar acceso HTTPS
curl -I https://petsalud.docker.sulbaranjc.com
```

La primera emisión puede tardar 1-2 minutos por dominio.

---

## Advertencia sobre `dns_hostinger`

El driver `dns_hostinger` en `acme.sh` es relativamente reciente y funciona, pero es menos
maduro que el de Cloudflare. Si se presentan problemas, la alternativa más robusta es:

1. Cambiar los nameservers de `sulbaranjc.com` a Cloudflare (gratis, solo en hPanel)
2. Usar el driver `dns_cloudflare` con un API Token de Cloudflare
3. Con Cloudflare también sería posible usar **Cloudflare Tunnel** (`cloudflared`),
   que elimina la necesidad de abrir el puerto 443 en el router

---

## Resumen de variables de entorno necesarias

| Variable | Dónde se define | Valor |
|---|---|---|
| `HOSTINGER_API_KEY` | `.env` del compose de nginx-proxy | API Key de hPanel |
| `DEFAULT_EMAIL` | compose de nginx-proxy | email para notificaciones de expiración |
| `LETSENCRYPT_HOST` | `.env` de cada proyecto | mismo valor que `VIRTUAL_HOST` |
| `LETSENCRYPT_EMAIL` | `.env` de cada proyecto | mismo email |
