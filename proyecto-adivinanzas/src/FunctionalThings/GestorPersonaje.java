package FunctionalThings;

import Interfaz.Ordenador;
import Things.Personaje;
import Things.Personaje.ColorCabello;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Crea y administra los 23 personajes.
 *
 * El enunciado pide que los personajes "empiecen ordenados unicamente segun su
 * genero" y que sea la maquina la que los disponga en una lista con id
 * autoincremental. Por eso se cargan desordenados, despues se ordenan por
 * genero con un MERGE SORT propio (ver MergeSortPersonajes) y recien ahi se les
 * asigna el id 1..23 de forma incremental.
 *
 * Se usa merge sort y no quick sort para este paso porque el merge sort es
 * ESTABLE: garantiza que el unico criterio aplicado sea el genero y que no se
 * cuele ningun desempate no pedido. Ademas su O(n log n) esta garantizado.
 *
 * Los 23 personajes tienen combinaciones de atributos unicas entre si.
 */
public class GestorPersonaje {

    private final List<Personaje> personajes;
    private final Ordenador ordenadorInicial = new MergeSortPersonajes();
    private final Ordenador ordenadorPorId = new QuickSortPersonajes();

    public GestorPersonaje() {
        this.personajes = crearPersonajes();
    }

    private List<Personaje> crearPersonajes() {
        List<Personaje> lista = new ArrayList<>();

        // --- 12 varones: todas las combinaciones de calvo x lentes x color ---
        lista.add(new Personaje("Bruno",  "Ferrari",  true,  true,  true,  ColorCabello.COLORADO));
        lista.add(new Personaje("Hugo",   "Salas",    true,  true,  true,  ColorCabello.NEGRO));
        lista.add(new Personaje("Ivan",   "Moreno",   true,  true,  true,  ColorCabello.AMARILLO));
        lista.add(new Personaje("Julian", "Rios",     true,  true,  false, ColorCabello.COLORADO));
        lista.add(new Personaje("Kevin",  "Pardo",    true,  true,  false, ColorCabello.NEGRO));
        lista.add(new Personaje("Leo",    "Duarte",   true,  true,  false, ColorCabello.AMARILLO));
        lista.add(new Personaje("Martin", "Vega",     true,  false, true,  ColorCabello.COLORADO));
        lista.add(new Personaje("Nahuel", "Costa",    true,  false, true,  ColorCabello.NEGRO));
        lista.add(new Personaje("Omar",   "Bianchi",  true,  false, true,  ColorCabello.AMARILLO));
        lista.add(new Personaje("Pablo",  "Sosa",     true,  false, false, ColorCabello.COLORADO));
        lista.add(new Personaje("Ramiro", "Ponce",    true,  false, false, ColorCabello.NEGRO));
        lista.add(new Personaje("Simon",  "Vera",     true,  false, false, ColorCabello.AMARILLO));

        // --- 11 mujeres: las mismas combinaciones menos (calva + lentes + colorado) ---
        lista.add(new Personaje("Alma",   "Rivas",    false, true,  true,  ColorCabello.NEGRO));
        lista.add(new Personaje("Bianca", "Ortiz",    false, true,  true,  ColorCabello.AMARILLO));
        lista.add(new Personaje("Carla",  "Mendez",   false, true,  false, ColorCabello.COLORADO));
        lista.add(new Personaje("Delia",  "Ruiz",     false, true,  false, ColorCabello.NEGRO));
        lista.add(new Personaje("Elena",  "Paz",      false, true,  false, ColorCabello.AMARILLO));
        lista.add(new Personaje("Flor",   "Aguirre",  false, false, true,  ColorCabello.COLORADO));
        lista.add(new Personaje("Gala",   "Ibarra",   false, false, true,  ColorCabello.NEGRO));
        lista.add(new Personaje("Irene",  "Lauro",    false, false, true,  ColorCabello.AMARILLO));
        lista.add(new Personaje("Julia",  "Nieves",   false, false, false, ColorCabello.COLORADO));
        lista.add(new Personaje("Lara",   "Quiroga",  false, false, false, ColorCabello.NEGRO));
        lista.add(new Personaje("Mora",   "Toledo",   false, false, false, ColorCabello.AMARILLO));

        // Se desordenan a proposito para que se note que el orden lo impone la maquina.
        Collections.shuffle(lista);

        // Paso 1: ordenar UNICAMENTE por genero (varones primero), con merge sort propio.
        ordenadorInicial.ordenar(lista, CriteriosOrden.POR_GENERO);

        // Paso 2: id autoincremental segun la posicion final.
        int contadorId = 1;
        for (Personaje p : lista) {
            p.setId(contadorId);
            contadorId++;
        }

        return lista;
    }

    public List<Personaje> getPersonajes() {
        return personajes;
    }

    public int cantidad() {
        return personajes.size();
    }

    /** Busqueda por id. Como la lista esta indexada 1..n, es acceso directo. */
    public Personaje buscarPersonajePorID(int id) {
        if (id < 1 || id > personajes.size()) {
            return null;
        }
        return personajes.get(id - 1);
    }

    /**
     * Reordena por id una lista cualquiera de personajes usando el quick sort
     * propio. Se usa para mostrar los candidatos de un tablero siempre en el
     * mismo orden, aunque hayan pasado por varios filtros.
     */
    public void ordenarPorId(List<Personaje> lista) {
        ordenadorPorId.ordenar(lista, CriteriosOrden.POR_ID);
    }

    public Ordenador getOrdenadorInicial() { return ordenadorInicial; }
    public Ordenador getOrdenadorPorId() { return ordenadorPorId; }

    public void imprimirTablero() {
        System.out.println("---------------- TABLERO (23 personajes) ----------------");
        for (Personaje p : personajes) {
            System.out.println(p.descripcion());
        }
        System.out.println("---------------------------------------------------------");
    }
}
