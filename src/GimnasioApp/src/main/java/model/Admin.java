package model;

import model.enums.EstadoUsuario;

/* ========================================================================================
 * GUÍA DE ESTUDIO: EL SUPERUSUARIO (ROOT)
 * ========================================================================================
 * Esta clase hereda de Usuario, pero NO tiene tabla en la base de datos.
 * Solo sirve para instanciar a nuestro "admin" en la memoria RAM y que el
 * MenuController pueda usar el polimorfismo (instanceof Administrador) para
 * darle acceso a todos los menús.
 * ======================================================================================== */
public class Admin extends Usuario {

    public Admin() {
        super();
        this.setNombre("admin");
        this.setApellido("admin");
        this.setEmail("admin");
        this.setEstado(EstadoUsuario.ACTIVO);
    }

}