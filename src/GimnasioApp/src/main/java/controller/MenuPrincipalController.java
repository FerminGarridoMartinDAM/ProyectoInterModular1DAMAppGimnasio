package controller;

import dao.*;
import model.*;

import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR PRINCIPAL (EL DIRECTOR DE ORQUESTA)
 * ========================================================================================
 * OBJETIVO: Mantener el bucle de la aplicación vivo, gestionar el inicio de sesión
 * y mostrar el menú correcto según el tipo de usuario.
 * ========================================================================================
 */
public class MenuPrincipalController {

    // Necesitamos todos los DAOs aquí para poder pasárselos a los sub-controladores
    private UsuarioDAO usuarioDAO;
    private SocioDAO socioDAO;
    private EntrenadorDAO entrenadorDAO;
    private SecretarioDAO secretarioDAO;
    private PlanDAO planDAO;
    private SuscripcionDAO suscripcionDAO;
    private SesionDAO sesionDAO;
    private ClaseDAO claseDAO;
    private ReservaDAO reservaDAO;
    private Scanner teclado;

    public MenuPrincipalController(UsuarioDAO usuarioDAO, SocioDAO socioDAO, EntrenadorDAO entrenadorDAO, SecretarioDAO secretarioDAO, PlanDAO planDAO, SuscripcionDAO suscripcionDAO, SesionDAO sesionDAO, ClaseDAO claseDAO, ReservaDAO reservaDAO, Scanner teclado) {
        this.usuarioDAO = usuarioDAO;
        this.socioDAO = socioDAO;
        this.entrenadorDAO = entrenadorDAO;
        this.secretarioDAO = secretarioDAO;
        this.planDAO = planDAO;
        this.suscripcionDAO = suscripcionDAO;
        this.sesionDAO = sesionDAO;
        this.claseDAO = claseDAO;
        this.reservaDAO = reservaDAO;
        this.teclado = teclado;
    }

    public void iniciar() {
        boolean apagarSistema = false;
        LoginController loginController = new LoginController(usuarioDAO, teclado);

        while (!apagarSistema) {

            // 1. FASE DE AUTENTICACIÓN
            Usuario usuarioActual = null;
            while (usuarioActual == null) {
                usuarioActual = loginController.iniciarSesion();
            }

            // 2. PREPARACIÓN DE HERRAMIENTAS (Se instancian con el usuario ya logueado)
            GestionSociosController sociosCtrl = new GestionSociosController(socioDAO, suscripcionDAO, planDAO, teclado, usuarioActual);
            GestionEntrenadoresController entrenadoresCtrl = new GestionEntrenadoresController(entrenadorDAO, teclado, usuarioActual);
            GestionSecretariosController secretariosCtrl = new GestionSecretariosController(secretarioDAO, teclado, usuarioActual);
            GestionPlanesController planesCtrl = new GestionPlanesController(planDAO, teclado, usuarioActual);
            GestionSuscripcionesController suscripcionesCtrl = new GestionSuscripcionesController(suscripcionDAO, planDAO, socioDAO, teclado, usuarioActual);
            GestionSesionController sesionesCtrl = new GestionSesionController(sesionDAO, claseDAO, entrenadorDAO, teclado, usuarioActual);
            GestionReservasController reservasCtrl = new GestionReservasController(reservaDAO, socioDAO, sesionDAO, teclado, usuarioActual);

            // NUEVO: Instanciamos el controlador de Backup con todo el enrutamiento polimórfico
            GestionBackupController backupCtrl = new GestionBackupController(
                    usuarioDAO, socioDAO, entrenadorDAO, secretarioDAO,
                    claseDAO, sesionDAO, planDAO, suscripcionDAO, reservaDAO,
                    teclado, usuarioActual
            );

            // 3. FASE DE ENRUTAMIENTO POR ROLES
            boolean cerrarSesion = false;

            while (!cerrarSesion) {
                System.out.println("\n========================================");
                System.out.println("  PANEL DE CONTROL - " + usuarioActual.getClass().getSimpleName().toUpperCase());
                System.out.println("========================================");

                // --- MENÚ PARA ADMINISTRADORES Y SECRETARIOS ---
                if (usuarioActual instanceof Admin || usuarioActual instanceof Secretario) {
                    System.out.println("1. Gestión de Socios");
                    System.out.println("2. Gestión de Entrenadores");
                    if (usuarioActual instanceof Admin) System.out.println("3. Gestión de Secretarios");
                    System.out.println("4. Gestión de Planes y Tarifas");
                    System.out.println("5. Gestión de Suscripciones (Ventas)");
                    System.out.println("6. Gestión de Sesiones (Horarios)");
                    System.out.println("7. Gestión de Reservas");

                    // NUEVO: Opción exclusiva para el Administrador
                    if (usuarioActual instanceof Admin) {
                        System.out.println("8. Herramientas de Sistema (Backup/Reset)");
                    }

                    System.out.println("0. Cerrar Sesión");
                    System.out.print("Selección: ");

                    try {
                        int opcion = Integer.parseInt(teclado.nextLine().trim());
                        switch (opcion) {
                            case 1: sociosCtrl.mostrarMenu(); break;
                            case 2: entrenadoresCtrl.mostrarMenu(); break;
                            case 3:
                                if(usuarioActual instanceof Admin) secretariosCtrl.mostrarMenu();
                                else System.out.println("❌ Opción no válida.");
                                break;
                            case 4: planesCtrl.mostrarMenu(); break;
                            case 5: suscripcionesCtrl.mostrar(); break;
                            case 6: sesionesCtrl.mostrar(); break;
                            case 7: reservasCtrl.mostrarMenu(); break;
                            case 8:
                                if(usuarioActual instanceof Admin) backupCtrl.mostrarMenu();
                                else System.out.println("❌ Opción no válida.");
                                break;
                            case 0: cerrarSesion = true; break;
                            default: System.out.println("❌ Opción no válida.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Error: Introduce un número válido.");
                    }
                }

                // --- MENÚ PARA ENTRENADORES ---
                else if (usuarioActual instanceof Entrenador) {
                    System.out.println("1. Ver mi horario de clases");
                    System.out.println("2. Ver listado de Socios (Solo Lectura)");
                    System.out.println("0. Cerrar Sesión");
                    System.out.print("Selección: ");

                    try {
                        int opcion = Integer.parseInt(teclado.nextLine().trim());
                        switch (opcion) {
                            case 1: sesionesCtrl.mostrar(); break;
                            case 2: sociosCtrl.mostrarMenu(); break;
                            case 0: cerrarSesion = true; break;
                            default: System.out.println("❌ Opción no válida.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Error de formato.");
                    }
                }

                // --- MENÚ PARA SOCIOS ---
                else if (usuarioActual instanceof Socio) {
                    System.out.println("1. Mis Reservas (Módulo en construcción)");
                    System.out.println("0. Cerrar Sesión");
                    System.out.print("Selección: ");

                    try {
                        int opcion = Integer.parseInt(teclado.nextLine().trim());
                        if (opcion == 0) cerrarSesion = true;
                        else System.out.println("⚠️ Próximamente.");
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Error de formato.");
                    }
                }
            } // Fin del while interno (Cerrar sesión)

            System.out.println("Sesión finalizada. Volviendo a la pantalla de Login...");
        } // Fin del while externo
    }
}