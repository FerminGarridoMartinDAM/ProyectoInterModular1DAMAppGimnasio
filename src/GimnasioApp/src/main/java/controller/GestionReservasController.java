package controller;

import dao.ReservaDAO;
import dao.SesionDAO;
import dao.SocioDAO;
import model.Reserva;
import model.Sesion;
import model.Socio;
import model.Usuario;
import model.Admin;
import model.enums.EstadoReserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR PARA GESTIONAR LAS RESERVAS
 * ========================================================================================
 * OBJETIVO: Unir a los Socios con las Sesiones mediante la tabla intermedia Reserva.
 *
 * - Clave Compuesta: Una reserva se identifica de forma única combinando el ID del Socio
 *   y el ID de la Sesión.
 * - Integridad Referencial: Antes de hacer el INSERT, debemos validar que tanto el
 *   Socio como la Sesión existen realmente en la base de datos.
 * ========================================================================================
 */
public class GestionReservasController {

    private ReservaDAO reservaDAO;
    private SocioDAO socioDAO;
    private SesionDAO sesionDAO;

    private Scanner teclado;
    private Usuario usuarioActual;

    public GestionReservasController(ReservaDAO reservaDAO, SocioDAO socioDAO, SesionDAO sesionDAO, Scanner teclado, Usuario usuarioActual) {
        this.reservaDAO = reservaDAO;
        this.socioDAO = socioDAO;
        this.sesionDAO = sesionDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }

