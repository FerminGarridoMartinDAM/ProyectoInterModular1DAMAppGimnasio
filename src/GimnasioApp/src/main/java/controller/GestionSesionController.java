package controller;

import dao.ClaseDAO;
import dao.EntrenadorDAO;
import dao.SesionDAO;
import model.Entrenador;
import model.Sesion;
import model.Usuario;
import model.enums.SalaGimnasio;
import utils.ValidadorFechas;

import java.time.LocalDateTime;
import java.util.Scanner;

/* ========================================================================================
 * MENU PARA GESTION DE SESIONES.
 * ========================================================================================
 * OBJETIVO DE ESTA CLASE:
 * Gestiona las sesiones, y dependiendo del tipo de usuario le da unas funcionalidades u otras.
 *
 *
 * CONCEPTOS CLAVE :
 * Autorización: El objeto 'usuarioActual' es nuestro guarda de seguridad. Antes de
 *    borrar nada, verifica quién es el usuario.
 * ========================================================================================
 */
public class GestionSesionController {

    private SesionDAO sesionDAO;
    private ClaseDAO claseDAO;
    private EntrenadorDAO entrenadorDAO;
    private Scanner teclado;
    // Guardamos quién ha hecho login para gestionar sus permisos
    private Usuario usuarioActual;


    public GestionSesionController(SesionDAO sesionDAO, ClaseDAO claseDAO, EntrenadorDAO entrenadorDAO, Scanner teclado, Usuario usuarioActual) {
        this.sesionDAO = sesionDAO;
        this.claseDAO = claseDAO;
        this.entrenadorDAO = entrenadorDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }

