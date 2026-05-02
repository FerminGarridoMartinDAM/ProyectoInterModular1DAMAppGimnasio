package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.EstadoUsuario;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Socio extends Usuario {

    private LocalDate fechaAlta;


    // Constructor completo con los datos del Padre (Usuario) y del Hijo (Socio), hay que hacerlo manual ,
    // porque lombok lo hace con @superBuilder y crear luego objetos era mas dificil.
    public Socio(int idUsuario, EstadoUsuario estado, String nombre, String apellido,String email, String password, String telefono, LocalDate fechaAlta) {
        super(idUsuario, estado, nombre, apellido, email, password, telefono);
        this.fechaAlta = fechaAlta;
    }

    @Override
    public String toString() {
        return super.toString() + " | [Socio -> Alta: " + fechaAlta + "]";
    }

    public int getIidSocio() {  //Getter para puentear el IdEntrenador que me resultaba mas facil visualmente.
        return super.getIdUsuario();
    }

}