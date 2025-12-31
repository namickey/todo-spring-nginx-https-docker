# Todo app with Spring Boot, Nginx, HTTPS, and Docker Compose

## Overview
A minimal TODO API backed by Spring Boot and H2, fronted by Nginx terminating HTTPS with a self-signed certificate. Nginx reverse proxies to the Spring Boot service and forwards standard `X-Forwarded-*` headers.

## Architecture
- `nginx`: Terminates HTTPS with a self-signed cert generated at build time, redirects HTTP to HTTPS, proxies to the app, and forwards client headers.
- `app`: Spring Boot REST API with JPA/H2 in-memory storage. Honors forwarded headers so redirects and generated links respect HTTPS.
- `docker-compose`: Orchestrates both containers; `nginx` depends on `app`.

## Prerequisites
- Docker and Docker Compose (or `docker compose`) installed.
　Docker Desktop (Windows/macOS) or Docker Engine + Docker Compose plugin (Linux).

## Run with Docker Compose
```sh
docker compose up --build
```
- Nginx: https://localhost (self-signed; use `-k` with curl or accept the warning in a browser).
- API (via Nginx): https://localhost/api/todos
- H2 console (direct to app): http://localhost:8080/h2-console

```sh
docker compose down
```

## API quickstart
Create a todo:
```sh
curl -k -X POST https://localhost/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Sample","completed":false}'
```
List todos:
```sh
curl -k https://localhost/api/todos
```
Update:
```sh
curl -k -X PUT https://localhost/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated","completed":true}'
```
Patch:
```sh
curl -k -X PATCH https://localhost/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"completed":false}'
```
Delete:
```sh
curl -k -X DELETE https://localhost/api/todos/1
```

## Web UI (Thymeleaf)
- Via Nginx: https://localhost/
- Direct (without Docker): http://localhost:8080/
- Create, toggle, and delete todos from the page; validation errors show inline.

## Security
- Spring Security form login enabled at `/login`.
- Default user: `user` / `password` (in-memory). Change in `SecurityConfig` or externalize credentials for production.

## Local development (without Docker)
```sh
./mvnw spring-boot:run
```
API will be at http://localhost:8080/api/todos. Forwarded headers are enabled; if you place another proxy in front, set `X-Forwarded-Proto` to `https` to get HTTPS-aware redirects/links.

## Notes
- The Nginx image generates a self-signed certificate for CN=localhost during build. Replace it with your own certs for production.
- HTTP traffic is redirected to HTTPS by Nginx; the app trusts forwarded headers (`ForwardedHeaderFilter` and `server.forward-headers-strategy=framework`).
- H2 is in-memory and non-persistent; replace with an external database for durable storage.
