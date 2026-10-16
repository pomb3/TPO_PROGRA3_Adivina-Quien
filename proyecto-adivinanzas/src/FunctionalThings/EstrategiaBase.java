package FunctionalThings;

import Interfaz.EstrategiaMaquina;
import Things.Personaje;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Parte comun a las dos maquinas: el criterio GREEDY para elegir pregunta.
 *
 * ALGORITMO GREEDY
 * ----------------
 * En cada turno se evalua cada pregunta disponible y se cuenta como partiria
 * al conjunto actual de candidatos: cuantos caen del lado SI y cuantos del NO.
 * Se elige la pregunta que minimiza el PEOR CASO, o sea min( max(|SI|,|NO|) ).
 *
 * Es greedy porque toma la mejor decision LOCAL del turno (la que mas reduce
 * el conjunto ahora) sin explorar el arbol completo de secuencias de preguntas.
 * Calcular el arbol optimo global seria exponencial; este criterio corre en
 * O(cantidad de preguntas * cantidad de candidatos) por turno y en la practica
 * alcanza la profundidad optima o queda a una pregunta de ella.
 *
 * Cada subclase decide APARTE cuando conviene dejar de preguntar y arriesgar.
 * Eso es lo que hace que una maquina sea mas asertiva que la otra.
 */
public abstract class EstrategiaBase implements EstrategiaMaquina {

    protected final Random random = new Random();

    /** Resultado de la evaluacion greedy de un turno. */
    protected static class Evaluacion {
        Pregunta mejor;
        int si;
        int no;
        int peorCaso;
        List<String> analisis = new ArrayList<>();
    }

    protected Evaluacion evaluar(Tablero tablero, List<Pregunta> preguntas, HistorialPreguntas historial) {
        Evaluacion ev = new Evaluacion();
        ev.peorCaso = Integer.MAX_VALUE;

        List<Personaje> candidatos = tablero.getCandidatos();
        int total = candidatos.size();

        ev.analisis.add("Candidatos actuales: " + total + " -> " + tablero.resumenCandidatos());

        for (Pregunta p : preguntas) {
            if (historial.yaSePregunto(p)) {
                ev.analisis.add("  [" + p.getTexto() + "] ya preguntada, se saltea.");
                continue;
            }

            int si = p.contarSi(candidatos);
            int no = total - si;
            int peor = Math.max(si, no);

            if (si == 0 || no == 0) {
                ev.analisis.add("  [" + p.getTexto() + "] particion " + si + "/" + no
                        + " -> no aporta informacion, se descarta.");
                continue;
            }

            ev.analisis.add("  [" + p.getTexto() + "] particion SI=" + si + " NO=" + no
                    + " -> peor caso " + peor);

            if (peor < ev.peorCaso) {
                ev.peorCaso = peor;
                ev.mejor = p;
                ev.si = si;
                ev.no = no;
            }
        }
        return ev;
    }

    /** Elige un candidato al azar entre los que quedan (cuando toca arriesgar). */
    protected Personaje elegirAlAzar(Tablero tablero) {
        List<Personaje> c = tablero.getCandidatos();
        return c.get(random.nextInt(c.size()));
    }

    /** Cuantas preguntas hacen falta, en el mejor de los casos, para aislar n candidatos. */
    protected int preguntasMinimas(int n) {
        int preguntas = 0;
        int restantes = n;
        while (restantes > 1) {
            restantes = (restantes + 1) / 2;
            preguntas++;
        }
        return preguntas;
    }
}