    public void mostrarMenu() {
        int opcionMenu = -1;

        do {
            System.out.println("\n--- GESTIÓN DE RESERVAS ---");
            System.out.println("1. Mostrar todas las reservas del sistema");
            System.out.println("2. Registrar una nueva reserva");
            System.out.println("3. Cambiar estado de reserva (Cancelar / Marcar Asistencia)");

            if (usuarioActual instanceof Admin) {
                System.out.println("4. Borrado permanente de una reserva");
            }

            System.out.println("0. Volver al menú anterior");
            System.out.print("Selección: ");

            try {
                opcionMenu = Integer.parseInt(teclado.nextLine().trim());

                switch (opcionMenu) {
                    case 1: mostrarTodasLasReservas(); break;
                    case 2: registrarReserva(); break;
                    case 3: actualizarEstadoReserva(); break;
                    case 4:
                        if (usuarioActual instanceof Admin) {
                            borradoPermanenteReserva();
                        } else {
                            System.out.println("❌ Error: Opción no válida.");
                        }
                        break;
                    case 0: break;
                    default: System.out.println("❌ Error: Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Introduce un número válido.");
            }
        } while (opcionMenu != 0);
    }

    // ====================================================================================
    // MÉTODOS DEL MENÚ
    // ====================================================================================

    // --- OPCIÓN 1: MOSTRAR ---
    private void mostrarTodasLasReservas() {
        System.out.println("\n--- LISTADO GENERAL DE RESERVAS ---");
        List<Reserva> listaReservas = reservaDAO.selectAll();

        if (listaReservas.isEmpty()) {
            System.out.println("No hay ninguna reserva registrada en este momento.");
        } else {
            System.out.println("ID SOCIO | ID SESIÓN | ESTADO     | FECHA DE LA TRANSACCIÓN");
            System.out.println("-------------------------------------------------------------");

            for (Reserva reserva : listaReservas) {
                System.out.printf("%-8d | %-9d | %-10s | %s\n",
                        reserva.getIdSocio(),
                        reserva.getIdSesion(),
                        reserva.getEstado(),
                        reserva.getFechaReserva());
            }
            System.out.println("-------------------------------------------------------------");
        }
    }

    // --- OPCIÓN 2: REGISTRAR (INSERT) ---
    private void registrarReserva() {
        System.out.println("\n--- NUEVA RESERVA ---");

        // 1. Validar el Socio
        int idSocio = utils.LectorConsola.leerIdConCancelacion(teclado, "ID del Socio");
        if (idSocio <= 0) return;

        Socio socio = socioDAO.selectById(idSocio);
        if (socio == null) {
            System.out.println("❌ No existe ningún socio con ese ID.");
            return;
        }

        // 2. Validar la Sesión
        int idSesion = utils.LectorConsola.leerIdConCancelacion(teclado, "ID de la Sesión a reservar");
        if (idSesion <= 0) return;

        Sesion sesion = sesionDAO.selectById(idSesion);
        if (sesion == null) {
            System.out.println("❌ No existe ninguna sesión con ese ID.");
            return;
        }

        // 3. Crear y guardar la reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setIdSocio(idSocio);
        nuevaReserva.setIdSesion(idSesion);
        nuevaReserva.setEstado(EstadoReserva.RESERVADA);
        nuevaReserva.setFechaReserva(LocalDateTime.now()); // Sello de tiempo exacto de la transacción

        int resultado = reservaDAO.insert(nuevaReserva);

        if (resultado > 0) {
            System.out.println("✅ Reserva confirmada para el socio " + socio.getNombre() + " en la sesión " + idSesion);
        } else {
            System.out.println("❌ Error al guardar. Es posible que el socio ya tenga una reserva para esta sesión exacta.");
        }
    }

    // --- OPCIÓN 3: ACTUALIZAR ESTADO ---
    private void actualizarEstadoReserva() {
        System.out.println("\n--- MODIFICAR ESTADO DE RESERVA ---");

        int idSocio = utils.LectorConsola.leerIdConCancelacion(teclado, "ID del Socio");
        if (idSocio <= 0) return;

        int idSesion = utils.LectorConsola.leerIdConCancelacion(teclado, "ID de la Sesión");
        if (idSesion <= 0) return;

        // Buscamos si la reserva existe realmente en la base de datos
        Reserva reservaExistente = reservaDAO.selectById(idSocio, idSesion);

        if (reservaExistente != null) {
            System.out.println("Estado actual de la reserva: " + reservaExistente.getEstado());
            System.out.println("Selecciona el nuevo estado:");
            System.out.println("1. ASISTIDA (El socio acudió)");
            System.out.println("2. AUSENTE  (No se presentó)");
            System.out.println("3. CANCELADA (Avisó a tiempo)");
            System.out.print("Opción: ");

            try {
                int opcionEstado = Integer.parseInt(teclado.nextLine().trim());
                EstadoReserva nuevoEstado;

                switch (opcionEstado) {
                    case 1: nuevoEstado = EstadoReserva.ASISTIDA; break;
                    case 2: nuevoEstado = EstadoReserva.AUSENTE; break;
                    case 3: nuevoEstado = EstadoReserva.CANCELADA; break;
                    default:
                        System.out.println("❌ Opción de estado no válida.");
                        return;
                }

                // Modificamos el objeto y usamos el update estándar
                reservaExistente.setEstado(nuevoEstado);

                if (reservaDAO.update(reservaExistente) > 0) {
                    System.out.println("✅ El estado de la reserva se ha actualizado a: " + nuevoEstado);
                } else {
                    System.out.println("❌ Hubo un error al guardar el nuevo estado en la base de datos.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Error de formato.");
            }
        } else {
            System.out.println("❌ No se ha encontrado ninguna reserva para ese Socio y Sesión.");
        }
    }

    // --- OPCIÓN 4: BORRADO FÍSICO (HARD DELETE) ---
    private void borradoPermanenteReserva() {
        System.out.println("\n--- ☢️ BORRADO PERMANENTE DE RESERVA ☢️ ---");

        if (usuarioActual instanceof Admin) {

            int idSocio = utils.LectorConsola.leerIdConCancelacion(teclado, "ID del Socio");
            if (idSocio <= 0) return;

            int idSesion = utils.LectorConsola.leerIdConCancelacion(teclado, "ID de la Sesión");
            if (idSesion <= 0) return;

            System.out.print("⚠️ ¿Seguro que quieres eliminar este registro por completo? (S/N): ");
            if (teclado.nextLine().trim().equalsIgnoreCase("S")) {

                int resultado = reservaDAO.delete(idSocio, idSesion);

                if (resultado > 0) {
                    System.out.println("✅ Reserva eliminada de la base de datos.");
                } else {
                    System.out.println("❌ No se encontró ninguna reserva coincidente para borrar.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } else {
            System.out.println("❌ ACCESO DENEGADO.");
        }
    }
}