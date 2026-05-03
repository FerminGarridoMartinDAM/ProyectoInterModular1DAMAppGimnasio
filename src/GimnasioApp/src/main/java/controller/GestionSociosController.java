package controller;

import dao.PlanDAO;
import dao.SocioDAO;
import dao.SuscripcionDAO;
import model.*;
import model.enums.EstadoUsuario;
import utils.ValidadorFechas;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;





/* ========================================================================================
 * CONTROLADOR PARA GESTIONAR A LOS ENTRENADORES
 * ========================================================================================
 * OBJETIVO: Gestionar el ciclo de vida de los clientes (Socios) del gimnasio.
 *

 * - Baja Lógica (Soft Delete): En lugar de borrar al socio, le ponemos la etiqueta
 *       INACTIVO. Es la práctica más segura en el mundo real, porque nos permite
 *       conservar su historial de pagos y suscripciones antiguas sin romper la base de datos.
 *
 *
 * - Borrado Físico (Hard Delete): Ejecuta un 'DELETE' real en SQL. Exclusivo para Admins.
 *  Importante aqui asegurarse que en la base de datos estan programados los "on delete Cascade" para que no se queden huerfanos.
 *
 *
 * ========================================================================================
 */
public class GestionSociosController {

    private SocioDAO socioDAO;
    private SuscripcionDAO suscripcionDAO;
    private PlanDAO planDAO;
    private Scanner teclado;
    private Usuario usuarioActual; // El guarda de seguridad

    public GestionSociosController(SocioDAO socioDAO, SuscripcionDAO suscripcionDAO, PlanDAO planDAO, Scanner teclado, Usuario usuarioActual) {
        this.socioDAO = socioDAO;
        this.suscripcionDAO = suscripcionDAO;
        this.planDAO = planDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }



