package dao;

import model.Plan;
import model.enums.EstadoPlan;
import java.util.List;

public interface PlanDAO {
    int insert(Plan plan);
    List<Plan> selectAll();
     int update(Plan plan);
    int actualizarEstado(int id, EstadoPlan nuevoEstado);
    int delete(int id);
    Plan selectById(int id);
}