package FunctionalThings;

import Interfaz.Ordenador;
import Things.Personaje;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MERGE SORT implementado a mano.
 *
 * Es el ejemplo canonico de DIVIDE & CONQUER:
 *   DIVIDIR    -> partir la lista al medio en dos mitades.
 *   CONQUISTAR -> ordenar cada mitad recursivamente (caso base: 1 elemento,
 *                 una lista de un solo elemento ya esta ordenada).
 *   COMBINAR   -> fusionar las dos mitades ordenadas recorriendolas en paralelo
 *                 y tomando siempre el menor de los dos frentes.
 *
 * Recurrencia:  T(n) = 2*T(n/2) + O(n)  =>  O(n log n)
 * A diferencia del quick sort, el O(n log n) esta GARANTIZADO: no hay peor caso
 * cuadratico, porque la particion siempre es exactamente por la mitad y no
 * depende de los datos.
 *
 * Es ESTABLE: dos personajes con el mismo genero conservan su orden relativo
 * previo. Esto importa porque el enunciado pide ordenar "unicamente segun el
 * genero"; la estabilidad garantiza que no se introduzca ningun otro criterio
 * de desempate por accidente.
 *
 * Costo en memoria: O(n) por el arreglo auxiliar. Es el precio de la garantia.
 */
public class MergeSortPersonajes implements Ordenador {

    private int comparaciones;

    @Override
    public String getNombre() { return "Merge Sort"; }

    @Override
    public int getComparaciones() { return comparaciones; }

    @Override
    public void ordenar(List<Personaje> lista, Comparator<Personaje> criterio) {
        comparaciones = 0;
        if (lista == null || lista.size() < 2) return;

        List<Personaje> auxiliar = new ArrayList<>(lista);
        mergeSort(lista, auxiliar, 0, lista.size() - 1, criterio);
    }

    private void mergeSort(List<Personaje> lista, List<Personaje> aux,
                           int izquierda, int derecha, Comparator<Personaje> criterio) {

        // CASO BASE: 0 o 1 elemento, ya esta ordenado.
        if (izquierda >= derecha) return;

        // DIVIDIR
        int medio = izquierda + (derecha - izquierda) / 2;

        // CONQUISTAR
        mergeSort(lista, aux, izquierda, medio, criterio);
        mergeSort(lista, aux, medio + 1, derecha, criterio);

        // COMBINAR
        combinar(lista, aux, izquierda, medio, derecha, criterio);
    }

    private void combinar(List<Personaje> lista, List<Personaje> aux,
                          int izquierda, int medio, int derecha, Comparator<Personaje> criterio) {

        // Se copia el tramo a ordenar al arreglo auxiliar.
        for (int i = izquierda; i <= derecha; i++) {
            aux.set(i, lista.get(i));
        }

        int i = izquierda;      // frente de la mitad izquierda
        int j = medio + 1;      // frente de la mitad derecha
        int k = izquierda;      // posicion donde se escribe en la lista real

        while (i <= medio && j <= derecha) {
            comparaciones++;
            if (criterio.compare(aux.get(i), aux.get(j)) <= 0) {
                // "<=" y no "<": esto es lo que hace al algoritmo ESTABLE.
                lista.set(k, aux.get(i));
                i++;
            } else {
                lista.set(k, aux.get(j));
                j++;
            }
            k++;
        }

        // Sobrantes de la mitad izquierda (los de la derecha ya estan en su lugar).
        while (i <= medio) {
            lista.set(k, aux.get(i));
            i++;
            k++;
        }
    }
}
