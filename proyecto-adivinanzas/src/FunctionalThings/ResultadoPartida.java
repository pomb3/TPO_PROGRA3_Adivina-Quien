package FunctionalThings;

import Things.Personaje;

/** Resumen de como termino una partida. */
public class ResultadoPartida {

    private final String ganador;
    private final int turnos;
    private final Personaje objetivo;
    private final boolean ganoElHumano;

    public ResultadoPartida(String ganador, int turnos, Personaje objetivo, boolean ganoElHumano) {
        this.ganador = ganador;
        this.turnos = turnos;
        this.objetivo = objetivo;
        this.ganoElHumano = ganoElHumano;
    }

    public String getGanador() { return ganador; }
    public int getTurnos() { return turnos; }
    public Personaje getObjetivo() { return objetivo; }
    public boolean ganoElHumano() { return ganoElHumano; }

    public void imprimir() {
        System.out.println();
        System.out.println("======================= FIN DE LA PARTIDA =======================");
        System.out.println(" Ganador: " + ganador);
        System.out.println(" Turnos jugados: " + turnos);
        if (objetivo != null) {
            System.out.println(" El personaje buscado era: " + objetivo.getNombreCompleto());
        }
        System.out.println("================================================================");
    }
}
