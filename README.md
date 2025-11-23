# 📱 Aplicación de Gestión de Eventos Comunitarios

La **Aplicación de Gestión de Eventos Comunitarios** es una app Android en **Kotlin** que permite a una comunidad organizar, publicar y gestionar eventos. Los usuarios pueden registrarse, iniciar sesión, ver eventos, confirmar asistencia y revisar su historial de participación.

---

## 👥 Integrantes

- **José Valentín Corcios Segovia** – CS232913  
- **Fernando Samuel Quijada Arévalo** – QA190088  

---

## 🧩 Tecnologías

- **Lenguaje:** Kotlin  
- **Plataforma:** Android (minSdk 24, targetSdk 36)  
- **UI:** Jetpack Compose  
- **Backend (sugerido):** Firebase Authentication, Firestore, (opcional) Cloud Messaging  

---

## 🔑 Autenticación

- Registro e inicio de sesión con correo y contraseña.  
- Inicio de sesión con Google (Google Sign-In).  
- Manejo básico de sesión para mantener al usuario autenticado.

---

## 📅 Gestión de Eventos

- Crear, editar y listar eventos.  
- Datos del evento: título, descripción, fecha, hora y ubicación.  
- Eventos próximos y pasados.

---

## ✅ Participación (RSVP)

- Confirmar o cancelar asistencia a eventos.  
- Registro de asistencia por usuario.  
- Soporte para notificaciones de recordatorio (diseño pensado para FCM).

---

## 💬 Interacción Social

- Comentarios y calificaciones por evento.  
- Visualización de comentarios y promedio de calificación.  
- Opción de compartir eventos mediante otras apps (intents de Android).

---

## 📊 Historial

- Pantalla con eventos asistidos por el usuario.  
- Estadísticas básicas (por ejemplo, total de eventos asistidos).

---

## 🎨 UI / UX

- Diseño simple e intuitivo, con navegación clara entre pantallas.  
- Mockups diseñados en **Figma**:  
  https://dove-spline-46417051.figma.site/

---

## 📌 Gestión del proyecto

- Metodología de trabajo: **Scrum**.  
- Tablero en **Trello** para seguimiento de tareas y evidencias:  
  https://trello.com/invite/b/691fd9dbaf13acf92b9e0bf9/ATTI63c87b9a96fb93849ddf0d4364d1050e0DF1F6B0/dsm-proyectofinal  

- Control de versiones con **Git/GitHub**:
  - Una rama por integrante.
  - Uso de commits y Pull Requests para integrar cambios.

---

## 🛠 Requisitos y ejecución

- **Android Studio** actualizado.  
- `compileSdk = 36`, `minSdk = 24`.  
- Configurar Firebase y agregar `google-services.json` en `app/` (si aplica).

Para ejecutar:

```bash
git clone https://github.com/usuario/RepositorioEventosComunitarios.git
