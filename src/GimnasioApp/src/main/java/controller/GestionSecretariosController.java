package controller;

import dao.SecretarioDAO;
import model.Secretario;
import model.Usuario;
import model.Admin;
import model.enums.EstadoUsuario;

import java.util.List;
import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR PARA GESTIONAR A LOS SECRETARIOS
 * ========================================================================================
 * Gestionar el ciclo de vida del personal de recepción (Secretarios).
 *
 * ========================================================================================
 */
public class GestionSecretariosController {

    private SecretarioDAO secretarioDAO;
    private Scanner teclado;
    private Usuario usuarioActual; // El guarda de seguridad

    public GestionSecretariosController(SecretarioDAO secretarioDAO, Scanner teclado, Usuario usuarioActual) {
        this.secretarioDAO = secretarioDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }

    public void mostrarMenu() {
        int opcionMenu = -1;

        do {
            System.out.println("\n--- GESTIÓN DE SECRETARIOS ---");
            System.out.println("1. Mostrar lista de todos los secretarios");
            System.out.println("2. Alta de nuevo secretario");
            System.out.println("3. Dar de baja a un secretario (Desactivar)");

            // Esta opción solo sale si es un admin.
            if (usuarioActual instanceof Admin) {
                System.out.println("4. Borrado permanente de un secretario");
            }

            System.out.println("0. Volver al menú anterior");
            System.out.print("Selección: ");

            try {
                opcionMenu = Integer.parseInt(teclado.nextLine().trim());

                switch (opcionMenu) {
                    case 1:
                        mostrarTodosLosSecretarios();
                        break;
                    case 2:
                        altaSecretario();
                        break;
                    case 3:
                        bajaLogicaSecretario();
                        break;
                    case 4:
                        // Capa de seguridad: Solo si es admin deja ejecutar la opción 4.
                        if (usuarioActual instanceof Admin) {
                            borradoPermanenteSecretario();
                        } else {
                            System.out.println("❌ Error: Opción no válida. Elige una opción válida.");
                        }
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("❌ Error: Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Introduce un número válido.");
            }
        } while (opcionMenu != 0);
    }

    // ====================================================================================
    // MÉTODOS DEL MENÚ
    // ====================================================================================

    // --- OPCIÓN 1: MOSTRAR TODOS ---
    private void mostrarTodosLosSecretarios() {
        System.out.println("\n--- LISTADO DE SECRETARIOS ---");

        // 1. Traemos los secretarios del DAO.
        List<Secretario> listaSecretarios = secretarioDAO.selectAll();

        // 2. Comprobamos si la lista está vacía
        if (listaSecretarios.isEmpty()) {
            System.out.println("ℹ️ No hay ningún secretario registrado en la base de datos en este momento.");
        } else {
            // 3. Recorremos la lista y la imprimimos de forma limpia (Adaptado para la columna TURNO)
            System.out.println("ID  | ESTADO   | NOMBRE Y APELLIDO | EMAIL | TURNO");
            System.out.println("------------------------------------------------------------------");

            for (Secretario secretario : listaSecretarios) {
                System.out.printf("%-3d | %-8s | %s %s | %s | %s\n",
                        secretario.getIdUsuario(),
                        secretario.getEstado(),
                        secretario.getNombre(),
                        secretario.getApellido(),
                        secretario.getEmail(),
                        secretario.getTurno());
            }
            System.out.println("------------------------------------------------------------------");
        }
    }

    // --- OPCIÓN 2: ALTA ---
    private void altaSecretario() {
        System.out.println("\n--- ALTA DE NUEVO SECRETARIO ---");
        Secretario nuevoSecretario = new Secretario();

        // 1. Datos de la tabla madre (Usuario)
        System.out.print("Nombre: ");
        nuevoSecretario.setNombre(teclado.nextLine().trim());

        System.out.print("Apellido: ");
        nuevoSecretario.setApellido(teclado.nextLine().trim());

        System.out.print("Email: ");
        nuevoSecretario.setEmail(teclado.nextLine().trim());

        System.out.print("Contraseña temporal: ");
        nuevoSecretario.setPassword(teclado.nextLine().trim());

        System.out.print("Teléfono: ");
        nuevoSecretario.setTelefono(teclado.nextLine().trim());

        // Por defecto, un secretario recién creado siempre nace activo
        nuevoSecretario.setEstado(EstadoUsuario.ACTIVO);

        // 2. Datos de la tabla hija (Secretario)
        System.out.print("Turno de trabajo (ej. Mañana, Tarde, Noche): ");
        nuevoSecretario.setTurno(teclado.nextLine().trim());

        // 3. Enviamos al DAO
        int resultado = secretarioDAO.insert(nuevoSecretario);

        if (resultado > 0) {
            System.out.println("✅ Secretario registrado con éxito en el sistema.");
        } else {
            System.out.println("❌ Hubo un problema al registrar al secretario en la base de datos.");
        }
    }

    // --- OPCIÓN 3: BAJA LÓGICA (SOFT DELETE) ---
    private void bajaLogicaSecretario() {
        System.out.println("\n--- BAJA DE SECRETARIO (DESACTIVACIÓN) ---");
        System.out.print("Introduce el ID del secretario a dar de baja: ");

        try {
            int idSecretario = Integer.parseInt(teclado.nextLine().trim());

            // Primero, buscamos si el secretario existe
            Secretario secretarioABorrar = secretarioDAO.selectById(idSecretario);

            if (secretarioABorrar != null) {
                if (secretarioABorrar.getEstado() == EstadoUsuario.INACTIVO) {
                    System.out.println("⚠️ Este secretario ya estaba dado de baja previamente.");
                } else {
                    // Cambiamos su estado a INACTIVO
                    secretarioABorrar.setEstado(EstadoUsuario.INACTIVO);

                    // Usamos update() para guardar el cambio de estado en la BD
                    secretarioDAO.update(secretarioABorrar);
                    System.out.println("✅ El secretario ha sido desactivado correctamente (Baja lógica).");
                }
            } else {
                System.out.println("❌ No se encontró ningún secretario con ese ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato de ID incorrecto.");
        }
    }

    // --- OPCIÓN 4: BORRADO FÍSICO (HARD DELETE) ---
    private void borradoPermanenteSecretario() {
        System.out.println("\n--- ☢️ BORRADO PERMANENTE DE SECRETARIO ☢️ ---");

        // ADUANA DE SEGURIDAD
        if (usuarioActual instanceof Admin) {

            System.out.print("Introduce el ID del secretario a ELIMINAR del sistema: ");
            try {
                int idSecretario = Integer.parseInt(teclado.nextLine().trim());

                System.out.print("⚠️ ¿Estás completamente seguro? Esta acción no se puede deshacer. (S/N): ");
                String confirmacion = teclado.nextLine().trim().toUpperCase();

                if (confirmacion.equals("S")) {
                    int resultado = secretarioDAO.delete(idSecretario);

                    if (resultado > 0) {
                        System.out.println("✅ El secretario ha sido borrado físicamente de la base de datos.");
                    } else {
                        System.out.println("❌ No se pudo borrar al secretario (¿Quizás no existe o tiene dependencias?)");
                    }
                } else {
                    System.out.println("Operación de borrado cancelada.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Formato de ID incorrecto.");
            }

        } else {
            // Lanzamiento de excepción de seguridad
            System.out.println("ACCESO DENEGADO: No tienes el nivel de privilegios necesario (Se requiere rol Admin).");
            throw new SecurityException("Intento de acceso no autorizado al borrado físico de la base de datos.");
        }
    }
}