/*
package controller;

import dao.SocioDAO;
import model.Socio;
import model.Usuario;
import utils.ValidadorFechas;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class GestionSociosController extends controller.GestionUsuariosControllerBase<Socio> {

    private SocioDAO socioDAO;

    public GestionSociosController(SocioDAO socioDAO, Scanner teclado, Usuario usuarioActual) {
        super(teclado, usuarioActual);
        this.socioDAO = socioDAO;
    }

    // --- CONFIGURACIÓN DE TEXTOS ---
    @Override protected String getNombrePlural() { return "SOCIOS"; }
    @Override protected String getNombreSingular() { return "SOCIO"; }

    // --- CONEXIÓN AL DAO ---
    @Override protected List<Socio> obtenerTodosDelDAO() { return socioDAO.selectAll(); }
    @Override protected Socio buscarPorIdEnDAO(int id) { return socioDAO.selectById(id); }
    @Override protected int insertarEnDAO(Socio entidad) { return socioDAO.insert(entidad); }
    @Override protected int actualizarEnDAO(Socio entidad) { return socioDAO.update(entidad); }
    @Override protected int borrarFisicamenteEnDAO(int id) { return socioDAO.delete(id); }

    // --- LÓGICA ESPECÍFICA DEL SOCIO ---
    @Override
    protected Socio instanciarEntidad() {
        return new Socio(); // Entregamos la caja vacía al Padre
    }

    @Override
    protected void pedirDatosEspecificos(Socio socio) {
        System.out.println();
        LocalDate fechaAlta = ValidadorFechas.pedirSoloFecha(teclado, "Fecha de matriculación");
        socio.setFechaAlta(fechaAlta);
    }

    @Override
    protected void imprimirCabeceraTabla() {
        System.out.println("ID  | ESTADO   | NOMBRE Y APELLIDO | EMAIL | ALTA");
        System.out.println("------------------------------------------------------------------");
    }

    @Override
    protected void imprimirDatosFila(Socio socio) {
        System.out.printf("%-3d | %-8s | %s %s | %s | %s\n",
                socio.getIdUsuario(), socio.getEstado(), socio.getNombre(),
                socio.getApellido(), socio.getEmail(), socio.getFechaAlta());
    }
}*/
