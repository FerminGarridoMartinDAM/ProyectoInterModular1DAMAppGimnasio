package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.EstadoUsuario;

@Getter
@Setter
@NoArgsConstructor
public class Entrenador extends Usuario {

    private String especialidad;

    // Constructor completo que une los datos del Padre (Usuario) y del Hijo (Entrenador)ç

    public Entrenador(int idUsuario, EstadoUsuario estado, String nombre, String apellido, String email, String password, String telefono, String especialidad) {
        super(idUsuario, estado, nombre, apellido, email, password, telefono);
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        // Al heredar, usamos los getters del padre para acceder al nombre, id, etc.
        return "Entrenador [" + getIdUsuario() + "] Nombre: " + getNombre() + " | Especialidad: " + especialidad;
    }
}