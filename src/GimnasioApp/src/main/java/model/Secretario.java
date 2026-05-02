


        package model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import model.enums.EstadoUsuario;

@Getter
@Setter
@NoArgsConstructor
public class Secretario extends Usuario {

    private String turno;



    // Constructor completo que une los datos del Padre (Usuario) y del Hijo (Secretarioo)
    public Secretario(int idUsuario, EstadoUsuario estado, String nombre, String apellido, String email, String password, String telefono, String turno) {
        super(idUsuario, estado, nombre, apellido, email, password, telefono);
        this.turno = turno;
    }

    @Override
    public String toString() {
        return "Secretario [" + getIdUsuario() + "] Nombre: " + getNombre() + " | Turno: " + turno;
    }


    public int getIdSecretario() {  //Getter para puentear el IdEntrenador que me resultaba mas facil visualmente.
        return super.getIdUsuario();
    }
}