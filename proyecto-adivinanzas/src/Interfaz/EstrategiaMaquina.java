package Interfaz;

import FunctionalThings.HistorialPreguntas;
import FunctionalThings.Jugada;
import FunctionalThings.Pregunta;
import FunctionalThings.Tablero;
import java.util.List;

/**
 * Las dos maquinas comparten el mismo motor y solo cambian de estrategia.
 */
public interface EstrategiaMaquina {
    String getNombre();
    Jugada decidir(Tablero tablero, List<Pregunta> preguntas, HistorialPreguntas historial);
}
