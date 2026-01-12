# Angular Bank – Sistema Bancario Full Stack

Proyecto  Full Stack que implementa un sistema bancario básico utilizando **Angular**, **Spring Boot**, **PostgreSQL** y **Docker**.

La aplicación permite la gestión de clientes, cuentas, movimientos bancarios y reportes financieros, todo desplegado mediante contenedores Docker.

---

## 🧩 Tecnologías utilizadas

### Frontend
- Angular
- TypeScript
- HTML5 / CSS3
- Nginx (para despliegue)

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate

### Base de datos
- PostgreSQL 17

### DevOps
- Docker
- Docker Compose

---

## 📂 Estructura del proyecto

angular-bank/
│
├── bank-frontend/ # Aplicación Angular
├── bank-backend/ # API REST con Spring Boot
├── docker-compose.yml # Orquestación de contenedores
├── postman/ # Colección Postman para pruebas
└── README.md


---

## 🚀 Ejecución del proyecto (paso a paso)

### Requisitos
- Docker
- Docker Compose

### Pasos para levantar el sistema

Desde la carpeta raíz del proyecto:

```bash
docker compose up -d --build
🌐 Accesos del sistema
Servicio	URL
Frontend	http://localhost:4200
Backend	http://localhost:8081
Base de datos	localhost:5432

🔌 Endpoints principales (API REST)
Clientes
GET /clientes

POST /clientes

PUT /clientes/{id}

DELETE /clientes/{id}

Cuentas
GET /cuentas

GET /cuentas/cliente/{clienteId}

POST /cuentas/cliente/{clienteId}

PUT /cuentas/{cuentaId}

DELETE /cuentas/{cuentaId}

Movimientos
POST /movimientos

GET /movimientos/cuenta/{numeroCuenta}

Reportes
GET /reportes/estado-cuenta

GET /reportes/cuentas-por-cliente

GET /reportes/estado-cuenta-por-cliente

GET /reportes/movimientos-por-cliente

GET /reportes/movimientos-por-cliente/pdf

🧪 Pruebas con Postman
Dentro del repositorio se incluye una carpeta postman/ que contiene:

Colección Postman (Angular Bank API.postman_collection.json)

Variable base_url configurada como:
http://localhost:8081
Esto permite validar todos los endpoints de forma inmediata.

📌 Notas importantes
La base de datos se ejecuta dentro de un contenedor Docker.
Los datos se persisten en un volumen Docker.
El frontend consume la API REST expuesta por el backend.
Todo el sistema puede levantarse con un solo comando.

👤 Autor
J. Francisco Luzuriaga
Ejercicio FullStack – Entrega final
