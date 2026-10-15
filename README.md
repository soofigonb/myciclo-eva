<div align="center">

# 🌷 MyCiclo · EVA

### Conocimiento es bienestar 💜

Aplicación Android para acompañar el seguimiento del ciclo menstrual,  
registrar observaciones y aprender sobre el uso de EVA.

**Kotlin · Jetpack Compose · Material 3 · Room · DataStore**

🌿 Conocer · Registrar · Aprender 🌿

**Grupo 5 · DSY1105 · Duoc UC**

</div>

---

## 🌸 Sobre el proyecto

**MyCiclo · EVA** es una aplicación Android desarrollada como proyecto académico para el caso **Calendario Ciclo Ovulatorio**, de la asignatura **DSY1105 — Desarrollo de Aplicaciones Móviles**, en Duoc UC.

Reúne el seguimiento del ciclo menstrual, una bitácora de observaciones, fotografías y contenido educativo sobre el dispositivo EVA.

El proyecto integra los aportes del equipo y considera el material proporcionado por **MyCiclo SpA**.

---

## 🤝 Equipo de desarrollo

| Integrante |
| :--- |
| 🌷 Sofía González |
| 🌿 Jonathan Rivera |
| 🌸 Nitsi Sepúlveda |

El trabajo se desarrolla mediante ramas y Pull Requests hacia `main`, con revisión e integración de los módulos.

---

## 📱 Funcionalidades implementadas

### 🌷 Bienvenida y cuentas locales

- Pantalla de bienvenida con acceso a Login y Registro.
- Creación de cuentas locales con nombre, correo y contraseña.
- Validación de campos obligatorios, formato de correo y confirmación de contraseña.
- Detección de correos ya registrados en el dispositivo.
- Opciones para mostrar y ocultar la contraseña.
- Contraseñas almacenadas como un hash PBKDF2 con sal aleatoria.
- Sesión persistente mediante DataStore.
- Cierre de sesión y acceso como invitada.
- Registros del ciclo y bitácoras separados por cuenta local.

> Las cuentas y sus registros se almacenan localmente en el dispositivo donde se crean.

### 🏠 Inicio

- Resumen del seguimiento.
- Día del ciclo calculado a partir del último inicio menstrual.
- Registro de fechas de inicio menstrual.
- Selector de fecha con estilo morado.
- Validaciones para impedir fechas futuras e inicios duplicados.
- Mensajes de carga, error y confirmación.

### 🗓️ Calendario

- Vista mensual con navegación entre meses.
- Identificación de fechas con inicios menstruales y registros diarios.
- Consulta de registros asociados al mes.
- Acceso a la bitácora de una fecha seleccionada.
- Restricción del registro de observaciones en fechas futuras.

### 📖 Bitácora diaria

- Creación y actualización de un registro por fecha.
- Selección explícita del patrón observado: Gotas o Helechos.
- Campos opcionales de estado de ánimo, síntomas, flujos y notas.
- Captura de muestras mediante cámara frontal.
- Indicaciones antes de fotografiar la muestra con EVA.
- Selección de fotografías desde la galería.
- Vista previa y opciones para quitar o reemplazar fotografías.
- Eliminación del registro con confirmación.
- Almacenamiento local de datos y fotografías.

### 📂 Historial

- Consulta de inicios menstruales y bitácoras.
- Cálculo de días entre fechas consecutivas de inicio menstrual.
- Visualización de observaciones y fotografías guardadas.
- Acceso a la edición de bitácoras.
- Eliminación de bitácoras e inicios menstruales con confirmación.
- Limpieza de fotografías privadas sin uso al actualizar o eliminar bitácoras.

### 👤 Perfil

- Consulta de nombre, correo, fecha de nacimiento y fotografía.
- Edición del nombre y fecha de nacimiento.
- Validaciones visuales junto a los campos.
- Fotografía de perfil desde cámara o galería.
- Opción de quitar o reemplazar la fotografía.
- Confirmación de actualización y finalización de la edición.
- Acceso al historial y cierre de sesión.

### 📚 Aprender

El módulo reúne cuatro secciones educativas:

