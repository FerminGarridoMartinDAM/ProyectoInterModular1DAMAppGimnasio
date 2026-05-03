# Documentacion de Arquitectura y Flujos - Gym APP

Bienvenido a la documentacion visual del sistema Gym APP. En este documento se encuentran los diagramas UML que definen la arquitectura, las bases de datos y la logica de negocio de la aplicacion.

---

## 1. Arquitectura y Estructura del Sistema

### Casos de Uso
Define que acciones puede realizar cada tipo de usuario en el sistema.
![Diagrama de Casos de Uso](DiagramasCasoUso/Casos_De_Uso.png)

### Diagramas de Clases
Muestra la estructura de los datos, la conexion con la base de datos y los controladores.
* **Modelos y Dominio:**
  ![Diagrama de Clases](DiagramaClases/Diagrama_Clases_UML_MODELOS.png)
* **Capa de Acceso a Datos (DAO):**
  ![Diagrama DAO](DiagramaClases/Diagrama_Clases_UML_DAO.png)
* **Controladores y Utilidades:**
  ![Diagrama de Controladores](DiagramaClases/Diagrama_Clases_UML_CONTROLLERandUTILS.png)

---

## 2. Ciclo de Vida (Diagramas de Estados)

Muestra cómo cambian de estado las entidades principales a lo largo del tiempo.
* **Estado de una Reserva:**
  ![Estado Reserva](DiagramaEstados/Estado_Reserva.png)
* **Estado de una Suscripcion:**
  ![Estado Suscripcion](DiagramaEstados/Estado_Suscripcion.png)

---

## 3. Flujos de Trabajo (Diagramas de Actividad)


### Autenticacion y Gestion de Usuarios
* **Inicio de Sesion (Login):**
  ![Login](DiagramaActividades/Actividad_Login.png)
* **Alta de Usuario:**
  ![Alta Usuario](DiagramaActividades/Actividad_Alta_Usuario.png)

### Planes y Suscripciones
* **Administrar Planes:**
  ![Administrar Planes](DiagramaActividades/Actividad_Administrar_Planes.png)
* **Vender Suscripcion:**
  ![Venta Suscripcion](DiagramaActividades/Actividad_Gestion_Suscripcion.png)

### Gestion de Sesiones y Reservas
* **Programar Sesion:**
  ![Programar Sesion](DiagramaActividades/Actividad_Programar_Sesion.png)
* **Anadir Clase al Catalogo:**
  ![Crear Clase](DiagramaActividades/Actividad_Crear_clase.png)
* **Reserva de Sesion:**
  ![Reserva de Sesion](DiagramaActividades/Actividad_Reserva.png)
* **Consultar Asistencia:**
  ![Consultar Asistencia](DiagramaActividades/Actividad_Consultar_asistencia.png)

### Cancelaciones y Seguridad
* **Cancelar Sesion:**
  ![Cancelar Sesion](DiagramaActividades/Actividad_Cancelar_Sesion.png)
* **Cancelar Reserva:**
  ![Cancelar Reserva](DiagramaActividades/Actividad_Cancelar_Reserva.png)

### Mantenimiento del Sistema
* **Exportar Copia de Seguridad:**
  ![Exportar Backup](DiagramaActividades/Actividad_Exportar_CopiaSeguridad.png)
* **Restaurar Copia de Seguridad:**
  ![Restaurar Backup](DiagramaActividades/Actividad_Importar_CopiaSeguridad.png)

---

## 4. Interaccion entre Objetos (Diagramas de Secuencia)

Estos diagramas muestran el orden cronológico de los mensajes que se envían las clases (Vista -> Controlador -> DAO -> BD) para ejecutar una acción.

### Usuarios y Autenticacion
* **Iniciar Sesion (Login):**
  ![Secuencia Login](DiagramasSecuencias/Secuencia_Iniciar_Sesión(Login).png)
* **Alta de Usuario:**
  ![Secuencia Alta Usuario](DiagramasSecuencias/Secuencia_Alta_Usuario.png)

### Operaciones Core
* **Crear un Plan:**
  ![Secuencia Crear Plan](DiagramasSecuencias/Secuencia_Crear_Plan.png)
* **Gestionar Suscripcion:**
  ![Secuencia Gestionar Suscripcion](DiagramasSecuencias/Secuencia_Gestionar_Suscripcion.png)
* **Programar Sesion:**
  ![Secuencia Programar Sesion](DiagramasSecuencias/Secuencia_Programar_Sesion.png)
* **Reservar Sesion:**
  ![Secuencia Reservar Sesion](DiagramasSecuencias/Secuencia_Reservar_Sesion.png)
* **Consultar Asistencia:**
  ![Secuencia Consultar Asistencia](DiagramasSecuencias/Secuencia_Consultar_Asistencia.png)

### Cancelaciones y Mantenimiento
* **Cancelar Sesion:**
  ![Secuencia Cancelar Sesion](DiagramasSecuencias/Secuencia_Cancelar_Sesion.png)
* **Cancelar Reserva:**
  ![Secuencia Cancelar Reserva](DiagramasSecuencias/Secuencia_Cancelar_Reserva.png)
* **Generar Copia de Seguridad (JSON):**
  ![Secuencia Copia Seguridad](DiagramasSecuencias/Secuencia_Generar_Copia_Seguridad_JSON.png)