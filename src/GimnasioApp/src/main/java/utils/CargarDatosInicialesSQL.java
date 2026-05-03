package utils;

import database.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;



/* ========================================================================================
 * CARGADOR DE DATOS PRO (SQL DINÁMICO)
 *
 */
public class CargarDatosInicialesSQL {

    public static void cargarTodo() {
        System.out.println(" Iniciando inyección de datos");

        // Usamos un Bloque de Texto para que el SQL sea legible y fácil de mantener
        String sql = """
            -- 1. BLOQUE DE SECRETARIOS
            WITH s1 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Laura', 'Gómez', 'laura@gimnasio.com', 'pass123', '600111111') RETURNING id_usuario)
                INSERT INTO secretario (id_usuario, turno) SELECT id_usuario, 'Mañana' FROM s1;
            WITH s2 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Carlos', 'Ruiz', 'carlos@gimnasio.com', 'pass123', '600222222') RETURNING id_usuario)
                INSERT INTO secretario (id_usuario, turno) SELECT id_usuario, 'Tarde' FROM s2;
            WITH s3 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Ana', 'Martínez', 'ana@gimnasio.com', 'pass123', '600333333') RETURNING id_usuario)
                INSERT INTO secretario (id_usuario, turno) SELECT id_usuario, 'Mañana' FROM s3;
            WITH s4 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'David', 'Sánchez', 'david@gimnasio.com', 'pass123', '600444444') RETURNING id_usuario)
                INSERT INTO secretario (id_usuario, turno) SELECT id_usuario, 'Tarde' FROM s4;
            WITH s5 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Elena', 'Díaz', 'elena@gimnasio.com', 'pass123', '600555555') RETURNING id_usuario)
                INSERT INTO secretario (id_usuario, turno) SELECT id_usuario, 'Mañana' FROM s5;

            -- 2. BLOQUE DE ENTRENADORES
            WITH e1 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Miguel', 'Fuerza', 'miguel@gimnasio.com', 'pass123', '611111111') RETURNING id_usuario)
                INSERT INTO entrenador (id_usuario, especialidad) SELECT id_usuario, 'Musculación' FROM e1;
            WITH e2 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Sara', 'Agil', 'sara@gimnasio.com', 'pass123', '611222222') RETURNING id_usuario)
                INSERT INTO entrenador (id_usuario, especialidad) SELECT id_usuario, 'Crossfit' FROM e2;
            WITH e3 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Javier', 'Zen', 'javier@gimnasio.com', 'pass123', '611333333') RETURNING id_usuario)
                INSERT INTO entrenador (id_usuario, especialidad) SELECT id_usuario, 'Yoga' FROM e3;
            WITH e4 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Lucía', 'Core', 'lucia@gimnasio.com', 'pass123', '611444444') RETURNING id_usuario)
                INSERT INTO entrenador (id_usuario, especialidad) SELECT id_usuario, 'Pilates' FROM e4;
            WITH e5 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Pedro', 'Rueda', 'pedro@gimnasio.com', 'pass123', '611555555') RETURNING id_usuario)
                INSERT INTO entrenador (id_usuario, especialidad) SELECT id_usuario, 'Spinning' FROM e5;

            -- 3. BLOQUE DE SOCIOS
            WITH soc1 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Alberto', 'Cliente1', 'alberto@correo.com', 'pass123', '622111111') RETURNING id_usuario)
                INSERT INTO socio (id_usuario, fecha_alta) SELECT id_usuario, '2026-05-01' FROM soc1;
            WITH soc2 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Beatriz', 'Cliente2', 'beatriz@correo.com', 'pass123', '622222222') RETURNING id_usuario)
                INSERT INTO socio (id_usuario, fecha_alta) SELECT id_usuario, '2026-05-02' FROM soc2;
            WITH soc3 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Carmen', 'Cliente3', 'carmen@correo.com', 'pass123', '622333333') RETURNING id_usuario)
                INSERT INTO socio (id_usuario, fecha_alta) SELECT id_usuario, '2026-05-03' FROM soc3;
            WITH soc4 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Diego', 'Cliente4', 'diego@correo.com', 'pass123', '622444444') RETURNING id_usuario)
                INSERT INTO socio (id_usuario, fecha_alta) SELECT id_usuario, '2026-04-15' FROM soc4;
            WITH soc5 AS (INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) 
                VALUES ('ACTIVO', 'Eva', 'Cliente5', 'eva@correo.com', 'pass123', '622555555') RETURNING id_usuario)
                INSERT INTO socio (id_usuario, fecha_alta) SELECT id_usuario, '2026-04-20' FROM soc5;

            -- 4. PLANES Y CLASES
            INSERT INTO plan (estado, nombre, precio_mensual) VALUES 
                ('ACTIVO', 'Básico', 29.99), ('ACTIVO', 'Estándar', 39.99), ('ACTIVO', 'Premium', 49.99), ('ACTIVO', 'VIP', 59.99), ('ACTIVO', 'Estudiante', 19.99);
            INSERT INTO clase (estado, nombre, aforo_max) VALUES 
                ('ACTIVO', 'Yoga Relax', 20), ('ACTIVO', 'Crossfit Extremo', 15), ('ACTIVO', 'Pilates Core', 25), ('ACTIVO', 'Zumba Dance', 30), ('ACTIVO', 'Spinning Pro', 20);

            -- 5. SESIONES (VINCULACIÓN DINÁMICA POR EMAIL Y NOMBRE)
            INSERT INTO sesion (id_entrenador, id_clase, estado, sala, inicio, fin) VALUES
                ((SELECT id_usuario FROM usuario WHERE email = 'miguel@gimnasio.com'), (SELECT id_clase FROM clase WHERE nombre = 'Yoga Relax'), 'PROGRAMADA', 'SALA_PRINCIPAL', '2026-05-10 10:00:00', '2026-05-10 11:00:00'),
                ((SELECT id_usuario FROM usuario WHERE email = 'sara@gimnasio.com'), (SELECT id_clase FROM clase WHERE nombre = 'Crossfit Extremo'), 'PROGRAMADA', 'SALA_MULTIUSOS', '2026-05-11 18:00:00', '2026-05-11 19:00:00'),
                ((SELECT id_usuario FROM usuario WHERE email = 'javier@gimnasio.com'), (SELECT id_clase FROM clase WHERE nombre = 'Pilates Core'), 'PROGRAMADA', 'SALA_ZEN', '2026-05-12 09:00:00', '2026-05-12 10:00:00');

            -- 6. SUSCRIPCIONES Y RESERVAS
            INSERT INTO suscripcion (id_plan, id_socio, estado, fecha_inicio, fecha_fin) VALUES
                ((SELECT id_plan FROM plan WHERE nombre = 'Básico'), (SELECT id_usuario FROM usuario WHERE email = 'alberto@correo.com'), 'ACTIVA', '2026-05-01', '2026-06-01');
            INSERT INTO reserva (id_socio, id_sesion, estado, fecha_reserva) VALUES
                ((SELECT id_usuario FROM usuario WHERE email = 'alberto@correo.com'), (SELECT s.id_sesion FROM sesion s JOIN clase c ON s.id_clase = c.id_clase WHERE c.nombre = 'Yoga Relax' LIMIT 1), 'RESERVADA', '2026-05-05 08:00:00');
            """;


        // Declaramos variables fuera para poder cerrarlas en el finally
        Connection conexion = null;
        PreparedStatement pstmt = null;

        try {
            // 1. Obtenemos la conexión Singleton
            conexion = ConexionDB.getConexion();

            // 2. Preparamos la sentencia pasándole el bloque SQL masivo
            pstmt = conexion.prepareStatement(sql);

            // 3. Ejecutamos la inyección (como no es un SELECT, usamos execute en lugar de executeQuery)
            pstmt.execute();

            System.out.println("✅ Mock Data inyectada con éxito.");

        } catch (Exception e) {
            System.err.println("❌ ERROR al cargar datos: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // 4. CIERRE SEGURO DE RECURSOS (Solo cerramos el PreparedStatement)
            try {
                if (pstmt != null) {
                    pstmt.close();
                    System.out.println("PreparedStatement cerrado correctamente.");
                }
            } catch (Exception e) {
                System.err.println("❌ Error al cerrar el PreparedStatement: " + e.getMessage());
            }

            // Importante : Mantenemos la conexión 'conexion' abierta para que el Main pueda seguir funcionando
            System.out.println("Conexión abierta.");
        }
    }
}