| Sección | Contenido |
| :--- | :--- |
| 📖 Cómo utilizar EVA | Preparación, secado, observación y registro de la muestra. |
| 🔬 Guía de patrones | Descripción de formas observadas y límites de interpretación. |
| 🌸 Fases del ciclo | Información sobre las fases del ciclo menstrual. |
| 💬 Preguntas frecuentes | Uso, cuidados y alcance del dispositivo. |

Los textos se basan en el material educativo proporcionado por MyCiclo.

### ⚙️ Ajustes y recordatorios

- Programación de un recordatorio de secado para aproximadamente una hora.
- Prueba de notificación en diez segundos.
- Cancelación del recordatorio y de la prueba.
- Solicitud del permiso de notificaciones cuando corresponde.
- Acceso a los ajustes de notificaciones del teléfono.
- Mensajes visuales de confirmación.

> Android puede ajustar el momento del aviso por ahorro de batería. Después de reiniciar el teléfono se debe programar nuevamente. Para la prueba de diez segundos, se mantiene abierta la pantalla de Ajustes.

---

## 🧭 Navegación

La barra inferior contiene:

**Inicio · Calendario · Perfil · Aprender · Más**

El menú **Más** permite abrir **Ajustes** e **Historial**.

La bienvenida y los formularios de acceso se muestran sin barra inferior. El historial también es accesible desde Perfil.

---

## 🎨 Identidad visual y retroalimentación

La interfaz utiliza tonos rosados y morados, tarjetas redondeadas e íconos para identificar acciones y contenidos.

| Color | Código | Uso |
| :--- | :--- | :--- |
| 💜 Morado | `#56328B` | Títulos, botones y elementos destacados. |
| 🌷 Rosado suave | `#FFF0F5` | Fondo de pantallas. |
| 🤍 Blanco rosado | `#FFFCFE` | Tarjetas. |
| 🪻 Lavanda | `#EADDFF` | Avatar e indicadores de selección. |

La aplicación incorpora:

- Mensajes e íconos de error.
- Aparición animada de errores y avisos con `AnimatedVisibility`.
- Ajustes animados de tamaño con `animateContentSize`.
- Indicadores de carga.
- Bloqueo de acciones durante operaciones de guardado y eliminación.
- Diálogos de confirmación para eliminar registros y cerrar sesión.
- Confirmaciones visuales después de guardar.

---

## 🛠️ Tecnologías

| Tecnología | Uso |
| :--- | :--- |
| Kotlin | Lenguaje de programación. |
| Jetpack Compose y Material 3 | Interfaz y componentes visuales. |
| Navigation Compose | Navegación entre pantallas. |
| ViewModel | Estado y lógica de presentación. |
| Coroutines, Flow y StateFlow | Operaciones asíncronas y observación de datos. |
| Room | Persistencia local de cuentas y registros. |
| DataStore Preferences | Identificación de la sesión activa. |
| CameraX | Cámara frontal para fotografías de muestras. |
| Activity Result API | Selección de imágenes, cámara de perfil y permisos. |
| AlarmManager y notificaciones Android | Recordatorio de secado. |
| Git y GitHub | Control de versiones e integración del equipo. |

---

## 🧩 Organización del proyecto

El código se organiza con un enfoque **MVVM**, separando interfaz, estado, lógica y acceso a datos.

| Ubicación | Responsabilidad |
| :--- | :--- |
| `data/local` | Base de datos y sesión local. |
| `data/local/entities` | Entidades persistidas en Room. |
| `data/local/dao` | Consultas y operaciones de datos. |
| `data/repository` | Acceso a cuentas, ciclos y registros. |
| `ui/screens` | Pantallas y ViewModels por módulo. |
| `ui/navigation` | Rutas y navegación. |
| `ui/components` | Componentes compartidos. |
| `ui/theme` | Tema, colores y tipografía. |
| `utils` | Contraseñas y notificaciones. |

---

## 💾 Almacenamiento local

- Las cuentas se guardan en la base principal.
- Cada cuenta utiliza una base independiente para sus ciclos y bitácoras.
- El modo invitada conserva sus registros por separado de las cuentas.
- DataStore conserva el identificador de la sesión activa.
- Las fotografías seleccionadas o capturadas se guardan en archivos privados de la aplicación.
- Las migraciones de Room permiten actualizar el esquema sin recurrir al borrado destructivo de datos.

---

