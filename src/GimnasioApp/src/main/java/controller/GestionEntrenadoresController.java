package controller;

import dao.EntrenadorDAO;
import model.Entrenador;
import model.Admin;
import model.Usuario;
import model.enums.EstadoUsuario;

import java.util.List;
import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR PARA GESTIONAR A LOS ENTRENADORES
 * ========================================================================================
 * OBJETIVO: Gestionar el ciclo de vida de los Entrenadores.
 *
 *    Como Entrenador hereda de Usuario (igual que Socio),
 *    el controlador es estructuralmente idéntico al de Socios. Mantenemos las 3 capas:
 *    1. Alta (Insertando Padre + Hijo).
 *    2. Baja Lógica (Cambiando estado a INACTIVO sin romper las sesiones futuras).
 *    3. Borrado Físico (Aduana visual y lógica para el Admin).
 *
 * Al hacer este  controlador he visto que es casi identico  al de gestion de socios por lo que seria interesante crear una clase padre,
 * o bien gestion usuario o algo parecido, tendria que estudiarlo. No se si me va a dar tiempo a implementarla pero lo dejo en pendiente.
 *
 * ========================================================================================
 */
public class GestionEntrenadoresController {

    private EntrenadorDAO entrenadorDAO;
    private Scanner teclado;
    private Usuario usuarioActual; // El guarda de seguridad

    public GestionEntrenadoresController(EntrenadorDAO entrenadorDAO, Scanner teclado, Usuario usuarioActual) {
        this.entrenadorDAO = entrenadorDAO;
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }



