package Interfaz;

import Things.Personaje;
import java.util.Comparator;
import java.util.List;

/**
 * Abstrae el algoritmo de ordenamiento (otra vez patron Strategy).
 * El GestorPersonaje pide "ordename esta lista con este criterio" y no le
 * importa si atras hay un merge sort o un quick sort.
 */
public interface Ordenador {

    void ordenar(List<Personaje> lista, Comparator<Personaje> criterio);

    /** Nombre del algoritmo, para mostrarlo por pantalla. */
    String getNombre();

    /** Cantidad de comparaciones de la ultima corrida (sirve para comparar algoritmos). */
    int getComparaciones();
}
