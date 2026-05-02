/*
package controller.descartesEstudio;

import model.Usuario;
import model.Admin;
import model.enums.EstadoUsuario;

import java.util.Scanner;


// ESTA ERA UNA DE LAS IDEAS PARA CREAR LA CLASE PADRE DE LOS CONTROLLER DE USUARIOS , PERO NO ME HA CONVENCIDO MUCHO . LA DEJO PARA ESTUDIO.
// el segundo metodo de pedir datos comunes si es interesante , pero no lo voy a meter por no liar mucho el codigo.
// Y voy a añadir otra clase padre que comentare alli.


*/
/* ========================================================================================
 * Clase Padre de los controller de Usuarios.
 * ========================================================================================
 * Esta clase agrupa el código repetitivo:
 * 1. El blindaje del menú (try-catch) para que no explote si meten letras.
 * 2. Las preguntas de consola comunes a todos los usuarios.
 * ========================================================================================
 *//*

public class GestionUsuarioGeneralController {

    protected Scanner teclado;
    protected Usuario usuarioActual;

    public GestionUsuarioGeneralController(Scanner teclado, Usuario usuarioActual) {
        this.teclado = teclado;
        this.usuarioActual = usuarioActual;
    }

    // ====================================================================================
    // HERRAMIENTA 1: MENÚ PROTEGIDO
    // Devuelve el número que ha elegido el usuario (o -1 si se equivoca y mete letras)
    // Le decimos el tipo de usuario que es: Sociio , entrenador, secretario.
    // ====================================================================================
    protected int mostrarMenu(String tipoDeUsuario) {
        System.out.println("\n--- GESTIÓN DE " + tipoDeUsuario.toUpperCase() + " ---");
        System.out.println("1. Mostrar lista de todos "+ tipoDeUsuario.toLowerCase());
        System.out.println("2. Alta de nuevo "+tipoDeUsuario);
        System.out.println("3. Dar de baja (Desactivar) "+ tipoDeUsuario.toLowerCase());

        if (usuarioActual instanceof Admin) {
            System.out.println("4. Borrado permanente de "+ tipoDeUsuario.toLowerCase());
        }

        System.out.println("0. Volver al menú anterior");
        System.out.print("Selección: ");

        try {
            return Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Introduce un número válido.");
            return -1; // Devolvemos -1 para que el switch del hijo lo mande al 'default'
        }
    }

    // ====================================================================================
    // HERRAMIENTA 2: PEDIR DATOS COMUNES
    // ====================================================================================
    protected void pedirDatosComunesUsuario(Usuario usuarioVacio) {
        System.out.print("Nombre: ");
        usuarioVacio.setNombre(teclado.nextLine().trim());

        System.out.print("Apellido: ");
        usuarioVacio.setApellido(teclado.nextLine().trim());

        System.out.print("Email: ");
        usuarioVacio.setEmail(teclado.nextLine().trim());

        System.out.print("Contraseña temporal: ");
        usuarioVacio.setPassword(teclado.nextLine().trim());

        System.out.print("Teléfono: ");
        usuarioVacio.setTelefono(teclado.nextLine().trim());

        usuarioVacio.setEstado(EstadoUsuario.ACTIVO);
    }
}*/
