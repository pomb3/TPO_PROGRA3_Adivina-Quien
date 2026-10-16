package FunctionalThings;

import Things.Personaje;
import java.util.ArrayList;
import java.util.List;

/**
 * Lo que un jugador decide hacer en su turno: preguntar o arriesgar.
 * Ademas lleva el "motivo" y el "analisis" para poder mostrar por pantalla
 * todo el razonamiento de la maquina (requisito del modo Maquina vs Maquina).
 */
public class Jugada {

    public enum Tipo { PREGUNTA, ARRIESGAR }

    private final Tipo tipo;
    private final Pregunta pregunta;
    private final Personaje apuesta;
    private final String motivo;
    private final List<String> analisis;

    private Jugada(Tipo tipo, Pregunta pregunta, Personaje apuesta, String motivo, List<String> analisis) {
        this.tipo = tipo;
        this.pregunta = pregunta;
        this.apuesta = apuesta;
        this.motivo = motivo;
        this.analisis = (analisis == null) ? new ArrayList<String>() : analisis;
    }

    public static Jugada preguntar(Pregunta p, String motivo, List<String> analisis) {
        return new Jugada(Tipo.PREGUNTA, p, null, motivo, analisis);
    }

    public static Jugada arriesgar(Personaje p, String motivo, List<String> analisis) {
        return new Jugada(Tipo.ARRIESGAR, null, p, motivo, analisis);
    }

    public boolean esPregunta() { return tipo == Tipo.PREGUNTA; }
    public Pregunta getPregunta() { return pregunta; }
    public Personaje getApuesta() { return apuesta; }
    public String getMotivo() { return motivo; }
    public List<String> getAnalisis() { return analisis; }
}
