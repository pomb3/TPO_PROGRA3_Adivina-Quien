package FunctionalThings;

import Interfaz.Filtro;
import Things.Personaje;
import java.util.ArrayList;
import java.util.List;

/**
 * Una pregunta de si/no. Guarda los dos filtros posibles:
 * el que se aplica cuando la respuesta es SI y el que se aplica cuando es NO.
 *
 * Esta clase es la pieza clave del divide & conquer: cada pregunta parte
 * el conjunto de candidatos en dos subconjuntos disjuntos y complementarios.
 */
public class Pregunta {

    private final String texto;
    private final Filtro filtroSi;
    private final Filtro filtroNo;

    public Pregunta(String texto, Filtro filtroSi, Filtro filtroNo) {
        this.texto = texto;
        this.filtroSi = filtroSi;
        this.filtroNo = filtroNo;
    }

    public String getTexto() { return texto; }

    /** Devuelve true si ese personaje cumple la condicion de la pregunta. */
    public boolean cumple(Personaje p) {
        List<Personaje> uno = new ArrayList<>();
        uno.add(p);
        return !filtroSi.aplicar(uno).isEmpty();
    }

    /** El filtro que corresponde segun como se haya respondido. */
    public Filtro filtroSegun(boolean respuesta) {
        return respuesta ? filtroSi : filtroNo;
    }

    /** Cuantos candidatos caen del lado del SI. El resto cae del lado del NO. */
    public int contarSi(List<Personaje> candidatos) {
        return filtroSi.aplicar(candidatos).size();
    }

    @Override
    public String toString() { return texto; }
}
