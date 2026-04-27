package dao;

import model.Plan;
import model.enums.EstadoPlan;
import java.util.List;

public interface PlanDAO {
    boolean insert(Plan plan);
    List<Plan> selectAll();
    boolean update(Plan plan);
    boolean actualizarEstado(int id, EstadoPlan nuevoEstado);
    boolean delete(int id);
}