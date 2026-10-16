package FunctionalThings;

import Interfaz.ModoDeJuego;
import Things.Personaje;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * MODO 2: Maquina vs Maquina, con TODO el proceso a la vista.
 *
 * Las dos maquinas compiten por descubrir el mismo personaje secreto, elegido
 * al azar por el sistema y desconocido para ambas. Es el escenario que permite
 * comparar las dos estrategias en igualdad de condiciones y ver como cada una
 * acorta la busqueda.
 *
 * En cada turno se imprime:
 *   - cuantos candidatos le quedan a cada maquina y cuales son;
 *   - la evaluacion de TODAS las preguntas disponibles con su particion SI/NO
 *     y su peor caso (el calculo greedy completo);
 *   - la pregunta finalmente elegida y por que;
 *   - la respuesta y el nuevo conjunto de candidatos.
 *
 * La Maquina 2 recibe ademas el historial de la Maquina 1 antes de jugar, o sea
 * arranca su turno con el conjunto ya recortado sin haber gastado preguntas.
 */
public class ModoMaquinaVsMaquina implements ModoDeJuego {

    private final Scanner scanner;
    private final GestorPersonaje gestor;
    private final Random random = new Random();

    public ModoMaquinaVsMaquina(Scanner scanner, GestorPersonaje gestor) {
        this.scanner = scanner;
        this.gestor = gestor;
    }

    @Override
    public void jugar() {
        List<Personaje> todos = gestor.getPersonajes();
        Personaje secreto = todos.get(random.nextInt(todos.size()));

        JugadorMaquina m1 = new JugadorMaquina(new EstrategiaCautelosa(), todos);
        JugadorMaquina m2 = new JugadorMaquina(new EstrategiaArriesgada(), todos);

        System.out.println("\n=========== MODO MAQUINA VS MAQUINA ===========");
        System.out.println("Personaje secreto (oculto para las dos maquinas): "
                + secreto.getNombreCompleto());
        System.out.println("Juegan: " + m1.getNombre() + "  vs  " + m2.getNombre());
        System.out.println("Enter para avanzar turno a turno.");
        System.out.println("===============================================");

        int turno = 1;
        ResultadoPartida resultado = null;

        while (resultado == null) {
            System.out.println("\n############### TURNO " + turno + " ###############");
            esperar();

            if (jugarTurno(m1, secreto)) {
                resultado = new ResultadoPartida(m1.getNombre(), turno, secreto, false);
                break;
            }

            esperar();
            int recorte = m2.incorporarHistorialAjeno(m1.getHistorial());
            System.out.println("\n>>> VENTAJA: " + m2.getNombre() + " lee el historial de "
                    + m1.getNombre() + ".");
            System.out.println("    Historial recibido: " + m1.getHistorial().getRegistros());
            System.out.println("    Descarta " + recorte + " candidato(s) sin gastar una sola pregunta.");

            if (jugarTurno(m2, secreto)) {
                resultado = new ResultadoPartida(m2.getNombre(), turno, secreto, false);
                break;
            }

            turno++;
        }

        resultado.imprimir();
        System.out.println(" Preguntas usadas por " + m1.getNombre() + ": " + m1.getHistorial().cantidad());
        System.out.println(" Preguntas usadas por " + m2.getNombre() + ": " + m2.getHistorial().cantidad());
    }

    private boolean jugarTurno(JugadorMaquina maquina, Personaje secreto) {
        System.out.println("\n----------- " + maquina.getNombre() + " -----------");
        Jugada jugada = maquina.pensarJugada();

        System.out.println(" Proceso de decision:");
        if (jugada.getAnalisis().isEmpty()) {
            System.out.println("   (no hizo falta evaluar preguntas)");
        }
        for (String linea : jugada.getAnalisis()) {
            System.out.println("   " + linea);
        }

        if (jugada.esPregunta()) {
            System.out.println(" DECISION: preguntar -> " + jugada.getPregunta().getTexto());
            System.out.println(" MOTIVO: " + jugada.getMotivo());
            boolean respuesta = jugada.getPregunta().cumple(secreto);
            System.out.println(" RESPUESTA: " + (respuesta ? "SI" : "NO"));
            maquina.procesarRespuesta(jugada.getPregunta(), respuesta);
            System.out.println(" Nuevo conjunto (" + maquina.getTablero().cantidadCandidatos()
                    + "): " + maquina.getTablero().resumenCandidatos());
            return false;
        }

        System.out.println(" DECISION: arriesgar -> " + jugada.getApuesta().getNombreCompleto());
        System.out.println(" MOTIVO: " + jugada.getMotivo());
        if (jugada.getApuesta().getId() == secreto.getId()) {
            System.out.println(" RESULTADO: ACERTO.");
            return true;
        }
        System.out.println(" RESULTADO: fallo, descarta ese candidato.");
        maquina.descartar(jugada.getApuesta());
        return false;
    }

    private void esperar() {
        scanner.nextLine();
    }
}
