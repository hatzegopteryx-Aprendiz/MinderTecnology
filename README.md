# PC Minder - Gestión de Mantenimiento de Computadoras

**PC Minder** es una aplicación móvil desarrollada en **Android Studio** para la gestión de tiempos y tareas de mantenimiento de computadoras y dispositivos tecnológicos.  
Está pensada para técnicos y encargados de soporte que necesitan **organizar, programar y controlar** mantenimientos preventivos y correctivos, evitando fallas inesperadas y extendiendo la vida útil del equipo.

---

## Características principales

- Gestión de equipos y dispositivos.
- Programación de mantenimientos preventivos y correctivos.
- Notificaciones automáticas para recordar tareas pendientes.
- Registro de historial de mantenimiento.
- Integración con Firebase (autenticación, almacenamiento y notificaciones).

---

## Pantallas iniciales (Activities)

- **LoginActivity** → Inicio de sesión de usuario.  
- **MainMenuActivity** → Menú principal con acceso a funciones clave.  
- **EquiposActivity** → Listado y gestión de equipos registrados.  
- **CalendarioActivity** → Vista de calendario para programar o revisar mantenimientos.  
- **DetalleTareaActivity** → Información detallada de cada tarea.

---

## Navegación entre pantallas (Intents y Extras)

| Origen | Destino | Datos Transferidos |
|---------|----------|--------------------|
| LoginActivity | MainMenuActivity | Datos del usuario autenticado |
| MainMenuActivity | EquiposActivity | - |
| EquiposActivity | DetalleTareaActivity | `ID_equipo`, `ID_tarea` |
| MainMenuActivity | CalendarioActivity | Lista de mantenimientos |

---

##2 Componentes de Android utilizados

- **Activities** (pantallas principales)
- **RecyclerView/ListView** (listado de equipos o tareas)
- **DatePicker / CalendarView** (selección de fechas)
- **Firebase** (autenticación, notificaciones y almacenamiento en la nube)

---

## Servicios y comunicación interna

| Componente | Función |
|-------------|----------|
| **Service** | Ejecutar recordatorios en segundo plano. |
| **BroadcastReceiver** | Activar notificaciones programadas (alarmas). |
| **ContentProvider** | No se utiliza en esta etapa. |

---

## Datos y almacenamiento

- **Firebase Authentication** → Gestión segura de usuarios.  
- **Firebase Cloud Messaging** → Notificaciones push de mantenimiento.  
- **Firebase Storage** → Almacenamiento de documentos técnicos o reportes.  
- **(Futuro)** Integración con servicios externos de sincronización en la nube.

---

## Riesgos o desafíos técnicos

1. Manejo confiable de notificaciones en segundo plano (compatibilidad con versiones antiguas de Android).  
2. Diseño eficiente de la base de datos local.  
3. Aceptación y adopción real por parte de los usuarios técnicos.  

---

## Hitos de desarrollo

| Semana | Objetivo |
|:-------:|:---------|
| **5** | Diseño de interfaces principales |
| **10** | Implementación de la base de datos local |
| **14** | Integración de notificaciones automáticas |

---

## ⚙️ Instalación y configuración

### Prerrequisitos

- **Android Studio** (versión *Android Narwhal 3* o superior)  
- **JDK 11** o superior  
- **SDK de Android API 26 (Android 8.0)** o superior  
- **Dispositivo Android o emulador** con Google Play Services  

### Instalación

```bash
# Clonar el repositorio
git clone https://github.com/hatzegopteryx-Aprendiz/MinderTecnology.git

# Acceder al proyecto
cd MinderTecnology

# Abrir en Android Studio
-----
```
## 🔥confguracion firebase



