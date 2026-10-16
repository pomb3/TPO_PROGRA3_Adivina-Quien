package FunctionalThings;

import java.util.List;

/**
 * MAQUINA 2 - "la asertiva".
 *
 * Usa el mismo criterio greedy para elegir pregunta, pero suma una SEGUNDA
 * decision greedy: cuando conviene dejar de preguntar y tirar la apuesta.
 *
 * Criterio (comparacion de turnos esperados):
 *   - Si arriesga ahora, gana en este turno con probabilidad 1/n.
 *   - Si asegura, necesita techo(log2(n)) preguntas mas + 1 turno de apuesta.
 * Con n <= UMBRAL (3) la apuesta tiene al menos ~33% de exito inmediato contra
 * 2 o 3 turnos de espera garantizada, asi que conviene arriesgar.
 *
 * Ademas esta maquina arranca con la ventaja del enunciado: recibe el historial
 * de preguntas de la Maquina 1 y lo aplica a su propio tablero (ver
 * JugadorMaquina.incorporarHistorialAjeno).
 */
public class EstrategiaArriesgada extends EstrategiaBase {

    private static final int UMBRAL_RIESGO = 3;

    @Override
    public String getNombre() {
        return "Maquina 2 (asertiva)";
    }

    @Override
    public Jugada decidir(Tablero tablero, List<Pregunta> preguntas, HistorialPreguntas historial) {

        int n = tablero.cantidadCandidatos();

        if (n == 1) {
            return Jugada.arriesgar(tablero.getCandidatos().get(0),
                    "Queda un unico candidato posible: certeza total.", null);
        }

        if (n <= UMBRAL_RIESGO) {
            int faltarian = preguntasMinimas(n);
            return Jugada.arriesgar(elegirAlAzar(tablero),
                    "Quedan " + n + " candidatos: arriesgar da " + porcentaje(1, n)
                            + " de ganar YA, contra " + faltarian
                            + " pregunta(s) mas + 1 turno si voy a lo seguro. Arriesgo.", null);
        }

        Evaluacion ev = evaluar(tablero, preguntas, historial);

        if (ev.mejor == null) {
            return Jugada.arriesgar(elegirAlAzar(tablero),
                    "Sin preguntas utiles, arriesgo entre " + n
                            + " (probabilidad " + porcentaje(1, n) + ").", ev.analisis);
        }

        return Jugada.preguntar(ev.mejor,
                "Todavia hay " + n + " candidatos (arriesgar seria solo " + porcentaje(1, n)
                        + "). Pregunto la que parte en " + ev.si + "/" + ev.no + ".",
                ev.analisis);
    }

    private String porcentaje(int a, int b) {
        return Math.round((100.0 * a) / b) + "%";
    }
}
