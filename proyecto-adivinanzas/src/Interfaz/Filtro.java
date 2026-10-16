package Interfaz;

import Things.Personaje;
import java.util.List;

/**
 * Un filtro recibe la lista de candidatos actual y devuelve una lista nueva
 * con los que sobreviven. Nunca modifica la lista original.
 */
public interface Filtro {
    List<Personaje> aplicar(List<Personaje> candidatos);
}
