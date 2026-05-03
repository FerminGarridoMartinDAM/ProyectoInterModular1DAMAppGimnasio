package controller;

import dao.UsuarioDAO;
import model.Admin;
import model.Usuario;
import model.enums.EstadoUsuario;

import java.util.Scanner;

/* ========================================================================================
 * CONTROLADOR DE LOGIN (AUTENTICACIÓN)
 * ========================================================================================
 * OBJETIVO: Gestionar el acceso al sistema validando las credenciales del usuario.
 * ========================================================================================
 */
public class LoginController {

    private UsuarioDAO usuarioDAO;
    private Scanner teclado;

    public LoginController(UsuarioDAO usuarioDAO, Scanner teclado) {
        this.usuarioDAO = usuarioDAO;
        this.teclado = teclado;
    }

    /*
     * Muestra la pantalla de login y solicita credenciales.
     * @return El objeto Usuario autenticado, o null si falla.
     */
    public Usuario iniciarSesion() {
        System.out.println("\n========================================");
        System.out.println("      SISTEMA DE GESTIÓN DE GIMNASIO    ");
        System.out.println("========================================");

        System.out.print("Email: ");
        String email = teclado.nextLine().trim();

        System.out.print("Contraseña: ");
        String password = teclado.nextLine().trim();

        //AQUI CREAMOS EL SUPERADMIN cuando logueamos con admin admin

        if (email.equals("admin") && password.equals("admin")) {
            Admin superAdmin = new Admin();
            superAdmin.setNombre("Super");
            superAdmin.setApellido("Administrador");
            superAdmin.setEmail("admin");
            // Le damos estado ACTIVO para que pase los filtros de seguridad de los menús
            superAdmin.setEstado(EstadoUsuario.ACTIVO);

            System.out.println("✅ ¡Bienvenido/a al sistema, MODO DIOS (Admin) activado!");
            return superAdmin;
        }





        // Validación contra la base de datos
        Usuario usuarioAutenticado = usuarioDAO.login(email, password);

        if (usuarioAutenticado != null) {

            // Verificación de estado: No se permite el acceso a usuarios dados de baja
            if (usuarioAutenticado.getEstado() == EstadoUsuario.INACTIVO) {
                System.out.println("❌ Acceso denegado: La cuenta se encuentra inactiva. Contacte con administración.");
                return null;
            }

            System.out.println("✅ ¡Bienvenido/a al sistema, " + usuarioAutenticado.getNombre() + "!");
            return usuarioAutenticado;

        } else {
            System.out.println("❌ Credenciales incorrectas. Verifique su email y contraseña.");
            return null;
        }
    }
}