    /*  Asi lo hemos insertado manual ,


    -- 1. SECRETARIOS (IDs del 1 al 5)
        INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) VALUES
('ACTIVO', 'Laura', 'Gómez', 'laura@gimnasio.com', 'pass123', '600111111'),
        ('ACTIVO', 'Carlos', 'Ruiz', 'carlos@gimnasio.com', 'pass123', '600222222'),
        ('ACTIVO', 'Ana', 'Martínez', 'ana@gimnasio.com', 'pass123', '600333333'),
        ('ACTIVO', 'David', 'Sánchez', 'david@gimnasio.com', 'pass123', '600444444'),
        ('ACTIVO', 'Elena', 'Díaz', 'elena@gimnasio.com', 'pass123', '600555555');

        INSERT INTO secretario (id_usuario, turno) VALUES
(1, 'Mañana'),
        (2, 'Tarde'),
        (3, 'Mañana'),
        (4, 'Tarde'),
        (5, 'Mañana');

        -- 2. ENTRENADORES (IDs del 6 al 10)
        INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) VALUES
('ACTIVO', 'Miguel', 'Fuerza', 'miguel@gimnasio.com', 'pass123', '611111111'),
        ('ACTIVO', 'Sara', 'Agil', 'sara@gimnasio.com', 'pass123', '611222222'),
        ('ACTIVO', 'Javier', 'Zen', 'javier@gimnasio.com', 'pass123', '611333333'),
        ('ACTIVO', 'Lucía', 'Core', 'lucia@gimnasio.com', 'pass123', '611444444'),
        ('ACTIVO', 'Pedro', 'Rueda', 'pedro@gimnasio.com', 'pass123', '611555555');

        INSERT INTO entrenador (id_usuario, especialidad) VALUES
(6, 'Musculación'),
        (7, 'Crossfit'),
        (8, 'Yoga'),
        (9, 'Pilates'),
        (10, 'Spinning');

        -- 3. SOCIOS (IDs del 11 al 15)
        INSERT INTO usuario (estado, nombre, apellido, email, password, telefono) VALUES
('ACTIVO', 'Alberto', 'Cliente1', 'alberto@correo.com', 'pass123', '622111111'),
        ('ACTIVO', 'Beatriz', 'Cliente2', 'beatriz@correo.com', 'pass123', '622222222'),
        ('ACTIVO', 'Carmen', 'Cliente3', 'carmen@correo.com', 'pass123', '622333333'),
        ('ACTIVO', 'Diego', 'Cliente4', 'diego@correo.com', 'pass123', '622444444'),
        ('ACTIVO', 'Eva', 'Cliente5', 'eva@correo.com', 'pass123', '622555555');

        INSERT INTO socio (id_usuario, fecha_alta) VALUES
(11, '2026-05-01'),
        (12, '2026-05-02'),
        (13, '2026-05-03'),
        (14, '2026-04-15'),
        (15, '2026-04-20');

        -- 4. PLANES (IDs del 1 al 5)
        INSERT INTO plan (estado, nombre, precio_mensual) VALUES
('ACTIVO', 'Básico', 29.99),
        ('ACTIVO', 'Estándar', 39.99),
        ('ACTIVO', 'Premium', 49.99),
        ('ACTIVO', 'VIP', 59.99),
        ('ACTIVO', 'Estudiante', 19.99);

        -- 5. CLASES (IDs del 1 al 5)
        INSERT INTO clase (estado, nombre, aforo_max) VALUES
('ACTIVO', 'Yoga Relax', 20),
        ('ACTIVO', 'Crossfit Extremo', 15),
        ('ACTIVO', 'Pilates Core', 25),
        ('ACTIVO', 'Zumba Dance', 30),
        ('ACTIVO', 'Spinning Pro', 20);

        -- 6. SESIONES (IDs del 1 al 5)
-- Vinculamos Entrenadores (6 al 10) con Clases (1 al 5)
        INSERT INTO sesion (id_entrenador, id_clase, estado, sala, inicio, fin) VALUES
(6, 1, 'PROGRAMADA', 'SALA_PRINCIPAL', '2026-05-10 10:00:00', '2026-05-10 11:00:00'),
        (7, 2, 'PROGRAMADA', 'SALA_MULTIUSOS', '2026-05-11 18:00:00', '2026-05-11 19:00:00'),
        (8, 3, 'PROGRAMADA', 'SALA_ZEN', '2026-05-12 09:00:00', '2026-05-12 10:00:00'),
        (9, 4, 'PROGRAMADA', 'SALA_PRINCIPAL', '2026-05-13 19:30:00', '2026-05-13 20:30:00'),
        (10, 5, 'PROGRAMADA', 'SALA_SPINNING', '2026-05-14 14:00:00', '2026-05-14 15:00:00');

        -- 7. SUSCRIPCIONES (IDs del 1 al 5)
-- Vinculamos Planes (1 al 5) con Socios (11 al 15)
        INSERT INTO suscripcion (id_plan, id_socio, estado, fecha_inicio, fecha_fin) VALUES
(1, 11, 'ACTIVA', '2026-05-01', '2026-06-01'),
        (2, 12, 'ACTIVA', '2026-05-02', '2026-06-02'),
        (3, 13, 'ACTIVA', '2026-05-03', '2026-06-03'),
        (4, 14, 'VENCIDA', '2026-03-15', '2026-04-15'),
        (5, 15, 'ACTIVA', '2026-04-20', '2026-05-20');

        -- 8. RESERVAS
-- Vinculamos Socios (11 al 15) con Sesiones (1 al 5)
        INSERT INTO reserva (id_socio, id_sesion, estado, fecha_reserva) VALUES
(11, 1, 'RESERVADA', '2026-05-05 08:00:00'),
        (12, 2, 'RESERVADA', '2026-05-06 09:15:00'),
        (13, 3, 'RESERVADA', '2026-05-07 10:30:00'),
        (14, 4, 'RESERVADA', '2026-05-08 11:45:00'),
        (15, 5, 'RESERVADA', '2026-05-09 12:00:00');


*/