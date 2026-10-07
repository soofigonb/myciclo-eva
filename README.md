<div align="center">

# 🌷 MyCiclo · EVA

### Conocimiento es bienestar 💜

Aplicación Android para acompañar el seguimiento del ciclo menstrual,  
registrar observaciones y acceder a contenido educativo sobre EVA.

**Kotlin · Jetpack Compose · Material 3 · Room**

🌿 Conocer · Registrar · Aprender 🌿

**Grupo 5 · Duoc UC**

</div>

---

## 🌸 Sobre el proyecto


**MyCiclo · EVA** es una aplicación Android desarrollada como proyecto académico para el caso **Calendario Ciclo Ovulatorio**, de la asignatura **DSY1105 — Desarrollo de Aplicaciones Móviles**, en Duoc UC.

El proyecto busca reunir el seguimiento del ciclo menstrual, los registros diarios y la información educativa sobre el dispositivo EVA en una experiencia clara y cercana.

Su desarrollo considera los requisitos del caso y los acuerdos de la reunión con el cliente **MyCiclo SpA**.

> 🚧 **Proyecto en desarrollo:** los módulos se implementan y validan por etapas. Las funciones descritas representan el alcance del proyecto y no implican que todas estén terminadas.

---

## 🎯 Objetivo

Desarrollar una aplicación que permita organizar los registros del ciclo menstrual, consultar observaciones anteriores y aprender sobre el uso de EVA.

La aplicación busca apoyar el conocimiento del ciclo mediante información educativa y registros personales, respetando los límites de interpretación del dispositivo.

---

## 📱 Módulos de la aplicación

| Módulo | Propósito |
| :--- | :--- |
| 🏠 **Inicio** | Presentar el acceso a las funciones principales y la información inicial del seguimiento. |
| 🗓️ **Calendario** | Consultar fechas y registros asociados al ciclo menstrual. |
| 📝 **Registro diario** | Registrar observaciones, síntomas, estado de ánimo, flujos y notas. |
| 📂 **Historial** | Consultar los registros guardados y revisar observaciones anteriores. |
| 📖 **Aprender** | Acceder a guías sobre EVA, patrones, fases del ciclo y preguntas frecuentes. |
| ⚙️ **Ajustes** | Reunir las opciones de configuración de la aplicación. |

El registro diario puede integrarse dentro del flujo de otras pantallas; no necesariamente corresponde a una pestaña independiente.

---

## 🌿 Funcionalidades contempladas

### 🗓️ Seguimiento del ciclo

- Registro de fechas de inicio menstrual.
- Consulta de información mediante el calendario.
- Revisión de registros anteriores.

### 📝 Observaciones diarias

- Patrón observado con EVA.
- Síntomas y estado de ánimo.
- Flujo menstrual y vaginal.
- Notas personales.
- Fotografía asociada al registro, según los requisitos y acuerdos con el cliente.

### 📖 Educación

- Instrucciones para utilizar EVA.
- Explicación de los patrones observados en la saliva.
- Información sobre las fases del ciclo menstrual.
- Preguntas frecuentes sobre el dispositivo y su uso.

### 💾 Persistencia

- Almacenamiento local de registros mediante Room.
- Consulta de la información guardada en el dispositivo.

Estas funcionalidades se completan y prueban progresivamente durante el desarrollo.

---

## 📚 Contenido educativo

El módulo **Aprender** reúne cuatro secciones:

| Sección | Contenido |
| :--- | :--- |
| 📖 **Cómo utilizar EVA** | Preparación, secado, observación y registro de la muestra. |
| 🔬 **Guía de patrones** | Explicaciones sobre las formas observadas y los límites de su interpretación. |
| 🌸 **Fases del ciclo** | Menstruación, fase folicular, ovulación y fase lútea. |
| 💬 **Preguntas frecuentes** | Uso, cuidados y alcance del dispositivo. |

Los textos actuales se basan en el material educativo proporcionado por MyCiclo.

Las fotografías de referencia y sus descripciones se incorporarán con la validación correspondiente del cliente.

---

## 🎨 Identidad visual

La propuesta visual utiliza tonos rosados y morados, tarjetas con bordes redondeados y elementos gráficos que ayudan a identificar los contenidos.

| Color | Código | Uso actual en el módulo educativo |
| :--- | :--- | :--- |
| 💜 Morado | `#56328B` | Títulos y elementos destacados. |
| 🌷 Rosado suave | `#FFF0F5` | Fondo de las pantallas. |
| 🤍 Blanco rosado | `#FFFCFE` | Tarjetas de contenido. |
| 🪻 Lavanda | `#F0E1FA` | Fondo de los emojis. |

