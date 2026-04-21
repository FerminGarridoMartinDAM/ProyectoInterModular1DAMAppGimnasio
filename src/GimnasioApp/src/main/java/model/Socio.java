package model;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Socio extends Usuario {

    private LocalDate fechaAlta;

    @Override
    public String toString() {
        return super.toString() + " | [Socio -> Alta: " + fechaAlta + "]";
    }
}