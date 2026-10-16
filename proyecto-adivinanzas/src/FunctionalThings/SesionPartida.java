package FunctionalThings;

/**
 * Puntaje de una PARTIDA, entendida como una serie de RONDAS.
 *
 * Cada ronda es una partida completa del motor existente (elegir secreto,
 * jugar hasta que alguien acierte). La SesionPartida solo lleva la cuenta de
 * cuantas rondas gano el humano contra cuantas ganaron las maquinas (juntas,
 * sin distinguir Maquina 1 de Maquina 2) y decide cuando termina la partida.
 *
 * Se mantiene deliberadamente simple y sin dependencias de Swing ni de
 * consola: es un modelo de datos puro, facil de probar aislado.
 */
public class SesionPartida {

    /** Cuantas rondas hacen falta ganar para llevarse la partida. */
    public static final int RONDAS_PARA_GANAR = 3;

    private final String nombreJugador;
    private int victoriasHumano;
    private int victoriasMaquinas;

    public SesionPartida(String nombreJugador) {
        this(nombreJugador, 0, 0);
    }

    public SesionPartida(String nombreJugador, int victoriasHumano, int victoriasMaquinas) {
        this.nombreJugador = nombreJugador;
        this.victoriasHumano = victoriasHumano;
        this.victoriasMaquinas = victoriasMaquinas;
    }

    public String getNombreJugador() { return nombreJugador; }
    public int getVictoriasHumano() { return victoriasHumano; }
    public int getVictoriasMaquinas() { return victoriasMaquinas; }

    /** Registra el resultado de una ronda recien terminada. */
    public void registrarResultadoRonda(boolean ganoElHumano) {
        if (ganoElHumano) {
            victoriasHumano++;
        } else {
            victoriasMaquinas++;
        }
    }

    public boolean partidaTerminada() {
        return victoriasHumano >= RONDAS_PARA_GANAR || victoriasMaquinas >= RONDAS_PARA_GANAR;
    }

    public boolean ganoElHumanoLaPartida() {
        return victoriasHumano >= RONDAS_PARA_GANAR;
    }

    public String marcadorTexto() {
        return nombreJugador + " " + victoriasHumano + " - " + victoriasMaquinas + " Maquinas";
    }
}
