

import controller.MenuPrincipalController;
import dao.*;
import utils.CargadorDatosIniciales;
import utils.CargarDatosInicialesSQL;
import utils.GestorBackup;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        // 1. INICIALIZACIÓN DE RECURSOS BÁSICOS
        Scanner teclado = new Scanner(System.in);

        // Se instancia el dao y se implemente el DAOPostgres, por si cambiamos de lenguaje solo le cambiamos la implementacion.
        UsuarioDAO usuarioDAO = new UsuarioDAOPostgresImpl();
        SocioDAO socioDAO = new SocioDAOPostgresImpl();
        EntrenadorDAO entrenadorDAO = new EntrenadorDAOPostgresImpl();
        SecretarioDAO secretarioDAO = new SecretarioDAOPostgresImpl();
        PlanDAO planDAO = new PlanDAOPostgresImpl();
        SuscripcionDAO suscripcionDAO = new SuscripcionDAOPostgresImpl();
        SesionDAO sesionDAO = new SesionDAOPostgresImpl();
        ClaseDAO claseDAO = new ClaseDAOPostgresImpl();
        ReservaDAO reservaDAO = new ReservaDAOPostgresImpl();

        // VACIADO DE BASE DE DATOS!  Para el programa demo hago el vaciado de base de datos pero para el uso real del programa quitariamos o comentariamos estas lineas.
        GestorBackup gestorBackup = new GestorBackup();
        gestorBackup.vaciarBaseDatos();


        // 2. PRECARGA DE DATOS
        // Tu código con error:
        CargarDatosInicialesSQL.cargarTodo();

        // 3. CREAMOS EL CONTROLADOR PRINCIPAL Y LE PASAMOS TODO
        MenuPrincipalController aplicacion = new MenuPrincipalController(
                usuarioDAO, socioDAO, entrenadorDAO, secretarioDAO,
                planDAO, suscripcionDAO, sesionDAO, claseDAO, reservaDAO, teclado
        );

        // 4. ENCENDEMOS EL MOTOR
        aplicacion.iniciar();

        // 5. APAGADO SEGURO
        teclado.close();
    }
}