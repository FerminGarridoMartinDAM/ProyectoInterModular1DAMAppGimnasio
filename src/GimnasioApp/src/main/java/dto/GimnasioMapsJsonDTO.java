package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* ========================================================================================
 * GUÍA DE ESTUDIO: DTO GLOBAL (DATA TRANSFER OBJECT / EL ROOT)
 * ========================================================================================
 * Esta clase sirve como el "Contenedor Raíz" (Root) para nuestro backup.
 * Su única función es empaquetar todas las tablas en un solo objeto para que Gson
 * pueda generar un único archivo .json con toda la información del gimnasio.
 * * Crear esta clase (EstructuraJsonDTO) cumple exactamente la misma función
 * que si creáramos una variable gigantesca al principio de nuestro Gestor, algo así:
 *
 * Map<String, List<Map<String, Object>>> megaContenedor = new HashMap<>();
 * megaContenedor.put("tablaUsuarios", new ArrayList<>());
 * megaContenedor.put("tablaClases", new ArrayList<>());
 * megaContenedor.put("tablaSesiones", new ArrayList<>());
 *  ... y así con las 6 listas de la base de datos.
 *
 *
 * Hemos creado esta clase "Contenedor" por una razón principal:
 *  1. LA AMNESIA DE LOS PICOS < >: Java tiene un problema llamado "Type Erasure".
 * Cuando ejecutas el programa, Java borra lo que hay dentro de los picos < > para
 * ahorrar memoria. Si intentamos que Gson lea una List<Map<...>> suelta, Gson no
 * sabe qué hay dentro porque Java le ha borrado la etiqueta. Esta clase DTO
 * "ancla" los mapas como atributos fijos, impidiendo que Java borre la etiqueta
 * y permitiendo que Gson los lea sin volverse loco. Si no habia que crear una variable type e inicializarla typeToken... un lio.
 *
 *
 * 2. EL TRUCO PARA EL LocalDate:
 * Como Gson no sabe leer objetos LocalDate modernos.
 * Al convertir todo a Mapas en el Gestor, pasamos
 * la fecha a un String normal que Gson sí entiende.
 *
 * 3. ¿QUÉ PASARÍA SI NO HICIÉRAMOS ESTO?:
 * Si no usáramos este sistema de DTO + Mapas, tendríamos que crear "TypeAdapters" complejos.
 * Eso significa que tendríamos que haber mantenido y usado clases extra de 50 líneas,
 * como nuestro TraductorFechas o el FormateadorLocalDate, solo para enseñarle a Gson a leer
 * la fecha, o crear más traductores para explicarle qué es un Socio y qué un Entrenador
 * (Polimorfismo).
 * -> AHORA: Simplemente es hacer un .toString() al guardar en el Mapa, y un LocalDate.parse()
 * al leer, ahorrándonos todo ese código extra.
 *
 * EN RESUMEN: Este DTO es el "puente" que hace que el backup sea directo y nos permite
 * borrar todas esas clases extra de traductores y tokens.
 *
 *

 *
 *
 * ======================================================================================== */



@Data
@NoArgsConstructor
@AllArgsConstructor
public class GimnasioMapsJsonDTO {


    // 1. Agrupa a todos los usuarios (Socios, Entrenadores, Secretarios) usando "tipo_rol"
    private List<Map<String, Object>> tablaUsuarios = new ArrayList<>();
    // 2. Catálogo de clases genéricas (ej. Spinning, Yoga)
    private List<Map<String, Object>> tablaClases = new ArrayList<>();
    // 3. Eventos concretos en el calendario (Las clases programadas con un profesor en una sala)
    private List<Map<String, Object>> tablaSesiones = new ArrayList<>();
    // 4. Catálogo de tarifas del gimnasio (ej. Plan Básico, Plan VIP)
    private List<Map<String, Object>> tablaPlanes = new ArrayList<>();
    // 5. Historial de quién ha comprado qué plan y cuándo caduca
    private List<Map<String, Object>> tablaSuscripciones = new ArrayList<>();
    // 6. Registro de qué socios están apuntados a qué sesiones concretas
    private List<Map<String, Object>> tablaReservas = new ArrayList<>();

}


/*
Asi quedaria une vez el Gson lo lea.
el JSON es solo texto y no sabe qué es un "Usuario" o una "Clase". por eso se guardan maps

{
  "tablaUsuarios": [
    {
      "id_usuario": 1,
      "nombre": "Juan",
      "apellido": "Pérez",
      "email": "juan@email.com",
      "estado": "ACTIVO",
      "tipo_rol": "SOCIO",
      "fecha_alta": "2026-03-15"
    },
    {
      "id_usuario": 2,
      "nombre": "Marta",
      "especialidad": "Musculación",
      "tipo_rol": "ENTRENADOR"
    }
  ],
  "tablaClases": [
    {
      "id_clase": 101,
      "nombre": "Spinning Extremo",
      "aforo_max": 20
    }
  ]
}*/