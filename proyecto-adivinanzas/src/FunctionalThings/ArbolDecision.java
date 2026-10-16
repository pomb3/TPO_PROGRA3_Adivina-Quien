package FunctionalThings;

import Interfaz.Filtro;
import Things.Personaje;
import java.util.ArrayList;
import java.util.List;

/**
 * ARBOL DE DECISION - divide & conquer explicito.
 *
 * Construccion recursiva:
 *   1) Si queda 1 solo candidato -> es una hoja, la respuesta.
 *   2) Si no, se elige (greedy) la pregunta que mejor parte el conjunto.
 *   3) Se DIVIDE el conjunto en dos subconjuntos disjuntos (SI / NO).
 *   4) Se CONQUISTA cada subconjunto resolviendolo con el mismo procedimiento.
 *   5) La combinacion es trivial: cada rama ya es un subarbol terminado.
 *
 * Este arbol es exactamente la estrategia de la Maquina 1, pero materializada
 * de antemano. Durante la partida las maquinas recalculan la mejor pregunta
 * turno a turno (el resultado es el mismo camino, pero sin gastar memoria
 * guardando el arbol entero y adaptandose si el rival ya descarto candidatos).
 *
 * Se usa en el menu como demostracion de la logica y para medir la profundidad,
 * que es la cota de preguntas en el peor caso.
 */
public class ArbolDecision {

    private Pregunta pregunta;
    private ArbolDecision ramaSi;
    private ArbolDecision ramaNo;
    private Personaje hoja;
    private List<Personaje> empatados;

    private ArbolDecision() { }

    public static ArbolDecision construir(List<Personaje> candidatos, List<Pregunta> preguntas) {
        ArbolDecision nodo = new ArbolDecision();

        // CASO BASE 1: un solo candidato.
        if (candidatos.size() == 1) {
            nodo.hoja = candidatos.get(0);
            return nodo;
        }

        // Eleccion greedy: la pregunta con el peor caso mas chico.
        Pregunta mejor = null;
        int mejorPeor = Integer.MAX_VALUE;
        for (Pregunta p : preguntas) {
            int si = p.contarSi(candidatos);
            int no = candidatos.size() - si;
            if (si == 0 || no == 0) continue;
            int peor = Math.max(si, no);
            if (peor < mejorPeor) {
                mejorPeor = peor;
                mejor = p;
            }
        }

        // CASO BASE 2: ninguna pregunta los separa (personajes indistinguibles).
        if (mejor == null) {
            nodo.empatados = new ArrayList<>(candidatos);
            return nodo;
        }

        // DIVIDIR
        Filtro fSi = mejor.filtroSegun(true);
        Filtro fNo = mejor.filtroSegun(false);
        List<Personaje> subSi = fSi.aplicar(candidatos);
        List<Personaje> subNo = fNo.aplicar(candidatos);

        List<Pregunta> restantes = new ArrayList<>(preguntas);
        restantes.remove(mejor);

        // CONQUISTAR
        nodo.pregunta = mejor;
        nodo.ramaSi = construir(subSi, restantes);
        nodo.ramaNo = construir(subNo, restantes);
        return nodo;
    }

    public int profundidad() {
        if (pregunta == null) return 0;
        return 1 + Math.max(ramaSi.profundidad(), ramaNo.profundidad());
    }

    public void imprimir(String sangria, String etiqueta) {
        if (hoja != null) {
            System.out.println(sangria + etiqueta + "=> " + hoja.getNombreCompleto() + " (id " + hoja.getId() + ")");
            return;
        }
        if (empatados != null) {
            System.out.println(sangria + etiqueta + "=> indistinguibles: " + empatados.size() + " candidatos");
            return;
        }
        System.out.println(sangria + etiqueta + pregunta.getTexto());
        ramaSi.imprimir(sangria + "   ", "SI  ");
        ramaNo.imprimir(sangria + "   ", "NO  ");
    }
}
