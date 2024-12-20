# Mis Novelas

Una aplicación móvil para gestionar una lista de novelas. Los usuarios pueden añadir, eliminar, ver detalles, marcar como favoritas y agregar reseñas a las novelas. La aplicación está construida utilizando **Jetpack Compose** y **SQLite** para almacenamiento local.

## Características

- **Añadir novelas**: Permite al usuario agregar novelas con detalles como nombre, año, descripción y valoración.
- **Eliminar novelas**: Los usuarios pueden eliminar novelas de su lista.
- **Marcar como favoritas**: Los usuarios pueden marcar o desmarcar novelas como favoritas.
- **Ver detalles de la novela**: Los usuarios pueden ver los detalles completos de cada novela.
- **Filtrar novelas**: Los usuarios pueden ver todas las novelas o solo las favoritas.
- **Mapa de novelas**: Muestra un mapa con las ubicaciones de las novelas.
- **Ubicación actual**: Permite al usuario ver su ubicación actual en el mapa.

## Tecnologías utilizadas

- **Jetpack Compose**: Biblioteca de UI moderna y declarativa para crear interfaces de usuario.
- **SQLite**: Base de datos local para almacenar la información de las novelas y los usuarios.
- **SharedPreferences**: Para almacenar la sesión del usuario actual.
- **Kotlin**: Lenguaje de programación utilizado para el desarrollo de la aplicación.
- **Google Maps**: Para mostrar las ubicaciones de las novelas en un mapa.
- **Google Location Services**: Para obtener la ubicación actual del usuario.

## Instalación

### 1. Clonar el repositorio

## Instrucciones para ejecutar el proyecto

1. Clonar el repositorio:
    ```bash
    git clone https://github.com/tuusuario/misnovelas.git
    cd misnovelas
    ```

2. Abrir el proyecto en Android Studio:
    - Abre Android Studio y selecciona "Open an existing project".
    - Luego, selecciona la carpeta donde clonaste el proyecto.

3. Ejecutar la app:
    - Asegúrate de tener un dispositivo o emulador configurado.
    - Luego, ejecuta el proyecto desde Android Studio.

## Funcionalidades

### Pantalla de lista de novelas
- Muestra todas las novelas del usuario.
- Permite ver detalles y marcar como favoritas.

### Pantalla de añadir novela
- Permite al usuario agregar una nueva novela con nombre, año, descripción y valoración.
- Verifica que todos los campos sean correctos antes de permitir el envío.

### Pantalla de eliminar novela
- Permite al usuario eliminar una novela de la base de datos.

### Pantalla de mapa de novelas
- Muestra un mapa con las ubicaciones de las novelas.
- Permite al usuario ver su ubicación actual en el mapa.

### Navegación
- Se utiliza `NavController` para gestionar la navegación entre pantallas (agregar novela, ver lista, detalles, etc.).

## Contribuciones
Si deseas contribuir a este proyecto, por favor realiza un fork del repositorio y envía tus cambios mediante un pull request.

## Licencia
Este proyecto está licenciado bajo la MIT License.