## 📷 Recursos nativos y permisos

| Recurso | Uso |
| :--- | :--- |
| Cámara | Fotografías de muestras y de perfil. |
| Selector de imágenes | Fotografías desde la galería. |
| Notificaciones | Avisos para revisar el secado de la muestra. |

Los permisos se solicitan al utilizar la función correspondiente. Si se rechaza el acceso a la cámara, sigue disponible la selección desde la galería.

---

## 🚀 Ejecutar el proyecto

### Requisitos

- Android Studio y los componentes del SDK solicitados por Gradle.
- SDK de compilación Android 35.
- JDK 17.
- Emulador o teléfono con **Android 10 — API 29 o superior**.
- Cámara disponible para probar capturas.
- Notificaciones habilitadas para probar los recordatorios.

### 1. Clonar

```bash
git clone https://github.com/soofigonb/myciclo-eva.git
cd myciclo-eva
```

### 2. Abrir y sincronizar

Abre la carpeta del proyecto en Android Studio y espera a que finalice la sincronización de Gradle.

### 3. Ejecutar

Selecciona un emulador o teléfono y presiona **Run ▶**.

Si utilizas un teléfono, habilita la depuración USB.

### Compilar desde Git Bash

```bash
./gradlew :app:assembleDebug --no-configuration-cache
```

### Compilar desde PowerShell

```powershell
.\gradlew.bat :app:assembleDebug --no-configuration-cache
```

El APK de depuración se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Comprobaciones funcionales

Para revisar los principales flujos de la aplicación:

1. Crear una cuenta e iniciar sesión.
2. Comprobar errores con campos vacíos y contraseñas distintas.
3. Editar el perfil y guardar una fotografía.
4. Registrar un inicio menstrual y comprobar la restricción de duplicados.
5. Crear una bitácora seleccionando un patrón.
6. Incorporar una fotografía desde cámara o galería.
7. Consultar, actualizar y eliminar registros desde Historial.
8. Cerrar y abrir la aplicación para comprobar persistencia.
9. Acceder con otra cuenta y comprobar la separación de registros.
10. Probar notificaciones, permisos y cancelación.
11. Navegar por las cuatro secciones educativas y regresar.

---

## 📋 Planificación y colaboración

El equipo organiza sus tareas mediante **Trello** y mantiene el historial de desarrollo en **GitHub**.

- Trabajo mediante ramas.
- Integración mediante Pull Requests.
- Commits con descripciones de los cambios.
- Distribución y seguimiento de tareas en el tablero del equipo.

---

## 🌷 Logros del proyecto

- Interfaz coherente con la identidad visual de MyCiclo · EVA.
- Navegación entre seguimiento, calendario, perfil, historial y educación.
- Cuentas locales con sesión persistente y registros separados por usuaria.
- Formularios con validaciones, mensajes e íconos de error.
- Creación, consulta, actualización y eliminación de registros.
- Fotografías desde cámara y galería.
- Recordatorios con notificaciones y gestión de permisos.
- Persistencia local mediante Room y DataStore.
- Organización del código con pantallas, ViewModels, repositorios y DAO.
- Desarrollo colaborativo mediante ramas, commits y Pull Requests.

---

## 💜 Alcance educativo

EVA permite observar patrones en muestras de saliva seca. No mide hormonas directamente ni confirma por sí sola que la ovulación haya ocurrido.

El contenido y los registros de esta aplicación tienen un propósito educativo y de seguimiento personal.

**No deben utilizarse como método anticonceptivo ni como reemplazo de una evaluación profesional de salud.**

---

## 🔗 Referencias

- [Repositorio del proyecto](https://github.com/soofigonb/myciclo-eva)
- [Sitio oficial de MyCiclo](https://myciclo.cl/)
- [Instagram de MyCiclo](https://www.instagram.com/myciclo_official/)
- **EVA y el Ciclo de Fertilidad Femenina — Guía de apoyo para estudiantes**, proporcionada por MyCiclo.
- Caso, guías y pautas de evaluación de **DSY1105**.

---

<div align="center">

🌷 **MyCiclo · EVA** 🌷

*Conocimiento es bienestar.*

**Sofía · Jonathan · Nitsi**

**Grupo 5 · DSY1105 · Duoc UC**

</div>
