# Gestión de Funcionarios - Aplicación de Escritorio (Swing)

Aplicación de escritorio desarrollada en **Java (JDK 21)** y **Swing** para la gestión de funcionarios e inventarios, implementando el patrón **DAO** (Data Access Object) con JDBC.

Incluye **autenticación por email/contraseña**, **roles** y control de acceso por módulo.

## 🚀 Tecnologías Utilizadas

*   **Lenguaje:** Java 21
*   **Interfaz Gráfica:** Java Swing
*   **Gestor de Dependencias:** Maven
*   **Base de Datos:** PostgreSQL
*   **Pool de Conexiones:** HikariCP
*   **Logging:** SLF4J
*   **Seguridad:**
    *   Hash de contraseñas con **BCrypt**
    *   Sesión con **JWT** (almacenado en memoria en `SessionContext`)

## 🛠️ Características Principales

*   **Login** por email y contraseña.
*   **Roles**: `ADMINISTRADOR` y `DOCENTE`.
*   **Permisos por rol:**
    *   **ADMINISTRADOR**: CRUD de Usuarios (Funcionarios), Inventarios y Catálogos (Estados/Marcas/Tipos).
    *   **DOCENTE**: solo puede **visualizar Inventarios** (modo solo lectura).
*   **CRUD Completo (Admin):** Permite Crear, Leer (Listar), Actualizar y Eliminar registros.
*   **Patrón Arquitectónico:** Implementación estricta del patrón DAO para abstraer y encapsular todos los accesos a la base de datos.
*   **Gestión de Conexiones:** Uso de `HikariCP` para un pool de conexiones a la base de datos eficiente y robusto.
*   **Seguridad y Configuración:** Las credenciales de la base de datos están externalizadas en un archivo `db.properties` (excluido del control de versiones).

## ⚙️ Requisitos Previos

Antes de ejecutar la aplicación, asegúrate de tener instalado:

*   Java Development Kit **(JDK 21)** o superior.
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
    *   `sql/01_schema.sql`: Esquema base (incluye `password_hash` y `rol` en `funcionarios`).
    *   `sql/02_data.sql`: Datos base del módulo de funcionarios.
    *   `sql/03_security_roles.sql`: Refuerza seguridad (enum de roles y ajustes; seguro de ejecutar si la BD ya existía).
    *   `sql/04_inventarios_schema.sql`: Esquema de inventarios y catálogos.
    *   `sql/05_inventarios_data.sql`: Datos de inventarios/catálogos (opcional pero recomendado para pruebas).

    Si ya tienes datos y solo quieres asignar contraseña/rol de prueba:
    *   `sql/update_passwords.sql`

3.  **Configurar Credenciales (`db.properties`):**
    En el directorio raíz del proyecto (junto al archivo `pom.xml`), crea un archivo llamado `db.properties` con el siguiente contenido, ajustando los valores a tu configuración local:

    ```properties
    db.url=jdbc:postgresql://localhost:5432/gestion_funcionarios
    db.user=tu_usuario_postgres
    db.password=tu_contraseña
    ```
    *Nota: Este archivo está incluido en el `.gitignore` para no exponer credenciales sensibles en el repositorio.*

## 🔐 Usuarios de prueba / Credenciales

Si ejecutas `sql/03_security_roles.sql` o `sql/update_passwords.sql`, la contraseña de prueba configurada es:

- **Contraseña**: `Admin123*`

Los usuarios de prueba dependen de los datos cargados en `sql/02_data.sql`. Por ejemplo, típicamente se incluyen correos como:
- `caramirez@entidad.gov.co` (suele quedar como `ADMINISTRADOR` en scripts de prueba)

Si un usuario está en estado `INACTIVO`, el sistema bloqueará el ingreso.

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

## 🧭 Navegación en la aplicación (según rol)

- **ADMINISTRADOR**: verá pestañas para **Usuarios**, **Inventarios** y **Catálogos**.
- **DOCENTE**: verá solo **Inventarios** en modo **solo lectura**.

## 📂 Estructura del Proyecto

*   `src/main/java/com/gestion/funcionarios/`: Código fuente principal de la aplicación, organizado por capas (config, dao, model, ui).
*   `sql/`: Scripts de creación y poblamiento de la base de datos.
*   `pom.xml`: Archivo de configuración de Maven con las dependencias y plugins del proyecto.
*   `db.properties`: Archivo (local) de configuración para la conexión a la base de datos.