La uniformidad visual entre los módulos se revisa durante la integración del proyecto.

---

## 🛠️ Tecnologías

| Tecnología | Uso |
| :--- | :--- |
| **Kotlin** | Lenguaje de programación. |
| **Jetpack Compose** | Construcción de la interfaz Android. |
| **Material 3** | Componentes visuales. |
| **Navigation Compose** | Navegación entre pantallas. |
| **Room** | Persistencia local. |
| **ViewModel** | Gestión del estado y la lógica de presentación en los módulos que lo utilizan. |
| **Coroutines y Flow** | Operaciones asíncronas y observación de datos. |
| **Gradle** | Configuración y compilación. |
| **Git y GitHub** | Control de versiones y colaboración. |

---

## 🧩 Organización del código

El proyecto organiza sus responsabilidades en áreas:

| Área | Responsabilidad |
| :--- | :--- |
| **Datos** | Base de datos, entidades, DAO y repositorios. |
| **Pantallas** | Interfaz de los módulos de la aplicación. |
| **ViewModels** | Estado y lógica de presentación. |
| **Navegación** | Rutas y conexión entre pantallas. |
| **Componentes** | Elementos visuales reutilizables. |
| **Tema** | Colores, tipografía y configuración visual. |

La arquitectura se desarrolla hacia el enfoque **MVVM**, conforme a los requisitos de la asignatura.

---

## 🚀 Ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/soofigonb/myciclo-eva.git
```

### 2. Abrir en Android Studio

Abre la carpeta `myciclo-eva` y espera a que termine la sincronización de Gradle.

### 3. Preparar el entorno

- Instala los componentes del SDK solicitados por Android Studio.
- Utiliza JDK 17 para ejecutar Gradle.
- Configura un emulador o conecta un teléfono con **Android 10 — API 29 o superior**, según el mínimo actual del proyecto.
- Si utilizas un teléfono, activa la depuración USB.

### 4. Ejecutar la aplicación

Selecciona el dispositivo y presiona **Run ▶** en Android Studio.

### Compilar desde la terminal

**Git Bash:**

```bash
./gradlew :app:assembleDebug --no-configuration-cache
```

**Windows PowerShell:**

```powershell
.\gradlew.bat :app:assembleDebug --no-configuration-cache
```

El APK de depuración se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🤝 Equipo de desarrollo

### Grupo 5

| Integrante |
| :--- |
| 🌷 Sofía González |
| 🌿 Jonathan Cornejo |
| 🌸 Nitsi Sepúlveda |

El proyecto reúne los aportes de los tres integrantes en los módulos de seguimiento, registros, educación e integración.

Cada integrante trabaja en su rama y propone sus cambios mediante un **Pull Request** hacia `main`.

Antes de integrar, se revisan los cambios, la compilación y el funcionamiento de los módulos involucrados.

---

## 🌱 Estado del proyecto

El equipo continúa trabajando en:

- Completar los flujos del calendario, los registros y el historial.
- Revisar las validaciones y la persistencia de los datos.
- Incorporar y validar las imágenes educativas.
- Completar los recursos nativos exigidos por la asignatura.
- Revisar la arquitectura y la integración entre módulos.
- Unificar los detalles visuales.
- Realizar pruebas de navegación y funcionamiento.
- Actualizar los requisitos según los acuerdos con el cliente.

El seguimiento de tareas se realiza en el tablero del equipo y mediante la actividad del repositorio.

---

## 💜 Alcance educativo

EVA permite observar patrones en muestras de saliva seca. No mide hormonas directamente ni confirma por sí sola que la ovulación haya ocurrido.

El dispositivo y el contenido de la aplicación **no deben utilizarse como método anticonceptivo ni como reemplazo de una evaluación profesional de salud**.

Este repositorio corresponde a un proyecto académico en desarrollo.

---

## 🔗 Referencias

- [Sitio oficial de MyCiclo](https://myciclo.cl/)
- [Instagram de MyCiclo](https://www.instagram.com/myciclo_official/)
- **EVA y el Ciclo de Fertilidad Femenina — Guía de apoyo para estudiantes**, proporcionada por MyCiclo.
- **Guía inicial de Jetpack Compose y navegación**, material de la asignatura.
- Caso, requisitos y pautas de evaluación de **DSY1105**.

---

<div align="center">

🌷 **MyCiclo · EVA** 🌷

*Conocimiento es bienestar.*

**Sofía · Jonathan · Nitsi**

**Grupo 5 · DSY1105 · Duoc UC**

</div>
