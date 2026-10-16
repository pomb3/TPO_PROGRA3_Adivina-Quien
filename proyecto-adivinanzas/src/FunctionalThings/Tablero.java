package FunctionalThings;

import Interfaz.Filtro;
import Interfaz.Ordenador;
import Things.Personaje;
import java.util.ArrayList;
import java.util.List;

/**
 * El tablero personal de un jugador: la lista de candidatos que todavia
 * siguen en pie. Cada jugador (humano y cada maquina) tiene el suyo.
 */
public class Tablero {

    private List<Personaje> candidatos;

    /**
     * Los filtros devuelven listas nuevas y las apuestas fallidas sacan
     * elementos del medio, asi que antes de mostrar los candidatos se los
     * reordena por id con el quick sort propio. Asi el jugador siempre ve el
     * tablero en el mismo orden.
     */
    private final Ordenador ordenador = new QuickSortPersonajes();

    public Tablero(List<Personaje> listaOriginal) {
        this.candidatos = new ArrayList<>(listaOriginal);
    }

    /** Reordena los candidatos por id (quick sort). */
    public void ordenarPorId() {
        ordenador.ordenar(candidatos, CriteriosOrden.POR_ID);
    }

    /** Aplica un filtro: los que no lo pasan quedan descartados. */
    public void aplicarFiltro(Filtro f) {
        this.candidatos = f.aplicar(this.candidatos);
    }

    /** Saca un candidato puntual (por ejemplo, uno que ya se arriesgo y fallo). */
    public void descartar(Personaje p) {
        candidatos.remove(p);
    }

    public List<Personaje> getCandidatos() {
        return candidatos;
    }

    public int cantidadCandidatos() {
        return candidatos.size();
    }

    public void imprimirCandidatos() {
        ordenarPorId();
        System.out.println("  Candidatos en pie (" + candidatos.size() + "):");
        for (Personaje p : candidatos) {
            System.out.println("   " + p.descripcion());
        }
    }

    public String resumenCandidatos() {
        ordenarPorId();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < candidatos.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(candidatos.get(i).getNombre());
        }
        return sb.toString();
    }
}
