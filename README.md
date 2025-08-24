# MobileApp2025

Este es un proyecto desarrollado como parte de la clase COM-437 de Saint Leo University. Se trata de un reproductor de música nativo para Android creado con Jecpack Compose y programado en Kotlin. Su objetivo principak es brindar una experiencia de usuario simple y funcional para permitir la exploración de buenas prácticas pen el desarollo móvil. 

---

## Objetivos 

El objetivo de esta aplicación es el de brindar una Interfaz de Usuario implementada mediante la lógica de reproducción multimedia brindada por ExoPlayer, comprendiendo permisos de almacenamiento, funcionamiento en segundo plano, y widgets de notificación así como el manejo de archivos mediante MediaStore.

---

## Funcionalidades

- Listado de canciones
- Barra de reproducción
- Permisos de almacenamiento
- Widget de reproducción (pantalla de bloqueo y ajustes rápidos)

---

## Tecnologías Utilizadas

- **Kotlin** – Lenguaje de programación principal
- **MediaStore** – API para el acceso de archivos en Android.
- **Android Studio** – Entorno de desarrollo (IDE)
- **Jetpack Compose** – Kit para diseño moderno de UI en Android
- **Git/GitHub** – Control de versiones e integración continua
- **ExoPlayer** – Reproductor multimedia para audio y video
- **MediaSessionService** - Implementación de foreground service

---

## Flujo de usuario

- El usuario abre la App.
- La app solicita los permisos para acceder a los archvivos multimedia. Sin el permiso la app será capáz de ejecutarse, pero no podrá leer los archivos de música en el dispositivo del usuario.
- Se muestra un listado de canciones disponibles.
- El usuario selecciona y activa la reproducción de una canción, desde la cuál podrá pausa y saltar hacia la reproducción siguente/previa, al igual que podría al seleccioanr una nueva canción desde la lista.
- El usuario también puede acceder a las funcioens de la barra de reproducción mediante el widget de notificación.

---

## Cómo usar la App?

- Al abrir la App, el usuario deberá de aceptar los permisos de lectura de archivos y los de reproducción dependiendo de su versión del SO. En caso de no aceptar, la aplicación podría no mostrar las canciones o no ejecutarse.
- El usuario puede seleccionar la canción desde la lista de canciones, lo que la reproducirá y la pondrá en la barra de reproducción. La canción actualmente seleccionada se mantendrá resaltada en el UI.
- Desde la barra de reproducción, el usuario puede ir hacia atrás, pausar/reproducir, o seleccionar la siguente canción. El usuario también puede cambiar el tiempo de reproducción de la canción mediante el slider.
- Al minimizar la aplicación, el usuario podrá seguir escuchando la música, la cuál se reproducirá automáticamente de acuerdo a la playlist de canciones predeterminada.
- Por último, el usuario puede acceder a la notificación de reproducción desde la barra de acceso rápido y el bloqueo de pantalla, con el cuál puede realizar las mismas funciones de la barra de control. 


---

## Cambios recientes:

- 24 de Agosto del 2025: Se reorganizan las funciones y los elementos de UI en sus archivos correspondientes para disminuir la longitud de los archivos.
- 22-23 de Agosto del 2025: Se añade la funcionalidad del Slider. Se resuelven los bugs de sincronización desde la implementación del PlayBackService.
- 16 de Agosto del 2025: Se re-implementa la lógica de reproducción en un PlayBackService con MediaSessionService. Se realizan cambios al PlayerViewModel para que maneje lógica de sincronización en vez de reproducción. 
- 3 de Agosto del 2025: Se añadió la funcionalidad de reproducción/pausa así como el listado de canciones funcional, así como la incorporación de los permisos.
- Pendiente: mejora visual de la UI, posible barra de búsqueda de canción, posible creador de cola de reproducción, view individual de canción, view de playlist, view de configuración, almacenamiento de playlist personalizada.

---