    public void mostrarMenu() {
        int opcionMenu = -1;

        do {
            System.out.println("\n--- GESTIÓN DE SOCIOS ---");
            System.out.println("1. Mostrar lista de todos los socios");
            System.out.println("2. Alta de nuevo socio");
            System.out.println("3. Dar de baja a un socio (Desactivar)");
            //Esta opcion solo sale si es un admin.
            if (usuarioActual instanceof Admin) {
                System.out.println("4  . Borrado permanente de un socio");
            }
            System.out.println("0. Volver al menú anterior");
            System.out.print("Selección: ");

            try {
                opcionMenu = Integer.parseInt(teclado.nextLine().trim());

                switch (opcionMenu) {
                    case 1:
                        mostrarTodosLosSocios();
                        break;
                    case 2:
                        altaSocio();
                        break;
                    case 3:
                        bajaLogicaSocio();
                        break;
                    case 4:
                        //Y aqui lo mismo solo si es admin  deja usar la opcion 3.
                        if (usuarioActual instanceof Admin) {
                            borradoPermanenteSocio();
                        } else {
                            System.out.println("Error: Opción no válida. Elige una opción válida.");
                        }
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Error: Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número válido.");
            }
        } while (opcionMenu != 0);
    }

    // ====================================================================================
    // METODOS DEL MENU
    // ====================================================================================


    // --- OPCIÓN 1: MOSTRAR TODOS ---
    private void mostrarTodosLosSocios() {
        System.out.println("\n--- LISTADO DE SOCIOS ---");

        // 1. Traemos los socios del DAO.

        List<Socio> listaSocios = socioDAO.selectAll();
        List<Suscripcion> listaSuscripciones = suscripcionDAO.selectAll();
        List<Plan> listaPlanes = planDAO.selectAll();

        // 2. Comprobamos si la lista está vacía
        if (listaSocios.isEmpty()) {
            System.out.println("ℹ️ No hay ningún socio registrado en la base de datos en este momento.");
        } else {
            // 3. Recorremos la lista y la imprimimos de forma limpia
            System.out.println("ID  | ESTADO   | NOMBRE Y APELLIDO    | EMAIL                | ALTA       | PLAN ACTIVO");
            System.out.println("-------------------------------------------------------------------------------------------------");

            for (Socio socio : listaSocios) {

                // Lógica para descubrir el plan de este socio
                String nombrePlanActual = "Sin Plan"; // le hemos puesto "sin plan" pero lo podiamos inicializado com oquisieramos.

                // Buscamos entre todas las suscripciones la que sea de este socio y esté ACTIVA
                for (Suscripcion sub : listaSuscripciones) {
                    if (sub.getIdSocio() == socio.getIdUsuario() && sub.getEstado().toString().equals("ACTIVA")) {

                        // Si la encontramos, buscamos el nombre del plan usando el ID
                        for (Plan plan : listaPlanes) {
                            if (plan.getIdPlan() == sub.getIdPlan()) {
                                nombrePlanActual = plan.getNombre();
                                break; // Rompemos el bucle de planes, ya lo hemos encontrado
                            }
                        }
                        break; // Rompemos el bucle de suscripciones, un socio solo tiene una activa
                    }
                }
                System.out.printf("%-3d | %-8s | %s %s | %s | %s\n",
                        socio.getIdUsuario(),
                        socio.getEstado(),
                        socio.getNombre(),
                        socio.getApellido(),
                        socio.getEmail(),
                        socio.getFechaAlta());
            }
            System.out.println("------------------------------------------------------------------");
        }
    }

    // --- OPCIÓN 2: ALTA ---
    private void altaSocio() {
        System.out.println("\n--- ALTA DE NUEVO SOCIO ---");
        Socio nuevoSocio = new Socio();

        // 1. Datos de la tabla madre (Usuario)
        System.out.print("Nombre: ");
        nuevoSocio.setNombre(teclado.nextLine().trim());

        System.out.print("Apellido: ");
        nuevoSocio.setApellido(teclado.nextLine().trim());

        System.out.print("Email: ");
        nuevoSocio.setEmail(teclado.nextLine().trim());

        System.out.print("Contraseña temporal: ");
        nuevoSocio.setPassword(teclado.nextLine().trim());

        System.out.print("Teléfono: ");
        nuevoSocio.setTelefono(teclado.nextLine().trim());

        // Por defecto, un socio recién creado siempre nace activo
        nuevoSocio.setEstado(EstadoUsuario.ACTIVO);

        // 2. Datos de la tabla hija (Socio)
        System.out.println();
        LocalDate fechaAlta = ValidadorFechas.pedirSoloFecha(teclado, "Fecha de matriculación");
        nuevoSocio.setFechaAlta(fechaAlta);

        // 3. Enviamos al DAO
        int resultado = socioDAO.insert(nuevoSocio);

        if (resultado > 0) {
            System.out.println("✅ Socio registrado con éxito en el sistema.");
        } else {
            System.out.println("❌ Hubo un problema al registrar al socio en la base de datos.");
        }
    }

    // --- OPCIÓN 3: BAJA LÓGICA (SOFT DELETE) ---  en vez de hacerle un borrado permanente lo dejamos en inactivo por si necesitamos sus datos para algo.
    private void bajaLogicaSocio() {
        System.out.println("\n--- BAJA DE SOCIO (DESACTIVACIÓN) ---");

        int idSocio = utils.LectorConsola.leerIdConCancelacion(teclado, "Introduce el ID del socio a dar de baja");
        if (idSocio <= 0) return;

        /*System.out.print("Introduce el ID del socio a dar de baja: ");
        try {
            int idSocio = Integer.parseInt(teclado.nextLine().trim());*/

        // Primero, buscamos si el socio existe
        Socio socioABorrar = socioDAO.selectById(idSocio);

        if (socioABorrar != null) {
            if (socioABorrar.getEstado() == EstadoUsuario.INACTIVO) {
                System.out.println("⚠️ Este socio ya estaba dado de baja previamente.");
            } else {
                // Cambiamos su estado a INACTIVO
                socioABorrar.setEstado(EstadoUsuario.INACTIVO);
                // Usamos  update() para guardar el cambio de estado en la BD
                socioDAO.update(socioABorrar);
                System.out.println("✅ El socio ha sido desactivado correctamente (Baja lógica).");
            }
        } else {
            System.out.println("❌ No se encontró ningún socio con ese ID.");
        }

        /*} catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato de ID incorrecto.");
        }*/
    }

    // --- OPCIÓN 4: BORRADO FÍSICO (HARD DELETE ) --- Aqui si se borra el usuario permanente
    private void borradoPermanenteSocio() {
        System.out.println("\n--- ☢️ BORRADO PERMANENTE DE SOCIO ☢️ ---");

        // ADUANA DE SEGURIDAD: Solo pasas si eres un Admin . Ahora no hace falta porque he puesto que la opcion solo se vea si erea admin
        // Pero se deja como capa de seguridad.
        if (usuarioActual instanceof Admin) {

            int idSocio = utils.LectorConsola.leerIdConCancelacion(teclado, "Introduce el ID del socio a ELIMINAR del sistema");
            if (idSocio <= 0) return;

            /*System.out.print("Introduce el ID del socio a ELIMINAR del sistema: ");
            try {
                int idSocio = Integer.parseInt(teclado.nextLine().trim());*/

            System.out.print("⚠️ ¿Estás completamente seguro? Esta acción no se puede deshacer. (S/N): ");
            String confirmacion = teclado.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S")) {
                int resultado = socioDAO.delete(idSocio);

                if (resultado > 0) {
                    System.out.println("✅ El socio ha sido borrado físicamente de la base de datos.");
                } else {
                    System.out.println("❌ No se pudo borrar al socio (¿Quizás no existe o tiene dependencias?)");
                }
            } else {
                System.out.println("Operación de borrado cancelada.");
            }

            /*} catch (NumberFormatException e) {
                System.out.println("❌ Error: Formato de ID incorrecto.");
            }*/

        } else {
            // Y por lo tanto este tampoco haria falta pero lo vamos a dejar para verlo.
            System.out.println("⛔ ACCESO DENEGADO: No tienes el nivel de privilegios necesario (Se requiere rol Admin).");
            //De hecho lo que se aconseja  es lanzar una exception de seguridad que detinene el progama
            throw new SecurityException("❌Intento de acceso no autorizado al borrado físico de la base de datos.");
        }
    }
}