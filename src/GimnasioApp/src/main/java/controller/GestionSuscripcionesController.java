package controller;

import dao.PlanDAO;
import dao.SocioDAO;
import dao.SuscripcionDAO;
import model.Plan;
import model.Socio;
import model.Suscripcion;
import model.Usuario;
import model.Admin;
import model.enums.EstadoSuscripcion;
import utils.LectorConsola;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR DE SUSCRIPCIONES
 * ========================================================================================
 * OBJETIVO: Gestionar la venta de planes a los socios y controlar sus fechas de acceso.
 * ========================================================================================
 */
public class GestionSuscripcionesController {

    private SuscripcionDAO suscripcionDAO;
    private PlanDAO planDAO;
    private SocioDAO socioDAO;

    private Scanner teclado;
    private Usuario usuarioActual;

    // Se solicitan los 3 DAOs para realizar la validación de existencia del socio y del plan
    public GestionSuscripcionesController(SuscripcionDAO suscripcionDAO, PlanDAO planDAO, SocioDAO socioDAO, Scanner teclado, Usuario usuarioActual) {
        this.suscripcionDAO = suscripcionDAO;
        this.planDAO = planDAO;
        this.socioDAO = socioDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }

    public void mostrar() {
        int opcionMenu = -1;

        do {
            System.out.println("\n--- GESTIÓN DE SUSCRIPCIONES ---");
            System.out.println("1. Mostrar todas las suscripciones");
            System.out.println("2. Vender nueva suscripción a un Socio");
            System.out.println("3. Cancelar una suscripción activa");

            if (usuarioActual instanceof Admin) {
                System.out.println("4. Borrado permanente (Admin)");
            }

            System.out.println("0. Volver al menú anterior");
            System.out.print("Selección: ");

            try {
                opcionMenu = Integer.parseInt(teclado.nextLine().trim());

                switch (opcionMenu) {
                    case 1: mostrarTodas(); break;
                    case 2: venderSuscripcion(); break;
                    case 3: cancelarSuscripcion(); break;
                    case 4:
                        if (usuarioActual instanceof Admin) {
                            borradoPermanente();
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
    // MÉTODOS DE ACCIÓN
    // ====================================================================================

    private void mostrarTodas() {
        System.out.println("\n--- LISTADO DE SUSCRIPCIONES ---");
        List<Suscripcion> lista = suscripcionDAO.selectAll();

        if (lista.isEmpty()) {
            System.out.println("No hay suscripciones registradas.");
        } else {
            System.out.println("ID  | ESTADO     | ID SOCIO | ID PLAN | INICIO     | FIN");
            System.out.println("----------------------------------------------------------------");
            for (Suscripcion s : lista) {
                System.out.printf("%-3d | %-10s | %-8d | %-7d | %s | %s\n",
                        s.getIdSuscripcion(), s.getEstado(), s.getIdSocio(),
                        s.getIdPlan(), s.getFechaInicio(), s.getFechaFin());
            }
            System.out.println("----------------------------------------------------------------");
        }
    }

    private void venderSuscripcion() {
        System.out.println("\n--- VENDER NUEVA SUSCRIPCIÓN ---");

        // 1. VALIDACIÓN DEL SOCIO
        int idSocio = LectorConsola.leerIdConCancelacion(teclado, "ID del Socio que compra");
        if (idSocio <= 0) return;

        Socio socioComprador = socioDAO.selectById(idSocio);
        if (socioComprador == null) {
            System.out.println("❌ No existe ningún socio con el ID " + idSocio);
            return;
        }

        // 2. VALIDACIÓN DEL PLAN
        int idPlan = LectorConsola.leerIdConCancelacion(teclado, "ID del Plan a contratar");
        if (idPlan <= 0) return;

        Plan planContratado = planDAO.selectById(idPlan);
        if (planContratado == null) {
            System.out.println("❌ No existe ningún plan con el ID " + idPlan);
            return;
        }

        // 3. ASIGNACIÓN DE FECHAS
        Suscripcion nuevaSuscripcion = new Suscripcion();
        nuevaSuscripcion.setIdSocio(idSocio);
        nuevaSuscripcion.setIdPlan(idPlan);
        nuevaSuscripcion.setEstado(EstadoSuscripcion.ACTIVA);

        /* -------------------------------------------------------------------
         * LocalDate.now() obtiene la fecha actual del sistema.
         * plusMonths(1) añade exactamente un mes, calculando automáticamente
         * años bisiestos y variación de días por mes.
         * ------------------------------------------------------------------- */
        LocalDate fechaDeHoy = LocalDate.now();
        LocalDate fechaElMesQueViene = fechaDeHoy.plusMonths(1);

        nuevaSuscripcion.setFechaInicio(fechaDeHoy);
        nuevaSuscripcion.setFechaFin(fechaElMesQueViene);

        // 4. REGISTRO EN BASE DE DATOS
        if (suscripcionDAO.insert(nuevaSuscripcion) > 0) {
            System.out.println("✅ Venta completada. El socio " + socioComprador.getNombre() +
                    " tiene acceso hasta el " + fechaElMesQueViene);
        } else {
            System.out.println("❌ Hubo un error al procesar la venta en la base de datos.");
        }
    }

    private void cancelarSuscripcion() {
        System.out.println("\n--- CANCELAR SUSCRIPCIÓN ---");

        int idSuscripcion = LectorConsola.leerIdConCancelacion(teclado, "Introduce el ID de la suscripción a cancelar");
        if (idSuscripcion <= 0) return;

        Suscripcion suscripcion = suscripcionDAO.selectById(idSuscripcion);

        if (suscripcion != null) {
            if (suscripcion.getEstado() == EstadoSuscripcion.CANCELADA) {
                System.out.println("⚠️ Esta suscripción ya estaba cancelada.");
            } else {
                suscripcion.setEstado(EstadoSuscripcion.CANCELADA);
                suscripcionDAO.update(suscripcion);
                System.out.println("✅ Suscripción cancelada con éxito.");
            }
        } else {
            System.out.println("❌ No se encontró la suscripción.");
        }
    }

    private void borradoPermanente() {
        System.out.println("\n--- ☢️ BORRADO FÍSICO DE SUSCRIPCIÓN ---");

        if (usuarioActual instanceof Admin) {
            int idSuscripcion = LectorConsola.leerIdConCancelacion(teclado, "ID de la suscripción a destruir");
            if (idSuscripcion <= 0) return;

            System.out.print("⚠️ ¿Seguro? (S/N): ");
            if (teclado.nextLine().trim().equalsIgnoreCase("S")) {
                if (suscripcionDAO.delete(idSuscripcion) > 0) {
                    System.out.println("✅ Borrado físicamente.");
                } else {
                    System.out.println("❌ No se pudo borrar.");
                }
            } else {
                System.out.println("Cancelado.");
            }
        } else {
            throw new SecurityException("⛔ Acceso denegado.");
        }
    }
}