package FunctionalThings;

import Interfaz.Ordenador;
import Things.Personaje;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * BUBBLE SORT (ordenamiento de burbuja). NO se usa en el flujo del juego:
 * se implementa unicamente como testigo cuadratico para la tabla comparativa
 * de tiempos que pide la documentacion (contraste contra Merge Sort / Quick
 * Sort, que son divide & conquer).
 *
 * En cada pasada recorre la lista comparando pares adyacentes y los
 * intercambia si estan en el orden incorrecto. El elemento mas "pesado"
 * segun el criterio "burbujea" hasta el final en cada pasada.
 *
 * Complejidad: O(n^2) en el caso promedio y en el peor caso (dos bucles
 * anidados). Con bandera de corte anticipado queda O(n) en el mejor caso
 * (lista ya ordenada), que es el unico "ahorro" que tiene frente a otros
 * cuadraticos como seleccion. Es ESTABLE (nunca intercambia elementos iguales).
 */
public class BubbleSortPersonajes implements Ordenador {

    private int comparaciones;

    @Override
    public String getNombre() { return "Bubble Sort"; }

    @Override
    public int getComparaciones() { return comparaciones; }

    @Override
    public void ordenar(List<Personaje> lista, Comparator<Personaje> criterio) {
        comparaciones = 0;
        if (lista == null || lista.size() < 2) return;

        int n = lista.size();
        boolean huboIntercambio;
        for (int i = 0; i < n - 1; i++) {
            huboIntercambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                comparaciones++;
                if (criterio.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    Collections.swap(lista, j, j + 1);
                    huboIntercambio = true;
                }
            }
            if (!huboIntercambio) break; // ya quedo ordenada, corte anticipado
        }
    }
}
