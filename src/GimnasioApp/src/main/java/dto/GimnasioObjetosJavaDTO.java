package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.*;

import java.util.ArrayList;
import java.util.List;

/* ========================================================================================
 * GUÍA DE ESTUDIO: DTO INTERNO (EL CONTENEDOR DE OBJETOS VIVOS)
 * ========================================================================================
 * A diferencia de MapasJsonDTO (que habla con el archivo de texto),
 * esta clase sirve como la "Maleta Interna" de nuestro programa Java.
 *
 * ¿POR QUÉ LO NECESITAMOS?
 * Cuando el Gestor lee el JSON y reconstruye todos los datos, termina con 6 listas
 * llenas de objetos vivos (List<Usuario>, List<Clase>, etc.).
 * En lugar de hacer que nuestro método devuelva las 6 listas por separado (lo cual
 * es imposible en Java sin hacer trucos raros), metemos las 6 listas en este DTO.
 *
 * EL FLUJO ES ASÍ:
 * 1. GestorBackup lee MapasJsonDTO (Mapas de texto).
 * 2. Transforma esos Mapas en objetos reales (new Socio(), new Clase()).
 * 3. Guarda esos objetos aquí, en ObjetosJavaDTO.
 * 4. El Gestor le entrega este DTO completo al BackupController.
 * 5. El BackupController abre el DTO y le manda cada lista a su DAO correspondiente
 *    para que haga los INSERTs en MySQL.
 *
 * EN RESUMEN: Es un simple vehículo de transporte para mover datos listos para
 * usar entre el Gestor y el Controlador.
 * ======================================================================================== */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GimnasioObjetosJavaDTO {

    // 1. Lista de usuarios reales (Socios, Entrenadores, Secretarios ya polimorfizados)
    private List<Usuario> usuarios = new ArrayList<>();

    // 2. Lista de clases instanciadas
    private List<Clase> clases = new ArrayList<>();

    // 3. Lista de sesiones con sus fechas (LocalDateTime) ya operativas
    private List<Sesion> sesiones = new ArrayList<>();

    // 4. Lista de planes listos para la base de datos
    private List<Plan> planes = new ArrayList<>();

    // 5. Lista de suscripciones con sus fechas (LocalDate) ya operativas
    private List<Suscripcion> suscripciones = new ArrayList<>();

    // 6. Lista de reservas
    private List<Reserva> reservas = new ArrayList<>();

}


/*
Y asi es como quedaria se veria esta clase , son clases y objetos java a diferencia del Json Dto que es texto crudo estructurado en diccionarios (Mapas) indexados en una List



ObjetosJavaDTO @EspacioMemoria0x99A
│
├── usuarios (ArrayList)
│   ├── [0] -> Instancia de Clase Socio (@Memoria0x11B)
│   │          - idUsuario: (int) 1
│   │          - nombre: (String) "Juan"
│   │          - fechaAlta: (LocalDate) [Objeto Inteligente con Calendario]
│   │          - metodosDisponibles: getNombre(), pagarCuota(), etc.
│   │
│   └── [1] -> Instancia de Clase Entrenador (@Memoria0x22C)
│              - idUsuario: (int) 2
│              - especialidad: (String) "Musculación"
│              - metodosDisponibles: asignarRutina(), getEspecialidad()
│
└── clases (ArrayList)
    └── [0] -> Instancia de Clase Clase (@Memoria0x33D)
               - nombre: "Spinning Extremo"
               - aforoMax: (int) 20*/