import FunctionalThings.ArbolDecision;
import FunctionalThings.CriteriosOrden;
import FunctionalThings.FiltrosBusqueda;
import FunctionalThings.MergeSortPersonajes;
import FunctionalThings.QuickSortPersonajes;
import FunctionalThings.GestorPersonaje;
import FunctionalThings.MarcadorArchivo;
import FunctionalThings.ModoHumanoVsMaquinas;
import FunctionalThings.ModoMaquinaVsMaquina;
import Interfaz.Marcador;
import Interfaz.ModoDeJuego;
import Interfaz.Ordenador;
import Things.Personaje;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GestorPersonaje gestor = new GestorPersonaje();
        Marcador marcador = new MarcadorArchivo();

        boolean salir = false;
        while (!salir) {
            System.out.println("\n========== JUEGO DE ADIVINANZAS ==========");
            System.out.println(" 1 - Jugar contra las dos maquinas");
            System.out.println(" 2 - Ver una partida Maquina vs Maquina");
            System.out.println(" 3 - Ver el marcador record");
            System.out.println(" 4 - Ver el arbol de decision completo");
            System.out.println(" 5 - Ver el tablero de personajes");
            System.out.println(" 6 - Comparar Merge Sort vs Quick Sort");
            System.out.println(" 0 - Salir");
            System.out.print("Opcion: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1": {
                    ModoDeJuego modo = new ModoHumanoVsMaquinas(scanner, gestor, marcador);
                    modo.jugar();
                    break;
                }
                case "2": {
                    ModoDeJuego modo = new ModoMaquinaVsMaquina(scanner, gestor);
                    modo.jugar();
                    break;
                }
                case "3": {
                    System.out.println("\n--------- MARCADOR RECORD ---------");
                    if (marcador.ranking().isEmpty()) {
                        System.out.println("Todavia no gano nadie.");
                    } else {
                        for (String linea : marcador.ranking()) {
                            System.out.println(linea);
                        }
                    }
                    break;
                }
                case "4": {
                    ArbolDecision arbol = ArbolDecision.construir(
                            gestor.getPersonajes(), FiltrosBusqueda.catalogo());
                    System.out.println("\n--------- ARBOL DE DECISION ---------");
                    arbol.imprimir("", "");
                    System.out.println("Profundidad (peor caso en preguntas): " + arbol.profundidad());
                    break;
                }
                case "5": {
                    gestor.imprimirTablero();
                    break;
                }
                case "6": {
                    demoOrdenamientos(gestor);
                    break;
                }
                case "0": {
                    salir = true;
                    System.out.println("Chau.");
                    break;
                }
                default:
                    System.out.println("Opcion invalida.");
            }
        }
        scanner.close();
    }

    /**
     * Corre los dos algoritmos de ordenamiento sobre la misma lista y compara
     * la cantidad de comparaciones que hace cada uno.
     */
    private static void demoOrdenamientos(GestorPersonaje gestor) {
        List<Personaje> base = new ArrayList<>(gestor.getPersonajes());

        System.out.println("\n--------- ORDENAMIENTO: MERGE SORT vs QUICK SORT ---------");
        System.out.println("Lista de " + base.size() + " personajes.\n");

        // Caso 1: lista desordenada, criterio por genero.
        List<Personaje> a = new ArrayList<>(base);
        List<Personaje> b = new ArrayList<>(base);
        Collections.shuffle(a);
        b.clear();
        b.addAll(a);

        Ordenador merge = new MergeSortPersonajes();
        Ordenador quick = new QuickSortPersonajes();

        merge.ordenar(a, CriteriosOrden.POR_GENERO);
        quick.ordenar(b, CriteriosOrden.POR_GENERO);
        System.out.println("Lista desordenada, criterio POR GENERO:");
        System.out.println("  " + merge.getNombre() + ": " + merge.getComparaciones() + " comparaciones");
        System.out.println("  " + quick.getNombre() + ": " + quick.getComparaciones() + " comparaciones");
        System.out.println("  (n log n teorico ~ " + (int) Math.round(base.size() * (Math.log(base.size()) / Math.log(2))) + ")");

        // Caso 2: lista YA ordenada por id. Es el peor caso clasico del quick sort
        // con pivote fijo; con pivote aleatorio se mantiene en n log n.
        List<Personaje> c = new ArrayList<>(base);
        List<Personaje> d = new ArrayList<>(base);
        merge.ordenar(c, CriteriosOrden.POR_ID);
        quick.ordenar(d, CriteriosOrden.POR_ID);
        System.out.println("\nLista YA ordenada, criterio POR ID:");
        System.out.println("  " + merge.getNombre() + ": " + merge.getComparaciones() + " comparaciones");
        System.out.println("  " + quick.getNombre() + ": " + quick.getComparaciones() + " comparaciones");
        System.out.println("  (con pivote FIJO el quick sort haria " + (base.size() * (base.size() - 1) / 2)
                + " comparaciones: su peor caso O(n^2))");

        // Prueba de estabilidad: se parte de la lista ordenada por id y se ordena
        // por genero. Si el algoritmo es estable, dentro de cada genero los ids
        // tienen que quedar igual de ascendentes que antes.
        System.out.println("\nPrueba de ESTABILIDAD (partiendo de la lista ordenada por id):");
        List<Personaje> e = new ArrayList<>(base);
        merge.ordenar(e, CriteriosOrden.POR_GENERO);
        System.out.println("  Merge Sort -> " + (idsAscendentesPorGrupo(e) ? "ESTABLE" : "no estable")
                + "  " + idsDe(e));
        List<Personaje> f = new ArrayList<>(base);
        quick.ordenar(f, CriteriosOrden.POR_GENERO);
        System.out.println("  Quick Sort -> " + (idsAscendentesPorGrupo(f) ? "ESTABLE" : "NO estable")
                + "  " + idsDe(f));
        System.out.println("\nPor eso el orden inicial por genero se hace con merge sort:");
        System.out.println("el enunciado pide ordenar UNICAMENTE por genero, y solo un algoritmo");
        System.out.println("estable garantiza que no se aplique ningun otro criterio de desempate.");
    }

    /** Verifica que, dentro de cada genero, los ids sigan en orden ascendente. */
    private static boolean idsAscendentesPorGrupo(List<Personaje> lista) {
        int ultimoId = -1;
        boolean generoActual = lista.get(0).esMasculino();
        for (Personaje p : lista) {
            if (p.esMasculino() != generoActual) {
                generoActual = p.esMasculino();
                ultimoId = -1;
            }
            if (p.getId() < ultimoId) return false;
            ultimoId = p.getId();
        }
        return true;
    }

    private static String idsDe(List<Personaje> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(lista.get(i).getId());
        }
        return sb.append("]").toString();
    }
}
