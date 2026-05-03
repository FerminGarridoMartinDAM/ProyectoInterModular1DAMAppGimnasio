package controller;

import dao.*;
import dto.GimnasioObjetosJavaDTO;
import model.*;
import utils.GestorBackup;

import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR DE BACKUP
 ========================================================================================
 * Esta clase se encarga del menu de opciones de backups y del reseto de datos de la base de datos
 */
public class GestionBackupController {

    private UsuarioDAO usuarioDAO;
    private SocioDAO socioDAO;
    private EntrenadorDAO entrenadorDAO;
    private SecretarioDAO secretarioDAO;
    private ClaseDAO claseDAO;
    private SesionDAO sesionDAO;
    private PlanDAO planDAO;
    private SuscripcionDAO suscripcionDAO;
    private ReservaDAO reservaDAO;

    private Scanner teclado;
    private Usuario usuarioActual;

    // Instanciamos nuestra herramienta de lógica pura (La "Aduana")
    private GestorBackup gestorBackup;

    public GestionBackupController(UsuarioDAO usuarioDAO, SocioDAO socioDAO, EntrenadorDAO entrenadorDAO,
                                   SecretarioDAO secretarioDAO, ClaseDAO claseDAO, SesionDAO sesionDAO,
                                   PlanDAO planDAO, SuscripcionDAO suscripcionDAO, ReservaDAO reservaDAO,
                                   Scanner teclado, Usuario usuarioActual) {
        this.usuarioDAO = usuarioDAO;
        this.socioDAO = socioDAO;
        this.entrenadorDAO = entrenadorDAO;
        this.secretarioDAO = secretarioDAO;
        this.claseDAO = claseDAO;
        this.sesionDAO = sesionDAO;
        this.planDAO = planDAO;
        this.suscripcionDAO = suscripcionDAO;
        this.reservaDAO = reservaDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
        this.gestorBackup = new GestorBackup();
    }

    // =========================================================================
    // 1. MENÚ PRINCIPAL DEL CONTROLADOR
    // =========================================================================
    public void mostrarMenu() {

        // BARRERA DE SEGURIDAD ABSOLUTA
        // Aunque por error un controlador llame a este método, si el usuario no es
        // explícitamente un Administrador, la ejecución se corta aquí mismo (return).
        if (!(usuarioActual instanceof Admin)) {
            System.out.println("⛔ ACCESO DENEGADO: Área restringida a Mantenimiento del Sistema.");
            return;
        }

        boolean salir = false;

        while (!salir) {
            System.out.println("\n========================================");
            System.out.println("    HERRAMIENTAS DE SISTEMA (ADMIN)     ");
            System.out.println("========================================");
            System.out.println("1. ☢️ VACIAR BASE DE DATOS (Reset Total)");
            System.out.println("2. Exportar Copia de Seguridad a JSON");
            System.out.println("3. Restaurar Copia de Seguridad desde JSON");
            System.out.println("0. Volver al Panel de Control");
            System.out.print("Selección: ");

            try {
                int opcion = Integer.parseInt(teclado.nextLine().trim());

                switch (opcion) {
                    case 1:
                        resetearBaseDatos();
                        break;
                    case 2:
                        realizarExportacion();
                        break;
                    case 3:
                        realizarRestauracion();
                        break;
                    case 0:
                        salir = true;
                        break;
                    default:
                        System.out.println("❌ Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Introduce un número válido.");
            }
        }
    }

    // =========================================================================
    // 2. MÉTODOS DE ACCIÓN (Lógica de Interfaz)
    // =========================================================================

    private void resetearBaseDatos() {
        System.out.println("\n--- ☢️ VACIADO DE BASE DE DATOS ---");
        System.out.print("⚠️ CUIDADO: Se borrarán TODOS los datos de Postgres. ¿Estás absolutamente seguro? (S/N): ");
        String confirmacion = teclado.nextLine().trim();

        if (confirmacion.equalsIgnoreCase("S")) {
            gestorBackup.vaciarBaseDatos();
        } else {
            System.out.println("✅ Operación de vaciado abortada por seguridad.");
        }
    }

    private void realizarExportacion() {
        System.out.println("\n--- EXPORTACIÓN DE DATOS ---");
        System.out.println("Recopilando datos vivos de la base de datos...");

        // El controlador recolecta todas las listas usando los DAOs y se las lanza al Gestor.
        // El Gestor se encargará de desarmarlos y usar el método privado 'escribirArchivoJsonConGson'.
        gestorBackup.exportarBackupBD(
                usuarioDAO.selectAll(),
                claseDAO.selectAll(),
                sesionDAO.selectAll(),
                planDAO.selectAll(),
                suscripcionDAO.selectAll(),
                reservaDAO.selectAll()
        );
    }

    private void realizarRestauracion() {
        System.out.println("\n--- RESTAURACIÓN DE DATOS ---");
        System.out.print("⚠️ CUIDADO: Esto vaciará la BD actual y cargará los datos del JSON. ¿Seguro? (S/N): ");
        String confirmacion = teclado.nextLine().trim();

        if (confirmacion.equalsIgnoreCase("S")) {

            System.out.println("Leyendo archivo JSON...");

            // 1. El Gestor usa su método privado 'leerArchivoJsonConGson', reconstruye
            // los objetos y nos entrega el DTO lleno de vida.
            GimnasioObjetosJavaDTO dtoVivo = gestorBackup.importarBackup();

            if (dtoVivo != null) {
                // 2. Limpieza total de la BD para que no haya conflictos de IDs
                gestorBackup.vaciarBaseDatos();

                System.out.println("Restaurando datos en PostgreSQL...");

                // 3. INSERCIÓN ESTRATÉGICA. Insertamos por orden de Dependencias
                // Es crucial insertar primero las tablas "Padre" (Usuarios, Planes, Clases)
                // para que cuando insertemos las tablas "Hijo" (Suscripciones, Sesiones, Reservas)
                // las Foreign Keys (Claves Foráneas) encuentren los IDs que necesitan.

                // --- dependiendo del tipo de usuario que sea. ---
                if (dtoVivo.getUsuarios() != null) {
                    for (Usuario u : dtoVivo.getUsuarios()) {
                        if (u instanceof Socio) {
                            socioDAO.insert((Socio) u);
                        } else if (u instanceof Entrenador) {
                            entrenadorDAO.insert((Entrenador) u);
                        } else if (u instanceof Secretario) {
                            secretarioDAO.insert((Secretario) u);
                        }
                    }
                }
                if (dtoVivo.getPlanes() != null) dtoVivo.getPlanes().forEach(p -> planDAO.insert(p));
                if (dtoVivo.getClases() != null) dtoVivo.getClases().forEach(c -> claseDAO.insert(c));

                if (dtoVivo.getSesiones() != null) dtoVivo.getSesiones().forEach(s -> sesionDAO.insert(s));
                if (dtoVivo.getSuscripciones() != null) dtoVivo.getSuscripciones().forEach(sub -> suscripcionDAO.insert(sub));
                if (dtoVivo.getReservas() != null) dtoVivo.getReservas().forEach(r -> reservaDAO.insert(r));

                System.out.println("✅ Restauración completada con éxito. El sistema está listo.");
            } else {
                System.out.println("❌ No se pudo completar la restauración (El archivo JSON no existe o está corrupto).");
            }
        } else {
            System.out.println("✅ Restauración abortada por seguridad.");
        }
    }
}