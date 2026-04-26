package database;

public interface SchemDB {

    // ================= TABLAS =================
    String TAB_USUARIO = "usuario";
    String TAB_SOCIO = "socio";
    String TAB_ENTRENADOR = "entrenador";
    String TAB_SECRETARIO = "secretario";
    String TAB_CLASE = "clase";
    String TAB_SESION = "sesion";
    String TAB_PLAN = "plan";
    String TAB_SUSCRIPCION = "suscripcion";
    String TAB_RESERVA = "reserva";

    // ================= COLUMNAS USUARIO =================
    String COL_USUARIO_ID = "id_usuario";
    String COL_USUARIO_ESTADO = "estado";
    String COL_USUARIO_NOMBRE = "nombre";
    String COL_USUARIO_APELLIDO = "apellido";
    String COL_USUARIO_EMAIL = "email";
    String COL_USUARIO_PASSWORD = "password";
    String COL_USUARIO_TELEFONO = "telefono";

    // ================= COLUMNAS SOCIO =================
    String COL_SOCIO_FECHA_ALTA = "fecha_alta";

    // ================= COLUMNAS ENTRENADOR =================
    String COL_ENTRENADOR_ESPECIALIDAD = "especialidad";

    // ================= COLUMNAS SECRETARIO =================
    String COL_SECRETARIO_TURNO = "turno";

    // ================= COLUMNAS CLASE =================
    String COL_CLASE_ID = "id_clase";
    String COL_CLASE_NOMBRE = "nombre";
    String COL_CLASE_ESTADO = "estado";
    String COL_CLASE_AFORO = "aforo_max";

    // ================= COLUMNAS SESION =================
    String COL_SESION_ID = "id_sesion";
    String COL_SESION_ENTRENADOR = "id_entrenador";
    String COL_SESION_CLASE = "id_clase";
    String COL_SESION_ESTADO = "estado";
    String COL_SESION_SALA = "sala";
    String COL_SESION_INICIO = "inicio";
    String COL_SESION_FIN = "fin";

    // ================= COLUMNAS PLAN =================
    String COL_PLAN_ID = "id_plan";
    String COL_PLAN_ESTADO = "estado";
    String COL_PLAN_NOMBRE = "nombre";
    String COL_PLAN_PRECIO = "precio_mensual";

    // ================= COLUMNAS SUSCRIPCION =================
    String COL_SUSC_ID = "id_suscripcion";
    String COL_SUSC_PLAN = "id_plan";
    String COL_SUSC_SOCIO = "id_socio";
    String COL_SUSC_ESTADO = "estado";
    String COL_SUSC_INICIO = "fecha_inicio";
    String COL_SUSC_FIN = "fecha_fin";

    // ================= COLUMNAS RESERVA =================
    String COL_RES_SOCIO = "id_socio";
    String COL_RES_SESION = "id_sesion";
    String COL_RES_ESTADO = "estado";
    String COL_RES_FECHA = "fecha_reserva";
}