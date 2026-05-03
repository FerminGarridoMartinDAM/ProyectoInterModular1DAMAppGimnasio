package utils;

import dao.*;
import model.*;
import model.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// He dejado este metodo para ver como hacerle un insert inicial con java pero voy a usar un insert sql que es mucho mas sencillo.

/* ========================================================================================
 * CARGADOR DE DATOS INICIALES (SEEDER) - NIVEL AVANZADO
 * ========================================================================================
 * OBJETIVO: Poblar la base de datos de forma dinámica.
 * Las entidades padre se insertan secuencialmente. Para las entidades dependientes
 * (Suscripciones, Reservas, etc.), se realizan consultas previas (SELECT) para
 * obtener los IDs reales asignados por el autoincrement de PostgreSQL, evitando así
 * el 'hardcoding' matemático que asume IDs fijos y protegiendo la integridad referencial.
 * ========================================================================================
 */
public class CargadorDatosIniciales {

    public static void inyectarDatos(SocioDAO socioDAO, EntrenadorDAO entrenadorDAO,
                                     SecretarioDAO secretarioDAO, PlanDAO planDAO,
                                     ClaseDAO claseDAO, SesionDAO sesionDAO,
                                     SuscripcionDAO suscripcionDAO, ReservaDAO reservaDAO) {

        System.out.println(" Comprobando estado de la base de datos.");

        // ====================================================================
        // FASE 1: ENTIDADES INDEPENDIENTES (Se generan y Postgres asigna ID)
        // ====================================================================

        // 1. CARGA DE SECRETARIOS
        if (secretarioDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Secretarios vacía. Inyectando 5 registros...");
            for (int i = 0; i < 5; i++) {
                Secretario secretario = new Secretario();
                secretario.setNombre("NombreSecretario" + (i + 1));
                secretario.setApellido("ApellidoSecretario" + (i + 1));
                secretario.setEmail("emailSecretario" + (i + 1) + "@gimnasio.com");
                secretario.setTelefono("11111111" + (i + 1));
                secretario.setPassword("passSecretario" + (i + 1));
                secretario.setEstado(EstadoUsuario.ACTIVO);
                secretario.setTurno("Mañana");
                secretarioDAO.insert(secretario);
            }
        }

        // 2. CARGA DE ENTRENADORES
        if (entrenadorDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Entrenadores vacía. Inyectando 5 registros...");
            String[] especialidades = {"Musculación", "Crossfit", "Yoga", "Pilates", "Spinning"};
            for (int i = 0; i < 5; i++) {
                Entrenador entrenador = new Entrenador();
                entrenador.setNombre("NombreEntrenador" + (i + 1));
                entrenador.setApellido("ApellidoEntrenador" + (i + 1));
                entrenador.setEmail("emailEntrenador" + (i + 1) + "@gimnasio.com");
                entrenador.setTelefono("22222222" + (i + 1));
                entrenador.setPassword("passEntrenador" + (i + 1));
                entrenador.setEstado(EstadoUsuario.ACTIVO);
                entrenador.setEspecialidad(especialidades[i]);
                entrenadorDAO.insert(entrenador);
            }
        }

        // 3. CARGA DE SOCIOS
        if (socioDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Socios vacía. Inyectando 5 registros...");
            for (int i = 0; i < 5; i++) {
                Socio socio = new Socio();
                socio.setNombre("NombreSocio" + (i + 1));
                socio.setApellido("ApellidoSocio" + (i + 1));
                socio.setEmail("emailSocio" + (i + 1) + "@correo.com");
                socio.setTelefono("33333333" + (i + 1));
                socio.setPassword("passSocio" + (i + 1));
                socio.setEstado(EstadoUsuario.ACTIVO);
                socio.setFechaAlta(LocalDate.now().minusDays(i + 1));
                socioDAO.insert(socio);
            }
        }

        // 4. CARGA DE PLANES
        if (planDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Planes vacía. Inyectando 5 registros...");
            String[] nombresPlanes = {"Básico", "Estándar", "Premium", "VIP", "Estudiante"};
            double[] precios = {29.99, 39.99, 49.99, 59.99, 19.99};
            for (int i = 0; i < 5; i++) {
                Plan plan = new Plan();
                plan.setNombre(nombresPlanes[i]);
                plan.setPrecioMensual(precios[i]);
                plan.setEstado(EstadoPlan.ACTIVO);
                planDAO.insert(plan);
            }
        }

        // 5. CARGA DE CLASES
        if (claseDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Clases vacía. Inyectando 5 registros...");
            String[] nombresClases = {"Yoga Relax", "Crossfit Extremo", "Pilates Core", "Zumba Dance", "Spinning Pro"};
            for (int i = 0; i < 5; i++) {
                Clase clase = new Clase();
                clase.setNombre(nombresClases[i]);
                clase.setAforoMax(15 + ((i + 1) * 5));
                clase.setEstado(EstadoClase.ACTIVO);
                claseDAO.insert(clase);
            }
        }

        // ====================================================================
        // FASE 2: ENTIDADES DEPENDIENTES Requieren consulta previa a la BD para
        // ====================================================================

        // 6. CARGA DE SESIONES (Depende de Entrenador y Clase)
        if (sesionDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Sesiones vacía. Inyectando 5 registros...");

            // SELECT: Traemos los datos vivos de la BD para usar sus IDs reales
            List<Entrenador> entrenadoresActivos = entrenadorDAO.selectAll();
            List<Clase> clasesActivas = claseDAO.selectAll();
            SalaGimnasio[] salasDisponibles = SalaGimnasio.values();

            // Aseguramos que haya al menos 5 para vincular
            if (entrenadoresActivos.size() >= 5 && clasesActivas.size() >= 5) {
                for (int i = 0; i < 5; i++) {
                    Sesion sesion = new Sesion();

                    // VINCULACIÓN DINÁMICA: Extraemos el ID generado por Postgres
                    sesion.setIdEntrenador(entrenadoresActivos.get(i).getIdUsuario());
                    sesion.setIdClase(clasesActivas.get(i).getIdClase());

                    sesion.setSala(salasDisponibles[i].name());
                    LocalDateTime inicio = LocalDateTime.now().plusDays(i + 1).withHour(10).withMinute(0).withSecond(0).withNano(0);
                    sesion.setInicio(inicio);
                    sesion.setFin(inicio.plusHours(1));
                    sesion.setEstado(EstadoSesion.PROGRAMADA);

                    sesionDAO.insert(sesion);
                }
            } else {
                System.out.println("⚠️ No hay suficientes Entrenadores o Clases para crear Sesiones de prueba.");
            }
        }

        // 7. CARGA DE SUSCRIPCIONES (Depende de Plan y Socio)
        if (suscripcionDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Suscripciones vacía. Inyectando 5 registros...");

            // SELECT: Traemos los datos vivos
            List<Plan> planesActivos = planDAO.selectAll();
            List<Socio> sociosActivos = socioDAO.selectAll();

            if (planesActivos.size() >= 5 && sociosActivos.size() >= 5) {
                for (int i = 0; i < 5; i++) {
                    Suscripcion suscripcion = new Suscripcion();

                    // VINCULACIÓN DINÁMICA
                    suscripcion.setIdPlan(planesActivos.get(i).getIdPlan());
                    suscripcion.setIdSocio(sociosActivos.get(i).getIdUsuario());

                    LocalDate inicio = LocalDate.now().minusDays(i + 1);
                    suscripcion.setFechaInicio(inicio);
                    suscripcion.setFechaFin(inicio.plusMonths(1));
                    suscripcion.setEstado(EstadoSuscripcion.ACTIVA);

                    suscripcionDAO.insert(suscripcion);
                }
            } else {
                System.out.println("⚠️ No hay suficientes Planes o Socios para crear Suscripciones de prueba.");
            }
        }

        // 8. CARGA DE RESERVAS (Depende de Socio y Sesion)
        if (reservaDAO.selectAll().isEmpty()) {
            System.out.println("Tabla Reservas vacía. Inyectando 5 registros...");

            // SELECT: Traemos los datos vivos
            List<Socio> sociosActivos = socioDAO.selectAll();
            List<Sesion> sesionesActivas = sesionDAO.selectAll();

            if (sociosActivos.size() >= 5 && sesionesActivas.size() >= 5) {
                for (int i = 0; i < 5; i++) {
                    Reserva reserva = new Reserva();

                    // VINCULACIÓN DINÁMICA
                    reserva.setIdSocio(sociosActivos.get(i).getIdUsuario());
                    reserva.setIdSesion(sesionesActivas.get(i).getIdSesion());

                    reserva.setFechaReserva(LocalDateTime.now());
                    reserva.setEstado(EstadoReserva.RESERVADA);

                    reservaDAO.insert(reserva);
                }
            } else {
                System.out.println("⚠️ No hay suficientes Socios o Sesiones para crear Reservas de prueba.");
            }
        }

        System.out.println("✅ Base de datos lista para operar con datos masivos.");
    }
}