        public void mostrar() {
            int opcionMenu = -1;

            do {
                System.out.println("\n--- GESTIÓN DE ENTRENADORES ---");
                System.out.println("1. Mostrar lista de todos los entrenadores");
                System.out.println("2. Alta de nuevo entrenador");
                System.out.println("3. Dar de baja a un entrenador (Desactivar)");

                // Renderizado condicional por roles
                if (usuarioActual instanceof Admin) {
                    System.out.println("4. Borrado permanente de un entrenador");
                }

                System.out.println("0. Volver al menú anterior");
                System.out.print("Selección: ");

                try {
                    opcionMenu = Integer.parseInt(teclado.nextLine().trim());

                    switch (opcionMenu) {
                        case 1:
                            mostrarTodosLosEntrenadores();
                            break;
                        case 2:
                            altaEntrenador();
                            break;
                        case 3:
                            bajaLogicaEntrenador();
                            break;
                        case 4:
                            // Defensa en profundidad
                            if (usuarioActual instanceof Admin) {
                                borradoPermanenteEntrenador();
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
        private void mostrarTodosLosEntrenadores() {
            System.out.println("\n--- LISTADO DE ENTRENADORES ---");

            // 1. Traemos los entrenadores del DAO
            List<Entrenador> listaEntrenadores = entrenadorDAO.selectAll();

            // 2. Comprobamos si la lista está vacía
            if (listaEntrenadores.isEmpty()) {
                System.out.println(" No hay ningún entrenador registrado en la base de datos en este momento.");
            } else {
                // 3. Recorremos la lista y la imprimimos de forma limpia
                // Ajustamos las columnas para mostrar la Especialidad
                System.out.println("ID  | ESTADO   | NOMBRE Y APELLIDO | EMAIL | ESPECIALIDAD");
                System.out.println("------------------------------------------------------------------");

                for (Entrenador entrenador : listaEntrenadores) {
                    System.out.printf("%-3d | %-8s | %s %s | %s | %s\n",
                            entrenador.getIdUsuario(),
                            entrenador.getEstado(),
                            entrenador.getNombre(),
                            entrenador.getApellido(),
                            entrenador.getEmail(),
                            entrenador.getEspecialidad());
                }
                System.out.println("------------------------------------------------------------------");
            }
        }

    // --- OPCIÓN 1: ALTA ---
    private void altaEntrenador() {
        System.out.println("\n--- ALTA DE NUEVO ENTRENADOR ---");
        Entrenador nuevoEntrenador = new Entrenador();

        // 1. Datos del Padre (Usuario)
        System.out.print("Nombre: ");
        nuevoEntrenador.setNombre(teclado.nextLine().trim());

        System.out.print("Apellido: ");
        nuevoEntrenador.setApellido(teclado.nextLine().trim());

        System.out.print("Email: ");
        nuevoEntrenador.setEmail(teclado.nextLine().trim());

        System.out.print("Contraseña temporal: ");
        nuevoEntrenador.setPassword(teclado.nextLine().trim());

        System.out.print("Teléfono: ");
        nuevoEntrenador.setTelefono(teclado.nextLine().trim());

        // El trabajador nace activo por defecto
        nuevoEntrenador.setEstado(EstadoUsuario.ACTIVO);

        // 2. Datos del Hijo (Entrenador)
        System.out.print("Especialidad principal (ej. Musculación, Yoga, Crossfit): ");
        nuevoEntrenador.setEspecialidad(teclado.nextLine().trim());

        // 3. Enviamos al DAO
        int resultado = entrenadorDAO.insert(nuevoEntrenador);

        if (resultado > 0) {
            System.out.println("✅ Entrenador registrado con éxito en la plantilla.");
        } else {
            System.out.println("❌ Hubo un problema al registrar al entrenador en la base de datos.");
        }
    }

    // --- OPCIÓN 2: BAJA LÓGICA ---
    private void bajaLogicaEntrenador() {
        System.out.println("\n--- BAJA DE ENTRENADOR (DESACTIVACIÓN) ---");
        System.out.print("Introduce el ID del entrenador a dar de baja: ");

        try {
            int idEntrenador = Integer.parseInt(teclado.nextLine().trim());

            // Usamos el maravilloso selectById que hemos estandarizado
            Entrenador entrenadorABorrar = entrenadorDAO.selectById(idEntrenador);

            if (entrenadorABorrar != null) {
                if (entrenadorABorrar.getEstado() == EstadoUsuario.INACTIVO) {
                    System.out.println("⚠️ Este entrenador ya constaba como inactivo.");
                } else {
                    entrenadorABorrar.setEstado(EstadoUsuario.INACTIVO);

                    entrenadorDAO.update(entrenadorABorrar);
                    System.out.println("✅ El entrenador ha sido desactivado correctamente. Sus sesiones pasadas se conservan.");
                }
            } else {
                System.out.println("❌ No se encontró ningún entrenador con ese ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Formato de ID incorrecto.");
        }
    }

    // --- OPCIÓN 3: BORRADO FÍSICO (HARD DELETE) ---
    private void borradoPermanenteEntrenador() {
        System.out.println("\n--- ☢️ BORRADO PERMANENTE DE ENTRENADOR ☢️ ---");

        // DEFENSA EN PROFUNDIDAD: La última frontera lógica (Evitar Fallo Silencioso)
        if (usuarioActual instanceof Admin) {

            System.out.print("Introduce el ID del entrenador a ELIMINAR del sistema: ");
            try {
                int idEntrenador = Integer.parseInt(teclado.nextLine().trim());

                System.out.print("⚠️ ¿Estás completamente seguro? Esta acción borrará al usuario y podría fallar si tiene sesiones asignadas. (S/N): ");
                String confirmacion = teclado.nextLine().trim().toUpperCase();

                if (confirmacion.equals("S")) {
                    int resultado = entrenadorDAO.delete(idEntrenador);

                    if (resultado > 0) {
                        System.out.println("✅ El entrenador ha sido borrado físicamente de la base de datos.");
                    } else {
                        System.out.println("❌ No se pudo borrar al entrenador. Asegúrate de que no tenga sesiones asignadas en la base de datos.");
                    }
                } else {
                    System.out.println("Operación cancelada.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Formato de ID incorrecto.");
            }

        } else {
            System.out.println("⛔ ACCESO DENEGADO: Intento de borrado físico bloqueado. Se requiere rol Admin.");
        }
    }
}