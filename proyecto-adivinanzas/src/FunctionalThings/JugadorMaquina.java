package FunctionalThings;

import Interfaz.EstrategiaMaquina;
import Things.Personaje;
import java.util.List;

/**
 * Un jugador controlado por la computadora.
 *
 * Importante: la maquina NO tiene acceso al personaje secreto del rival.
 * Lo unico que recibe del exterior es un true/false como respuesta a sus
 * preguntas. Toda su informacion vive en su propio Tablero e Historial.
 */
public class JugadorMaquina {

    private final EstrategiaMaquina estrategia;
    private final Tablero tablero;
    private final List<Pregunta> preguntas;
    private final HistorialPreguntas historial = new HistorialPreguntas();

    private Personaje secretoPropio;

    public JugadorMaquina(EstrategiaMaquina estrategia, List<Personaje> todos) {
        this.estrategia = estrategia;
        this.tablero = new Tablero(todos);
        this.preguntas = FiltrosBusqueda.catalogo();
    }

    public String getNombre() { return estrategia.getNombre(); }
    public Tablero getTablero() { return tablero; }
    public HistorialPreguntas getHistorial() { return historial; }

    public void setSecretoPropio(Personaje p) { this.secretoPropio = p; }
    public Personaje getSecretoPropio() { return secretoPropio; }

    /** Le pide a la estrategia que decida el movimiento del turno. */
    public Jugada pensarJugada() {
        return estrategia.decidir(tablero, preguntas, historial);
    }

    /** Guarda la respuesta y achica el tablero propio. */
    public void procesarRespuesta(Pregunta p, boolean respuesta) {
        historial.registrar(p, respuesta);
        tablero.aplicarFiltro(p.filtroSegun(respuesta));
    }

    public void descartar(Personaje p) {
        tablero.descartar(p);
    }

    /** Responde una pregunta sobre el propio secreto: solo devuelve si/no. */
    public boolean responder(Pregunta pregunta) {
        return pregunta.cumple(secretoPropio);
    }

    /** Verifica una apuesta contra el propio secreto sin exponer el objeto. */
    public boolean esElegido(Personaje apuesta) {
        return apuesta.getId() == secretoPropio.getId();
    }

    /**
     * VENTAJA DE LA MAQUINA 2 (enunciado): recibe el historial de preguntas y
     * respuestas de la Maquina 1 sobre el mismo objetivo y lo aplica de una
     * sola vez a su tablero, arrancando con el conjunto ya reducido.
     */
    public int incorporarHistorialAjeno(HistorialPreguntas ajeno) {
        int antes = tablero.cantidadCandidatos();
        for (HistorialPreguntas.Registro r : ajeno.getRegistros()) {
            if (!historial.yaSePregunto(r.getPregunta())) {
                historial.registrar(r.getPregunta(), r.getRespuesta());
                tablero.aplicarFiltro(r.getPregunta().filtroSegun(r.getRespuesta()));
            }
        }
        return antes - tablero.cantidadCandidatos();
    }
}
