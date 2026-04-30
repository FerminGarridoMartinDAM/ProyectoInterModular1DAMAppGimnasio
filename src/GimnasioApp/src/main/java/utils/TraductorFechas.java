package utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;




// NO LA VAMOS A USAR PORQUE AL FINAL USO EL DTO.


/* ========================================================================================
 * GUÍA DE ESTUDIO: EL INTERMEDIARIO (TYPE ADAPTER)
 * ========================================================================================
 * Gson no entiende el idioma de 'LocalDate', así que cuando se encuentra con uno,
 * nos lo pasa a nosotros. Nosotros lo pasamos a texto simple (String) y se lo devolvemos.
 * Al leer el archivo de texto, hacemos exactamente la operación inversa.
 * ========================================================================================
 */


// Esta clase no se va a usar pero la he dejado porque es muy interesante. En su lugar voy a usar un format como dijo Borja que es la clase FormateadorFechar
public class TraductorFechas implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    // GUARDAR: De la memoria RAM de Java al archivo de texto (Serializar)
    @Override
    public JsonElement serialize(LocalDate fechaJava, Type tipo, JsonSerializationContext contexto) {

        // Transformamos la fecha compleja en un texto simple y entendible
        String fechaEnTexto = fechaJava.toString();

        // Envolvemos el texto en una caja primitiva que Gson sabe manejar a la perfección
        return new JsonPrimitive(fechaEnTexto);
    }

    // LEER: Del archivo de texto a la memoria RAM de Java (Deserializar)
    @Override
    public LocalDate deserialize(JsonElement json, Type tipo, JsonDeserializationContext contexto) {

        // Gson nos entrega el trozo de texto exacto que ha leído del disco duro
        String textoDelArchivo = json.getAsString();

        // Usamos la herramienta matemática de Java para reconstruir la fecha original
        return LocalDate.parse(textoDelArchivo);
    }
}

/*   Manual de Estudio: Anatomía del Traductor Gson
1. Las Firmas del Contrato: JsonSerializer<LocalDate> y JsonDeserializer<LocalDate>
En Java, cuando ves la palabra implements seguida de unos nombres, significa que estás firmando un Contrato de Trabajo (una Interfaz).

Gson te dice: "Si quieres trabajar para mí traduciendo fechas, tienes que firmar estos dos contratos".

JsonSerializer (El Contrato de Escritura): Te obliga a saber cómo pasar de un objeto Java a un texto JSON para guardarlo en el disco duro.

JsonDeserializer (El Contrato de Lectura): Te obliga a saber cómo coger un texto del disco duro y reconstruir el objeto Java.

<LocalDate> (La Especialidad): Esos símbolos de mayor y menor se llaman Genéricos. Es como poner tu especialidad en el currículum. Le estás diciendo a Gson: "Ojo, yo solo firmo el contrato para traducir objetos de tipo LocalDate. Si me pasas un Entrenador, no sé qué hacer con él".

2. El método de Guardado: serialize(...)
Como has firmado el contrato JsonSerializer, estás obligado a escribir este método exacto. Es la máquina que transforma la fecha en texto.

El resultado que devuelves (public JsonElement):
Gson no entiende de simples Strings. Él trabaja con sus propias piezas de Lego llamadas JsonElement. Tu trabajo es coger el texto de la fecha, meterlo en una de sus piezas de Lego (usando new JsonPrimitive(texto)), y devolvérselo para que él lo encaje en el archivo final.

Los parámetros (Lo que Gson te entrega por la cinta transportadora):

LocalDate fechaJava: La Materia Prima. Es la fecha exacta del socio que Gson tiene en la mano en ese milisegundo y no sabe cómo guardar. Te la da a ti para que la transformes.

Type tipo: El Plano. Es información técnica sobre el tipo de dato. En el 99% de los casos básicos (Nivel 1), lo ignoramos y no lo usamos para nada.

JsonSerializationContext contexto: La Caja de Herramientas. Imagina que dentro de tu fecha hubiera otro objeto más complejo. Podrías usar esta herramienta para decirle a Gson: "Oye, ayúdame a traducir este trozo que yo no sé". Para fechas simples, también lo ignoramos.

3. El método de Lectura: deserialize(...)
Como has firmado el contrato JsonDeserializer, estás obligado a escribir este método. Es la máquina inversa: lee el disco duro y resucita el objeto en la memoria RAM.

El resultado que devuelves (public LocalDate):
Tu objetivo final. Tienes que devolverle a Gson un objeto fecha real y perfectamente construido, listo para meterlo en la ficha del Socio.

Los parámetros (Lo que Gson te entrega tras leer el archivo):

JsonElement json: El paquete del disco duro. Es la pieza de Lego que Gson acaba de leer del archivo (por ejemplo, la cajita que contiene el texto "2026-04-28"). Tú tienes que abrir la caja con json.getAsString() para sacar el texto.

Type tipo: El Plano. Igual que antes, información técnica que por ahora no nos interesa.

JsonDeserializationContext contexto: La Caja de Herramientas inversa. Igual que antes, herramientas extra de Gson que no necesitamos tocar para una simple fecha.*/