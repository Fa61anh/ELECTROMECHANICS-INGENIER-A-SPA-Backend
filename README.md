# ⚡ ElectroTrack — Backend

## 📖 Descripción del proyecto

ElectroTrack es una aplicación móvil desarrollada para facilitar la gestión de servicios técnicos, jornadas laborales, actividades y evidencias fotográficas de una organización dedicada al área electromecánica.

El backend está desarrollado mediante una arquitectura de microservicios utilizando Spring Boot y PostgreSQL. Su objetivo es centralizar la información operacional y permitir la comunicación con la aplicación móvil Android mediante API REST, utilizando HTTPS y formato JSON.

La solución permite mejorar la organización de los servicios, controlar el avance de las actividades y facilitar la supervisión del trabajo realizado por los técnicos.

## 🎯 Objetivos

- Centralizar la gestión de servicios técnicos.
- Facilitar la asignación de trabajos a los técnicos.
- Registrar el inicio y término de las jornadas laborales.
- Controlar el avance de los servicios mediante actividades.
- Almacenar evidencias fotográficas.
- Facilitar la supervisión de las operaciones.
- Implementar una arquitectura modular mediante microservicios.

## 🛠️ Tecnologías utilizadas

| Tecnología | Utilización |
|---|---|
| Java 21 | Lenguaje de programación del backend |
| Spring Boot | Desarrollo de microservicios |
| Spring Data JPA | Persistencia de datos |
| Hibernate | Mapeo objeto-relacional |
| PostgreSQL | Base de datos relacional |
| Maven | Gestión de dependencias y compilación |
| JUnit 5 | Pruebas unitarias |
| Mockito | Simulación de dependencias en pruebas |
| Docker | Contenerización de microservicios |
| Docker Compose | Orquestación de contenedores |
| AWS EC2 | Despliegue del backend en la nube |
| Nginx | Proxy inverso |
| OpenSSL | Generación del certificado TLS |
| HTTPS | Comunicación cifrada |
| Git y GitHub | Control de versiones |

## 🏗️ Arquitectura del sistema

ElectroTrack utiliza una arquitectura de microservicios que permite separar las responsabilidades del backend en tres servicios independientes.

La aplicación Android se comunica mediante Retrofit con Nginx, que recibe las solicitudes HTTPS y las distribuye al microservicio correspondiente.

Cada microservicio administra sus datos mediante PostgreSQL, utilizando esquemas independientes dentro de una base de datos compartida.

La gestión de usuarios y autenticación se realiza localmente en la aplicación Android mediante SQLite y Room, por lo que no existe un microservicio de usuarios.

## 📦 Microservicios

### 1. Microservicio de Servicios

Puerto interno: **8082**

Responsable de gestionar los servicios técnicos registrados en la plataforma.

Funcionalidades:

- Crear servicios.
- Consultar servicios.
- Actualizar servicios.
- Eliminar servicios.
- Asignar técnicos.
- Controlar los estados de los servicios.

Estados disponibles:

- PENDIENTE
- EN_PROCESO
- FINALIZADO

Ruta principal: `/api/servicios`

### 2. Microservicio de Actividades y Evidencias

Puerto interno: **8083**

Responsable de registrar las actividades realizadas durante la ejecución de los servicios técnicos y almacenar sus evidencias fotográficas.

Funcionalidades:

- Registrar actividades.
- Consultar actividades.
- Registrar porcentajes de avance.
- Consultar actividades asociadas a un servicio.
- Almacenar evidencias fotográficas.
- Recuperar fotografías registradas.
- Validar archivos JPEG y PNG.
- Controlar el tamaño máximo de los archivos.

Rutas principales:

- `/api/actividades`
- `/api/evidencias`

### 3. Microservicio de Jornada

Puerto interno: **8084**

Responsable de gestionar las jornadas laborales de los técnicos.

Funcionalidades:

- Registrar el inicio de jornada.
- Registrar la finalización de jornada.
- Registrar coordenadas geográficas.
- Consultar jornadas por técnico.
- Validar la existencia de jornadas activas.
- Controlar los estados de las jornadas.

Estados disponibles:

- EN_CURSO
- FINALIZADA

Ruta principal: `/api/jornadas`

## 🗄️ Base de datos

El sistema utiliza PostgreSQL como motor de base de datos relacional.

Nombre de la base de datos: `electrotrack_db`

| Esquema | Tabla |
|---|---|
| servicios | servicios |
| actividades | actividades |
| actividades | evidencias |
| jornadas | jornadas |

La separación mediante esquemas permite organizar la información de acuerdo con las responsabilidades de cada microservicio.

Docker utiliza volúmenes persistentes para conservar los datos de PostgreSQL y las evidencias fotográficas.

## 📂 Estructura principal del repositorio

    ElectroTrack-backend/
    ├── servicios/
    │   ├── src/
    │   ├── Dockerfile
    │   ├── .dockerignore
    │   └── pom.xml
    ├── actividades/
    │   ├── src/
    │   ├── Dockerfile
    │   ├── .dockerignore
    │   └── pom.xml
    ├── jornada/
    │   ├── src/
    │   ├── Dockerfile
    │   ├── .dockerignore
    │   └── pom.xml
    ├── init-db/
    │   └── init.sql
    ├── docker-compose.yml
    ├── .gitignore
    └── README.md

## 🚀 Instalación y ejecución local

### Requisitos

