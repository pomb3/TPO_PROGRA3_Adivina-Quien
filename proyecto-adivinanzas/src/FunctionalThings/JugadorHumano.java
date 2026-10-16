package FunctionalThings;

import Things.Personaje;

/**
 * Representa al jugador humano dentro del motor de juego, con la misma forma
 * que JugadorMaquina representa a una maquina: guarda el secreto propio en
 * una variable privada y solo lo expone a traves de responder()/esElegido(),
 * nunca como un Personaje suelto.
 *
 * Antes de esta clase, ModoHumanoVsMaquinas sostenia el secreto del humano en
 * una variable local (Personaje objetivo) y el arbitro llamaba directamente
 * jugada.getPregunta().cumple(objetivo) para responderle a cada maquina. Eso
 * ya cumplia la regla de la consigna (ni JugadorMaquina ni las estrategias
 * recibian ese Personaje), pero dejaba una asimetria: el humano no era un
 * objeto con su propio comportamiento, como si lo es cada maquina. Con esta
 * clase, ninguna firma de metodo del programa vuelve a tomar el secreto de
 * un jugador como parametro: todo pasa por booleanos.
 */
public class JugadorHumano {

    private final Personaje secretoPropio;

    public JugadorHumano(Personaje secretoPropio) {
        this.secretoPropio = secretoPropio;
    }

    /** Responde una pregunta sobre el propio secreto: solo devuelve si/no. */
    public boolean responder(Pregunta pregunta) {
        return pregunta.cumple(secretoPropio);
    }

    /** Verifica una apuesta contra el propio secreto sin exponer el objeto. */
    public boolean esElegido(Personaje apuesta) {
        return apuesta.getId() == secretoPropio.getId();
    }

    /** Solo para revelar el secreto al terminar la partida. */
    public Personaje revelarSecreto() {
        return secretoPropio;
    }
}
