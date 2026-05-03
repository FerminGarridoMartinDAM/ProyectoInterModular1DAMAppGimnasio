package controller;

import dao.PlanDAO;
import model.Plan;
import model.Usuario;
import model.Admin;
import model.enums.EstadoPlan;

import java.util.List;
import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR PARA GESTIONAR LOS PLANES DEL GIMNASIO
 * ========================================================================================
 * OBJETIVO: Crear, listar y modificar las tarifas que el gimnasio ofrece.
 *
 * - Un Plan nunca caduca por sí solo, pero el dueño puede ponerlo en estado INACTIVO
 *   para que nadie más pueda comprarlo a partir de hoy (por ejemplo, si suben los precios).
 * ========================================================================================
 */
public class GestionPlanesController {

    private PlanDAO planDAO;
    private Scanner teclado;
    private Usuario usuarioActual; // Para verificar si es Admin

    public GestionPlanesController(PlanDAO planDAO, Scanner teclado, Usuario usuarioActual) {
        this.planDAO = planDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }

    public void mostrarMenu() {
        int opcionMenu = -1;

        do {
            System.out.println("\n--- GESTIÓN DE PLANES Y TARIFAS ---");
            System.out.println("1. Mostrar todos los planes");
            System.out.println("2. Crear un nuevo plan");
            System.out.println("3. Cambiar estado de un plan (Activar / Desactivar)");

            if (usuarioActual instanceof Admin) {
                System.out.println("4. Borrar un plan permanentemente");
            }

            System.out.println("0. Volver al menú anterior");
            System.out.print("Selección: ");

            try {
                opcionMenu = Integer.parseInt(teclado.nextLine().trim());

                switch (opcionMenu) {
                    case 1: mostrarTodosLosPlanes(); break;
                    case 2: crearPlan(); break;
                    case 3: cambiarEstadoPlan(); break;
                    case 4:
                        if (usuarioActual instanceof Admin) {
                            borrarPlanFisico();
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
    private void mostrarTodosLosPlanes() {
        System.out.println("\n--- LISTADO DE PLANES ---");
        List<Plan> listaPlanes = planDAO.selectAll();

        if (listaPlanes.isEmpty()) {
            System.out.println(" No hay ningún plan registrado en la base de datos.");
        } else {
            System.out.println("ID  | ESTADO   | NOMBRE DEL PLAN         | PRECIO MENSUAL");
            System.out.println("---------------------------------------------------------");

            for (Plan plan : listaPlanes) {
                // Formateamos el precio para que salga con dos decimales y el símbolo de Euro
                System.out.printf("%-3d | %-8s | %-23s | %.2f €\n",
                        plan.getIdPlan(),
                        plan.getEstado(),
                        plan.getNombre(),
                        plan.getPrecioMensual());
            }
            System.out.println("---------------------------------------------------------");
        }
    }

    // --- OPCIÓN 2: CREAR ---
    private void crearPlan() {
        System.out.println("\n--- CREAR NUEVO PLAN ---");
        Plan nuevoPlan = new Plan();

        System.out.print("Nombre del Plan (ej. 'Tarifa Plana', 'Solo Mañanas'): ");
        nuevoPlan.setNombre(teclado.nextLine().trim());

        System.out.print("Precio mensual (usa punto para decimales, ej. 29.99): ");
        try {
            double precio = Double.parseDouble(teclado.nextLine().trim());
            nuevoPlan.setPrecioMensual(precio);

            // Los planes siempre nacen activos para que se puedan vender ya
            nuevoPlan.setEstado(EstadoPlan.ACTIVO);

            if (planDAO.insert(nuevoPlan) > 0) {
                System.out.println("✅ Plan registrado y listo para venderse.");
            } else {
                System.out.println("❌ Hubo un error al guardar el plan en la base de datos.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato de precio incorrecto. Recuerda usar un punto (.) para los decimales.");
        }
    }


    // --- OPCIÓN 3: CAMBIAR ESTADO ---
    private void cambiarEstadoPlan() {
        System.out.println("\n--- CAMBIAR ESTADO DE UN PLAN ---");


        int id = utils.LectorConsola.leerIdConCancelacion(teclado, "Introduce el ID del Plan");
        if (id <= 0) return;

        /* CÓDIGO ANTIGUO:
        System.out.print("Introduce el ID del Plan: ");
        try {
            int id = Integer.parseInt(teclado.nextLine().trim());
        */

        Plan planExistente = planDAO.selectById(id);

        if (planExistente != null) {
            EstadoPlan nuevoEstado;

            // Si estaba activo, lo desactivamos. Si estaba inactivo, lo activamos. COMO SI FUERA UN INTERRUPTTOR
            if (planExistente.getEstado() == EstadoPlan.ACTIVO) {
                nuevoEstado = EstadoPlan.INACTIVO;
                System.out.println("⚠️ Vas a marcar el plan como INACTIVO. Los socios ya no podrán contratarlo.");
            } else {
                nuevoEstado = EstadoPlan.ACTIVO;
                System.out.println("✅ Vas a marcar el plan como ACTIVO. Volverá a estar disponible para contratar.");
            }

            if (planDAO.actualizarEstado(id, nuevoEstado) > 0) {
                System.out.println("✅ El estado del plan se ha actualizado correctamente a: " + nuevoEstado);
            } else {
                System.out.println("❌ Hubo un error al actualizar el estado en la base de datos.");
            }
        } else {
            System.out.println("❌ No existe ningún plan con ese ID.");
        }

        /* CÓDIGO ANTIGUO (Cierre del try-catch):
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato de ID incorrecto.");
        }
        */
    }

    // --- OPCIÓN 4: BORRADO FÍSICO ---
    private void borrarPlanFisico() {
        System.out.println("\n--- ☢️ BORRADO PERMANENTE DE PLAN ☢️ ---");

        if (usuarioActual instanceof Admin) {



            int id = utils.LectorConsola.leerIdConCancelacion(teclado, "Introduce el ID del Plan a ELIMINAR");
            if (id <= 0) return;

            /* CÓDIGO ANTIGUO:
            System.out.print("Introduce el ID del Plan a ELIMINAR: ");
            try {
                int id = Integer.parseInt(teclado.nextLine().trim());
            */

            System.out.print("⚠️ ¿Estás seguro? Si hay socios con este plan, dará error por seguridad de la BD. (S/N): ");
            String confirmacion = teclado.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S")) {
                int resultado = planDAO.delete(id);
                if (resultado > 0) {
                    System.out.println("✅ El plan ha sido borrado físicamente.");
                } else {
                    System.out.println("❌ No se pudo borrar. Probablemente hay suscripciones vinculadas a este plan.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }

            /* CÓDIGO ANTIGUO (Cierre del try-catch):
            } catch (NumberFormatException e) {
                System.out.println("❌ Error de formato.");
            }
            */

        } else {
            throw new SecurityException("⛔ Acceso denegado al borrado de planes.");
        }
    }
}