package FunctionalThings;

import java.util.ArrayList;
import java.util.List;

/**
 * Guarda las preguntas que ya se hicieron sobre un objetivo, con su respuesta.
 *
 * Sirve para dos cosas:
 *  1) que una maquina no repita una pregunta que ya hizo;
 *  2) para la ventaja del enunciado: la Maquina 2 recibe el historial de la
 *     Maquina 1 y aplica de una todas esas respuestas a su propio tablero.
 */
public class HistorialPreguntas {

    /** Un par pregunta-respuesta. */
    public static class Registro {
        private final Pregunta pregunta;
        private final boolean respuesta;

        public Registro(Pregunta pregunta, boolean respuesta) {
            this.pregunta = pregunta;
            this.respuesta = respuesta;
        }

        public Pregunta getPregunta() { return pregunta; }
        public boolean getRespuesta() { return respuesta; }

        @Override
        public String toString() {
            return pregunta.getTexto() + " -> " + (respuesta ? "SI" : "NO");
        }
    }

    private final List<Registro> registros = new ArrayList<>();

    public void registrar(Pregunta p, boolean respuesta) {
        registros.add(new Registro(p, respuesta));
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public boolean yaSePregunto(Pregunta p) {
        for (Registro r : registros) {
            if (r.getPregunta() == p || r.getPregunta().getTexto().equals(p.getTexto())) {
                return true;
            }
        }
        return false;
    }

    public int cantidad() {
        return registros.size();
    }
}
