# Nombre de la App  
MinderTecnology Prueba

## Propósito y problema que resuelve  
La aplicación **PC Minder** está orientada a la **gestión de tiempos de mantenimiento en equipos de cómputo y periféricos**.  
Su objetivo principal es facilitar a técnicos y encargados de soporte la **programación de mantenimientos preventivos y correctivos**, evitando fallas inesperadas y mejorando la vida útil de los dispositivos.  

## Pantallas iniciales (Activities)  
- **Pantalla de inicio de sesión** 
- **Menú principal**
- **Gestión de equipos** 
- **Calendario de mantenimientos**
- **Detalle de tarea**

## Navegación entre pantallas (Intents y extras)  
- **Login → Menú principal** (con datos del usuario autenticado).  
- **Menú principal → Gestión de equipos**.  
- **Gestión de equipos → Detalle de tarea** (`ID_equipo`, `ID_tarea`).  
- **Menú principal → Calendario** (lista de mantenimientos).  

## Componentes de Android que se prevén usar  
- **Activities**  
- **RecyclerView/ListView**  
- **SQLite o Room**  
- **Notificaciones (NotificationManager)**  
- **DatePicker/CalendarView**  

## Activities, Intents  
- **Activities**: LoginActivity, MainMenuActivity, EquiposActivity, CalendarioActivity, DetalleTareaActivity.  
- **Intents explícitos**: Navegación interna entre pantallas.  
- **Intents implícitos**: Enviar correos de aviso o abrir recursos externos si aplica.  

## ¿Service? ¿BroadcastReceiver? ¿Content Provider?  
- **Service**: Recordatorios de mantenimientos en segundo plano.  
- **BroadcastReceiver**: Alarmas programadas para disparar notificaciones.  
- **Content Provider**: No se prevé su uso en esta etapa.  

## Datos  
- **Internos (SQLite/Room)**: Registro de equipos, fechas de mantenimiento, usuarios y tareas.  
- **Externos (futuro)**: Integración con servicios en la nube para sincronización.  

## Riesgos o desafíos iniciales  
1. Gestión de notificaciones en segundo plano (compatibilidad con distintas versiones de Android).  
2. Diseño eficiente de la base de datos para consultas rápidas.  
3. Adopción por parte de los usuarios (que realmente usen la app).  

## Hitos de avance (3 semanas)  
- **Semana 1**: Diseño de interfaces principales.  
- **Semana 2**: Implementación de la base de datos local.  
- **Semana 3**: Integración de notificaciones automáticas.  
