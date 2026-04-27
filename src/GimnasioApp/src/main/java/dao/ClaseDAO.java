package dao;

import model.Clase;
import model.enums.EstadoClase;
import java.util.List;

public interface ClaseDAO {

    int insert(Clase clase);
    List<Clase> selectAll();
    int update(Clase clase);
    int actualizarEstado(int id, EstadoClase nuevoEstado);
    int delete(int id);
}