- Git.
- Docker.
- Docker Compose.
- Java 21 y Maven para ejecutar pruebas o compilar fuera de Docker.

### 1. Clonar el repositorio

    git clone https://github.com/Fa61anh/ELECTROMECHANICS-INGENIER-A-SPA-Backend.git

Ingresar al directorio del repositorio.

### 2. Configurar las variables de entorno

Crear un archivo `.env` en la raíz del proyecto con las siguientes variables:

    DB_HOST=postgres
    DB_PORT=5432
    DB_NAME=electrotrack_db
    DB_USER=electrotrack_app
    DB_PASSWORD=REEMPLAZAR_CON_CONTRASENA_SEGURA

Las credenciales reales no deben almacenarse en GitHub.

### 3. Construir e iniciar los contenedores

    docker compose up -d --build

### 4. Verificar los contenedores

    docker compose ps

El entorno está compuesto por cuatro contenedores:

| Contenedor | Puerto en el host |
|---|---|
| electrotrack-postgres | 5434 |
| electrotrack-servicios | 8082 |
| electrotrack-actividades | 8083 |
| electrotrack-jornada | 8084 |

Los puertos se encuentran vinculados a la interfaz local del servidor.

### 5. Detener los contenedores

    docker compose down

Los volúmenes persistentes permiten conservar los datos al detener los contenedores sin eliminar dichos volúmenes.

## 🧪 Pruebas unitarias

El backend incorpora pruebas unitarias mediante JUnit y Mockito para verificar la lógica de negocio y las validaciones de los microservicios.

| Clase de pruebas | Cantidad |
|---|---:|
| ServicioServiceImplTest | 27 |
| ActividadesServiceImplTest | 20 |
| EvidenciaServiceImplTest | 24 |
| JornadaServiceImplTest | 35 |
| **Total** | **106** |

Las pruebas consideran escenarios válidos, datos incorrectos, registros inexistentes, validaciones de estados y manejo de excepciones.

Para ejecutar las pruebas desde Windows, ingresar al directorio del microservicio correspondiente y ejecutar:

    .\mvnw.cmd test

En Linux:

    ./mvnw test

## ☁️ Despliegue en AWS

El backend se encuentra desplegado en una instancia Amazon EC2 utilizando Amazon Linux, Docker y Docker Compose.

La arquitectura de despliegue considera:

- Una instancia EC2.
- Tres contenedores de microservicios.
- Un contenedor PostgreSQL.
- Volúmenes persistentes.
- Nginx como proxy inverso.
- Comunicación HTTPS mediante TLS.

La aplicación móvil utiliza una dirección HTTPS común para acceder a los tres microservicios.

Nginx distribuye las solicitudes hacia los puertos internos correspondientes.

## 🔒 Seguridad y comunicación

El sistema incorpora las siguientes medidas:

- Comunicación cifrada mediante HTTPS.
- Certificado TLS autofirmado, configurado como certificado de confianza en Android.
- Variables de entorno para las credenciales de PostgreSQL.
- Restricción del acceso externo mediante grupos de seguridad de AWS.
- Puertos internos de los microservicios sin exposición pública directa.
- Validación de datos en el backend.
- Validación del formato y tamaño de las evidencias fotográficas.
- Exclusión de archivos sensibles mediante `.gitignore`.

La autenticación de usuarios se administra mediante SQLite y Room en la aplicación Android. El backend no implementa autenticación propia ni autorización por roles, por lo que el acceso a sus API se mantiene restringido a redes autorizadas.

## 📱 Integración con Android

El backend se comunica con la aplicación móvil ElectroTrack, desarrollada mediante:

- Kotlin.
- Jetpack Compose.
- Arquitectura MVVM.
- Retrofit.
- Room y SQLite.
- Recursos nativos de cámara y geolocalización.

La aplicación cuenta con dos roles:

**Administrador:** gestión de técnicos, creación y asignación de servicios y supervisión de las operaciones.

**Técnico:** consulta de servicios asignados, registro de jornadas, actividades, avances y evidencias fotográficas.

La integración fue probada desde el emulador de Android Studio, utilizando los microservicios desplegados en AWS mediante HTTPS.

## 📊 Estado del proyecto

- [x] Desarrollo de microservicios.
- [x] Implementación de PostgreSQL.
- [x] Pruebas unitarias con JUnit.
- [x] Configuración de Docker y Docker Compose.
- [x] Despliegue en AWS EC2.
- [x] Configuración de Nginx.
- [x] Implementación de HTTPS.
- [x] Integración con Android mediante Retrofit.
- [x] Pruebas funcionales en el emulador.
- [x] Generación de APK firmada.
- [ ] Validación final en dispositivo físico.

## ⚠️ Consideraciones del despliegue

La disponibilidad de las funcionalidades remotas depende de que la instancia EC2 permanezca activa.

Si la dirección pública de la instancia cambia, será necesario actualizar la configuración HTTPS y las direcciones de conexión de la aplicación Android.

El acceso al backend se encuentra restringido a las direcciones IP autorizadas en AWS.

## 🎓 Contexto académico

Proyecto desarrollado en el contexto académico de Duoc UC, aplicando conocimientos de desarrollo de aplicaciones móviles, arquitectura de microservicios, bases de datos relacionales, pruebas unitarias y despliegue de soluciones en la nube.

---

**ElectroTrack — Gestión digital de servicios electromecánicos.**
