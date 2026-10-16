package FunctionalThings;

import Things.Personaje;
import java.util.Comparator;

/**
 * Los criterios de comparacion que usa el juego.
 * Se separan del algoritmo: el mismo merge sort sirve para cualquier criterio.
 */
public class CriteriosOrden {

    /** Varones primero, mujeres despues. Es el orden inicial que pide el enunciado. */
    public static class PorGenero implements Comparator<Personaje> {
        @Override
        public int compare(Personaje a, Personaje b) {
            // Boolean.compare(true, false) da 1, asi que se invierten los argumentos
            // para que los varones (true) queden adelante.
            return Boolean.compare(b.esMasculino(), a.esMasculino());
        }
    }

    /** Orden ascendente por id. */
    public static class PorId implements Comparator<Personaje> {
        @Override
        public int compare(Personaje a, Personaje b) {
            return Integer.compare(a.getId(), b.getId());
        }
    }

    public static final Comparator<Personaje> POR_GENERO = new PorGenero();
    public static final Comparator<Personaje> POR_ID = new PorId();
}
