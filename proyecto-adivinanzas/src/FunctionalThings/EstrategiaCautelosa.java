package FunctionalThings;

import java.util.List;

/**
 * MAQUINA 1 - "la prudente".
 *
 * Nunca arriesga hasta estar segura: sigue preguntando mientras haya alguna
 * pregunta que separe a los candidatos, y recien tira su apuesta cuando queda
 * un unico candidato posible (probabilidad de acierto = 100%).
 *
 * Ventaja: no desperdicia turnos en apuestas fallidas.
 * Desventaja: gasta siempre todas las preguntas del camino, aunque tuviera
 * un 50% de chance de ganar un turno antes.
 */
public class EstrategiaCautelosa extends EstrategiaBase {

    @Override
    public String getNombre() {
        return "Maquina 1 (prudente)";
    }

    @Override
    public Jugada decidir(Tablero tablero, List<Pregunta> preguntas, HistorialPreguntas historial) {

        if (tablero.cantidadCandidatos() == 1) {
            return Jugada.arriesgar(tablero.getCandidatos().get(0),
                    "Queda un unico candidato posible: no es riesgo, es certeza.", null);
        }

        Evaluacion ev = evaluar(tablero, preguntas, historial);

        if (ev.mejor == null) {
            // No quedan preguntas utiles: no hay mas remedio que arriesgar.
            int n = tablero.cantidadCandidatos();
            ev.analisis.add("  No quedan preguntas que separen candidatos.");
            return Jugada.arriesgar(elegirAlAzar(tablero),
                    "Sin preguntas utiles disponibles, arriesgo entre " + n
                            + " (probabilidad " + porcentaje(1, n) + ").", ev.analisis);
        }

        return Jugada.preguntar(ev.mejor,
                "Elijo esta porque parte " + tablero.cantidadCandidatos() + " candidatos en "
                        + ev.si + "/" + ev.no + ", el peor caso mas chico (" + ev.peorCaso + ").",
                ev.analisis);
    }

    private String porcentaje(int a, int b) {
        return Math.round((100.0 * a) / b) + "%";
    }
}
