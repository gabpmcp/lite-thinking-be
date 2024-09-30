# Usa la imagen base de OpenJDK para Java 17
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR de la aplicación al contenedor
COPY target/litethinking-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que corre tu aplicación (8080 es típico en Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
