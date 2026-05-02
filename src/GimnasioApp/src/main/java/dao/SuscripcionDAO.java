package dao;

import model.Suscripcion;
import java.util.List;

public interface SuscripcionDAO {
    int insert(Suscripcion suscripcion);
    List<Suscripcion> selectAll();
    Suscripcion selectById(int id);
    int update(Suscripcion suscripcion);
    int delete(int id);
}