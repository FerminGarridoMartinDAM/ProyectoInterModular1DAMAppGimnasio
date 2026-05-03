package dao;

import model.Reserva;
import java.util.List;

public interface ReservaDAO {
    int insert(Reserva reserva);
    List<Reserva> selectAll();
    int update(Reserva reserva);
    // OJO: Necesitamos ambos IDs para saber exactamente qué reserva borrar
    int delete(int idSocio, int idSesion);
    Reserva selectById(int idSocio, int idSesion);
}