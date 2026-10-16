package FunctionalThings;

import Interfaz.Ordenador;
import Things.Personaje;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * QUICK SORT implementado a mano. Tambien es DIVIDE & CONQUER, pero al reves
 * que el merge sort:
 *   DIVIDIR    -> elegir un pivote y PARTICIONAR la lista de modo que a la
 *                 izquierda queden los menores y a la derecha los mayores.
 *                 El pivote ya queda en su posicion definitiva.
 *   CONQUISTAR -> ordenar recursivamente cada lado.
 *   COMBINAR   -> no hace falta: la particion ya dejo todo en orden.
 *
 * En el merge sort el trabajo pesado esta en combinar; en el quick sort esta
 * en dividir. Por eso el quick sort ordena EN EL LUGAR (O(log n) de memoria por
 * la pila de recursion, sin arreglo auxiliar).
 *
 * Complejidad: O(n log n) en promedio, pero O(n^2) en el peor caso, cuando el
 * pivote parte la lista 1 / n-1 en cada nivel.
 *
 * PIVOTE ALEATORIO: es una decision deliberada. En este programa el quick sort
 * se usa para ordenar por id listas que muchas veces YA vienen casi ordenadas,
 * y ese es justamente el peor caso del quick sort con pivote fijo (primero o
 * ultimo elemento): degeneraria a O(n^2). Al elegir el pivote al azar, el peor
 * caso deja de depender de como vienen los datos y pasa a ser improbable.
 *
 * NO es estable: la particion intercambia elementos lejanos entre si. Por eso
 * el orden inicial por genero, donde la estabilidad importa, se hace con merge
 * sort y no con este.
 */
public class QuickSortPersonajes implements Ordenador {

    private int comparaciones;
    private final Random random = new Random();

    @Override
    public String getNombre() { return "Quick Sort"; }

    @Override
    public int getComparaciones() { return comparaciones; }

    @Override
    public void ordenar(List<Personaje> lista, Comparator<Personaje> criterio) {
        comparaciones = 0;
        if (lista == null || lista.size() < 2) return;
        quickSort(lista, 0, lista.size() - 1, criterio);
    }

    private void quickSort(List<Personaje> lista, int izquierda, int derecha,
                           Comparator<Personaje> criterio) {

        // CASO BASE: tramo de 0 o 1 elemento.
        if (izquierda >= derecha) return;

        // DIVIDIR
        int posicionPivote = particionar(lista, izquierda, derecha, criterio);

        // CONQUISTAR (el pivote ya quedo en su lugar definitivo, se excluye)
        quickSort(lista, izquierda, posicionPivote - 1, criterio);
        quickSort(lista, posicionPivote + 1, derecha, criterio);
    }

    /** Particion de Lomuto con pivote elegido al azar. */
    private int particionar(List<Personaje> lista, int izquierda, int derecha,
                            Comparator<Personaje> criterio) {

        // Pivote aleatorio: se sortea una posicion y se manda al final.
        int azar = izquierda + random.nextInt(derecha - izquierda + 1);
        Collections.swap(lista, azar, derecha);

        Personaje pivote = lista.get(derecha);
        int i = izquierda - 1;   // frontera de los "menores o iguales"

        for (int j = izquierda; j < derecha; j++) {
            comparaciones++;
            if (criterio.compare(lista.get(j), pivote) <= 0) {
                i++;
                Collections.swap(lista, i, j);
            }
        }

        // El pivote se coloca justo despues de los menores: su lugar definitivo.
        Collections.swap(lista, i + 1, derecha);
        return i + 1;
    }
}