    public void mostrar() {
        int opcionMenu = -1;

        do {
            System.out.println("\n--- CALENDARIO DE SESIONES ---");
            System.out.println("1. Ver horario de sesiones");
            System.out.println("2. Programar nueva sesión");
            System.out.println("3. Reprogramar sesión existente");
            System.out.println("4. Cancelar sesión");
            System.out.println("0. Volver al menú anterior");
            System.out.print("Selección: ");

            try {
                opcionMenu = Integer.parseInt(teclado.nextLine().trim());

                switch (opcionMenu) {
                    case 1:
                        verHorarioDeSesiones();
                        break;
                    case 2:
                        programarNuevaSesion();
                        break;
                    case 3:
                        reprogramarSesionExistente();
                        break;
                    case 4:
                        cancelarSesion();
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
    // ZONA DE LÓGICA Y SEGURIDAD (MÉTODOS PRIVADOS)
    // ====================================================================================

    // --- OPCIÓN 1 ---
    private void verHorarioDeSesiones() {

        if (usuarioActual instanceof Entrenador) {
            // Casteamos al usuario genérico para tratarlo como Entrenador
            Entrenador entrenadorLogueado = (Entrenador) usuarioActual;
            System.out.println("\n--- TUS SESIONES ASIGNADAS ---");

            // ACTUALIZADO: selectAll() en lugar de obtenerTodasSesiones()
            sesionDAO.selectAll().stream()
                    .filter(sesion -> sesion.getIdEntrenador() == entrenadorLogueado.getIdEntrenador())
                    .forEach(System.out::println);

        } else {
            // Secretarios y Admins ven el horario global
            System.out.println("\n--- HORARIO COMPLETO DEL GIMNASIO ---");
            // ACTUALIZADO: selectAll() en lugar de obtenerTodasSesiones()
            sesionDAO.selectAll().forEach(System.out::println);
        }
    }

    // --- OPCIÓN 2 ---
    private void programarNuevaSesion() {
        System.out.println("\n--- NUEVA SESIÓN ---");
        Sesion nuevaSesion = new Sesion();
        nuevaSesion = rellenarDatosSesion(nuevaSesion);

        // ACTUALIZADO: insert() en lugar de insertarSesion()
        sesionDAO.insert(nuevaSesion);
        System.out.println("Sesión programada con éxito.");
    }

    // --- OPCIÓN 3 ---
    //Dejo comentado el metodo que tenia antes de crear el util para ver la diferencia,
    private void reprogramarSesionExistente() {
        System.out.println("\n--- REPROGRAMAR SESIÓN ---");

        int idSesionAModificar = utils.LectorConsola.leerIdConCancelacion(teclado, "ID de la sesión a modificar");

        if (idSesionAModificar <= 0) return;

        Sesion sesionAModificar = new Sesion();
        sesionAModificar.setIdSesion(idSesionAModificar);

        // Reutilizamos el formulario para pedir los nuevos datos
        sesionAModificar = rellenarDatosSesion(sesionAModificar);

        // Si el formulario fue cancelado internamente, salimos
        if (sesionAModificar == null) return;

        sesionDAO.update(sesionAModificar);
        System.out.println("✅ Sesión reprogramada correctamente.");
    }




    /*private void reprogramarSesionExistente() {
        System.out.println("\n--- REPROGRAMAR SESIÓN ---");
        System.out.print("ID de la sesión a modificar: ");

        try {
            int idSesionAModificar = Integer.parseInt(teclado.nextLine().trim());

            Sesion sesionAModificar = new Sesion();
            sesionAModificar.setIdSesion(idSesionAModificar);

            // Reutilizamos el formulario para pedir los nuevos datos de esa sesión
            sesionAModificar = rellenarDatosSesion(sesionAModificar);

            sesionDAO.update(sesionAModificar);
            System.out.println("Sesión reprogramada correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("Error: Formato de ID incorrecto.");
        }
    }*/

    // --- OPCIÓN 4 ---
    //Aqui lo mismo dejo comentado el metodo antiguo para ver la diferencia.
    private void cancelarSesion() {
        System.out.println("\n--- CANCELAR SESIÓN ---");

        int idSesionABorrar = utils.LectorConsola.leerIdConCancelacion(teclado, "ID de la sesión a borrar");

        if (idSesionABorrar <= 0) return;

        Sesion sesionABorrar = sesionDAO.selectById(idSesionABorrar);

        if (sesionABorrar != null) {
            if (usuarioActual instanceof Entrenador) {
                Entrenador entrenadorLogueado = (Entrenador) usuarioActual;

                if (sesionABorrar.getIdEntrenador() == entrenadorLogueado.getIdEntrenador()) {
                    sesionDAO.delete(idSesionABorrar);
                    System.out.println("✅ Tu sesión ha sido cancelada correctamente.");
                } else {
                    System.out.println("❌ ACCESO DENEGADO: No puedes cancelar las clases de otros profesores.");
                }
            } else {
                sesionDAO.delete(idSesionABorrar);
                System.out.println("✅ Sesión cancelada por administración.");
            }
        } else {
            System.out.println("❌ No existe ninguna sesión registrada con ese ID.");
        }
    }
  /*  private void cancelarSesion() {
        System.out.println("\n--- CANCELAR SESIÓN ---");
        System.out.print("ID de la sesión a borrar: ");

        try {
            int idSesionABorrar = Integer.parseInt(teclado.nextLine().trim());

            // Rescatamos la sesión de la base de datos para investigarla ANTES de borrarla
            Sesion sesionABorrar = sesionDAO.selectById(idSesionABorrar);

            if (sesionABorrar != null) {

                // --- ADUANA DE SEGURIDAD --- Para que el profesor solo pueda cambiar sus clases y no la de los demas.
                if (usuarioActual instanceof Entrenador) {
                    Entrenador entrenadorLogueado = (Entrenador) usuarioActual;

                    // Comprobación: Si el id del profesor de la sesion a borrar es la misma que el id del entreador logueado.
                    if (sesionABorrar.getIdEntrenador() == entrenadorLogueado.getIdEntrenador()) {
                        // ACTUALIZADO: delete() en lugar de eliminarSesion()
                        sesionDAO.delete(idSesionABorrar);
                        System.out.println("Tu sesión ha sido cancelada correctamente.");
                    } else {
                        System.out.println("ACCESO DENEGADO: No puedes cancelar las clases de otros profesores.");
                    }
                } else {
                    // Si llega aquí, es porque es Secretario o Admin. Tienen permiso universal.
                    sesionDAO.delete(idSesionABorrar);
                    System.out.println("Sesión cancelada por administración.");
                }

            } else {
                System.out.println("No existe ninguna sesión registrada con ese ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ID no válido.");
        }
    }*/

    // ====================================================================================
    // MÉTODO AUXILIAR PARA PEDIR DATOS
    // ====================================================================================
    //Aqui solo he comentado la parte que he cambiado por no llenar mucho el codigo.

    private Sesion rellenarDatosSesion(Sesion sesionEnProceso) {

        // 1. ELEGIR LA CLASE
        System.out.println("\n--- CATÁLOGO DE CLASES DISPONIBLES ---");
        claseDAO.selectAll().forEach(c ->
                System.out.println("ID: " + c.getIdClase() + " | Actividad: " + c.getNombre())
        );

        int idClase = utils.LectorConsola.leerIdConCancelacion(teclado, "Introduce el ID de la Clase elegida");
        if (idClase <= 0) return null; // Devolvemos null para indicar que se canceló la creación
        sesionEnProceso.setIdClase(idClase);

        /*Codigo antiguo
        System.out.print("Introduce el ID de la Clase elegida: ");
        sesionEnProceso.setIdClase(Integer.parseInt(teclado.nextLine().trim()));*/


        // 2. ELEGIR EL ENTRENADOR
        if (usuarioActual instanceof Entrenador) {
            Entrenador entrenadorLogueado = (Entrenador) usuarioActual;
            sesionEnProceso.setIdEntrenador(entrenadorLogueado.getIdEntrenador());
            System.out.println("ID Entrenador asignado automáticamente a tu perfil.");
        } else {
            System.out.println("\n--- PLANTILLA DE ENTRENADORES ---");
            entrenadorDAO.selectAll().forEach(e ->
                    System.out.println("ID: " + e.getIdEntrenador() + " | Nombre: " + e.getNombre() + " " + e.getApellido())
            );

            int idEntrenador = utils.LectorConsola.leerIdConCancelacion(teclado, "Introduce el ID del Entrenador");
            if (idEntrenador <= 0) return null;
            sesionEnProceso.setIdEntrenador(idEntrenador);
        }


        // 3. ELEGIR LA SALA
        System.out.println("\n--- SALAS DISPONIBLES ---");
        SalaGimnasio[] salas = SalaGimnasio.values();

        for (int i = 0; i < salas.length; i++) {
            System.out.println((i + 1) + ". " + salas[i].name());
        }

        int seleccionSala = utils.LectorConsola.leerIdConCancelacion(teclado, "Introduce el número de la sala asignada");
        if (seleccionSala <= 0) return null;

        if (seleccionSala <= salas.length) {
            sesionEnProceso.setSala(salas[seleccionSala - 1].name());
        } else {
            System.out.println("❌ Error: Sala no válida. Abortando...");
            return null;
        }

        // 4. DATOS DE TIEMPO
        LocalDateTime inicio;
        LocalDateTime fin;
        boolean horarioCoherente = false;

        do {
            System.out.println();
            inicio = ValidadorFechas.pedirFechaHora(teclado, "Fecha y hora de INICIO");
            fin = ValidadorFechas.pedirFechaHora(teclado, "Fecha y hora de FIN");

            if (fin.isAfter(inicio)) {
                sesionEnProceso.setInicio(inicio);
                sesionEnProceso.setFin(fin);
                horarioCoherente = true;
            } else {
                System.out.println("❌ Error de lógica: La sesión no puede terminar antes de empezar.");
                System.out.print("¿Quieres reintentar el horario? (S/N): ");
                if (!teclado.nextLine().trim().equalsIgnoreCase("S")) return null;
            }
        } while (!horarioCoherente);

        return sesionEnProceso;
    }
}