package model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import model.enums.EstadoUsuario;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Usuario {


    private int idUsuario;
    private EstadoUsuario estado;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;

    @Override
    public String toString() {
        return "ID: " + idUsuario + " | Nombre: " + nombre + " " + apellido + " | Email: " + email;
    }
}