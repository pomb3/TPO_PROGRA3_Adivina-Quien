package FunctionalThings;

import Things.Personaje.ColorCabello;
import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo con las preguntas/filtros que el enunciado habilita:
 * genero, calvicie, lentes y los tres colores de pelo.
 *
 * Son 6 preguntas binarias. Con ellas se pueden distinguir 2*2*2*3 = 24
 * combinaciones distintas de atributos, y el tablero tiene 23 personajes,
 * todos con una combinacion unica. Es decir: SIEMPRE se puede llegar a un
 * unico candidato preguntando, sin necesidad de adivinar al azar.
 */
public class FiltrosBusqueda {

    /** Fabrica una lista nueva de preguntas (patron Factory simple). */
    public static List<Pregunta> catalogo() {
        List<Pregunta> preguntas = new ArrayList<>();

        preguntas.add(new Pregunta("Es varon?",
                new FiltroGenero(true), new FiltroGenero(false)));

        preguntas.add(new Pregunta("Es calvo/a?",
                new FiltroCalvicie(true), new FiltroCalvicie(false)));

        preguntas.add(new Pregunta("Usa lentes?",
                new FiltroLentes(true), new FiltroLentes(false)));

        preguntas.add(new Pregunta("Tiene el pelo colorado?",
                new FiltroColorCabello(ColorCabello.COLORADO, true),
                new FiltroColorCabello(ColorCabello.COLORADO, false)));

        preguntas.add(new Pregunta("Tiene el pelo negro?",
                new FiltroColorCabello(ColorCabello.NEGRO, true),
                new FiltroColorCabello(ColorCabello.NEGRO, false)));

        preguntas.add(new Pregunta("Tiene el pelo amarillo?",
                new FiltroColorCabello(ColorCabello.AMARILLO, true),
                new FiltroColorCabello(ColorCabello.AMARILLO, false)));

        return preguntas;
    }
}
