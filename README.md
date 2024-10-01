# lite-thinking-be

This is a Spring Boot application built using Gradle. The application uses a PostgreSQL database, which can be set up using Docker. This README provides instructions on how to set up and run the application, including configuring environment variables through a `.env` file.

## Table of Contents

- [lite-thinking-be](#lite-thinking-be)
  - [Table of Contents](#table-of-contents)
  - [Requirements](#requirements)
  - [Environment Variables](#environment-variables)
    - [Security Configuration](#security-configuration)
    - [PostgreSQL Configuration](#postgresql-configuration)
    - [Mails Configuration](#mails-configuration)
  - [Local Server Deploy](#local-server-deploy)

## Requirements

Make sure you have the following installed:

- [Java 21](https://adoptopenjdk.net/)
- [Gradle](https://gradle.org/install/) (or use the Gradle wrapper)
- [Docker](https://www.docker.com/get-started)
- [PostgreSQL](https://www.postgresql.org/) (optional, if running the database locally)

## Environment Variables

The application requires some environment variables to be configured. These are stored in a `.env` file. A sample `.env` file is provided below. You can customize these variables according to your local setup:

### Security Configuration

Para obtener el token debe usar una llave firmada de al menos 32 bits. Se ha generado una por defecto con este valor: `JWT_SECRET=qAGlLhYySAHeXa5L9aO3vZHrn65KphZGLb1l9rXEepI=`. Luego, usa este cURL para establecer el token de autenticación para usar las demás funcionalidades. Este token de autenticación tiene un TTL de 1 hora, así que si se vence, debes logearte de nuevo.

```cURL
curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "admin",
    "password": "admin123"
}'
```

### PostgreSQL Configuration

Para el uso de la DB, debes iniciar un contenedor para la DB usando:

```bash
docker run --name postgres-litethinking -e POSTGRES_USER=FAKE_USER -e POSTGRES_PASSWORD=FAKE_PASS -e POSTGRES_DB=litethinking -p 5432:5432 -d postgres
```

Luego, debes asegurar que existan estas llaves:

`DB_USER={FAKE_USER}`
`DB_PASS={FAKE_PASS}`
`DB_URL=jdbc:postgresql://localhost:5432/litethinking`

> Nota importante: Tenga en cuenta que `DB_USER` y `DB_PASS` deben coincidir con los valores pasados al comando de creación del contenedor de DB.

### Mails Configuration

Para el envío de correo, debes informar previamente al administrador del sistema el correo en el que quieres recibir notificación. Luego, configura las credenciales de SMTP que provee AWS:

IAM_USER_NAME={IAM_USER_NAME}
SMTP_USER_NAME={SMTP_USER_NAME}
SMTP_PASSWORD={SMTP_PASSWORD}
SMTP_HOST={SMTP_HOST}
SMTP_PORT=587

## Local Server Deploy

Para desplegar un servidor local de una aplicación de Spring Boot utilizando la línea de comandos (CLI), sigue estos pasos:

- Navega a la carpeta del proyecto: Abre una terminal o línea de comandos y navega al directorio raíz del proyecto de Spring Boot.
- Ejecuta `./gradlew build`.
- Ejecuta `./gradlew bootRun`

Si no has cambiado la configuración por defecto, el servidor estará disponible en <http://localhost:8080>.
