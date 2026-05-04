# Gestión de Funcionarios - Aplicación CRUD

Una aplicación de escritorio desarrollada en **Java 17** y **Swing** para la gestión de funcionarios, implementando el patrón de diseño **DAO** (Data Access Object) para una separación limpia entre la interfaz de usuario y la lógica de acceso a datos.

## 🚀 Tecnologías Utilizadas

*   **Lenguaje:** Java 17
*   **Interfaz Gráfica:** Java Swing
*   **Gestor de Dependencias:** Maven
*   **Base de Datos:** PostgreSQL
*   **Pool de Conexiones:** HikariCP
*   **Logging:** SLF4J

## 🛠️ Características Principales

*   **CRUD Completo:** Permite Crear, Leer (Listar), Actualizar y Eliminar registros de funcionarios.
*   **Patrón Arquitectónico:** Implementación estricta del patrón DAO para abstraer y encapsular todos los accesos a la base de datos.
*   **Gestión de Conexiones:** Uso de `HikariCP` para un pool de conexiones a la base de datos eficiente y robusto.
*   **Seguridad y Configuración:** Las credenciales de la base de datos están externalizadas en un archivo `db.properties` (excluido del control de versiones).

## ⚙️ Requisitos Previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:

*   [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) o superior.
*   [Apache Maven](https://maven.apache.org/download.cgi)
*   [PostgreSQL](https://www.postgresql.org/download/) (versión 12 o superior recomendada).

## 🗄️ Configuración de la Base de Datos

1.  **Crear la Base de Datos:**
    Abre tu cliente de PostgreSQL (como pgAdmin o psql) y crea una base de datos llamada `gestion_funcionarios`:
    ```sql
    CREATE DATABASE gestion_funcionarios;
    ```

2.  **Ejecutar los Scripts SQL:**
    En la carpeta `sql/` del proyecto, encontrarás los scripts necesarios. Ejecútalos en tu base de datos en el siguiente orden:
    *   `sql/01_schema.sql`: Crea las tablas y la estructura de la base de datos.
    *   `sql/02_data.sql`: (Opcional) Inserta datos de prueba iniciales.

3.  **Configurar Credenciales (`db.properties`):**
    En el directorio raíz del proyecto (junto al archivo `pom.xml`), crea un archivo llamado `db.properties` con el siguiente contenido, ajustando los valores a tu configuración local:

    ```properties
    db.url=jdbc:postgresql://localhost:5432/gestion_funcionarios
    db.user=tu_usuario_postgres
    db.password=tu_contraseña
    ```
    *Nota: Este archivo está incluido en el `.gitignore` para no exponer credenciales sensibles en el repositorio.*

## 🚀 Compilación y Ejecución

1.  **Navegar a la carpeta del proyecto:**
    Abre tu terminal y ubícate en el directorio del proyecto (donde se encuentra `pom.xml`).

2.  **Compilar y empaquetar con Maven:**
    ```bash
    mvn clean package
    ```

3.  **Ejecutar la aplicación:**
    Puedes ejecutar la aplicación directamente usando Java con el JAR generado (el plugin de Maven copiará las dependencias en la carpeta `target/lib` automáticamente):
    ```bash
    java -jar target/funcionarios-app-1.0.0.jar
    ```
    Alternativamente, puedes ejecutar la clase principal `com.gestion.funcionarios.Main` desde tu IDE.

## 📂 Estructura del Proyecto

*   `src/main/java/com/gestion/funcionarios/`: Código fuente principal de la aplicación, organizado por capas (config, dao, model, ui).
*   `sql/`: Scripts de creación y poblamiento de la base de datos.
*   `pom.xml`: Archivo de configuración de Maven con las dependencias y plugins del proyecto.
*   `db.properties`: Archivo (local) de configuración para la conexión a la base de datos.
