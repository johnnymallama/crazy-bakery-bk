# Crazy Bakery API Backend

Este proyecto contiene el backend de la aplicación Crazy Bakery, una API RESTful construida con Java y Spring Boot. El proyecto está configurado para ser ejecutado localmente para desarrollo y para ser desplegado en Google Cloud Run.

## Requisitos Previos

- **Java 17:** Asegúrate de tener el JDK 17 instalado.
- **Apache Maven:** Para la gestión de dependencias y la construcción del proyecto.
- **Google Cloud SDK:** Necesario para interactuar con Google Cloud (especialmente para la autenticación).

## Ejecución en Entorno Local

Para el desarrollo local, la aplicación utiliza una base de datos en memoria (H2) para no depender de una instancia externa de MySQL. La configuración se encuentra en `src/main/resources/application-dev.properties`.

1.  **Activar el Perfil de Desarrollo:**
    Spring Boot utiliza el perfil `dev` por defecto si no se especifica otro. La configuración en `application-dev.properties` habilita la consola de H2 y define las propiedades de la base de datos.

2.  **Ejecutar la Aplicación:**
    Abre una terminal en la raíz del proyecto y ejecuta el siguiente comando de Maven:
    ```bash
    mvn spring-boot:run
    ```

3.  **Acceder a la Aplicación:**
    - La API estará disponible en `http://localhost:8080`.
    - La consola de la base de datos H2 estará disponible en `http://localhost:8080/h2-console`. Usa la URL JDBC `jdbc:h2:mem:testdb` para conectarte.

## Construcción y Despliegue en Google Cloud

El despliegue en producción se realiza mediante la "contenerización" de la aplicación y su posterior ejecución en Cloud Run.

### Paso 1: Autenticación en Google Cloud

Antes de construir, asegúrate de que tu entorno local esté autenticado con Google Cloud y que Docker esté configurado para usar tus credenciales.

```bash
# Inicia sesión con tu cuenta de usuario
gcloud auth login

# Configura las credenciales por defecto para las librerías cliente
gcloud auth application-default login

# Configura el asistente de credenciales para Docker
gcloud auth configure-docker us-central1-docker.pkg.dev
```

### Paso 2: Construir y Publicar la Imagen del Contenedor

El proyecto utiliza el plugin **Jib** de Maven para construir y publicar la imagen de Docker directamente, sin necesidad de tener Docker instalado localmente.

Para construir y publicar la imagen en Artifact Registry, ejecuta:

```bash
mvn compile jib:build
```

### Paso 3: Desplegar en Cloud Run

Una vez que la nueva imagen está en Artifact Registry, puedes crear o actualizar el servicio en Cloud Run desde la [Consola de Google Cloud](https://console.cloud.google.com/).

**Configuración Clave al Crear el Servicio:**

1.  **Imagen del Contenedor:** Selecciona la imagen `crazy-bakery-bk` que acabas de publicar.
2.  **Autenticación:** Marca `Permitir invocaciones no autenticadas` para que la API sea pública.
3.  **Conexiones > Cloud SQL:** Añade la conexión a tu instancia de base de datos (`uan-especializacion:us-central1:uan-especializacion`).
4.  **Variables y Secretos:** Añade las siguientes variables de entorno para configurar la conexión a la base de datos de producción (MySQL). Spring Boot las usará automáticamente.

| Nombre de la Variable | Valor de Ejemplo |
| :--- | :--- |
| `SPRING_DATASOURCE_USERNAME` | `crazy_bakery_user` |
| `SPRING_DATASOURCE_PASSWORD` | `TuContraseñaSecreta` |
| `DB_NAME` | `crazy-bakery` |
| `INSTANCE_CONNECTION_NAME` | `uan-especializacion:us-central1:uan-especializacion` |
## Prueba 
mvn compile jib:build
